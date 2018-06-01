package com.getomnify.hackernews.fragments;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getomnify.hackernews.R;
import com.getomnify.hackernews.activities.MainActivity;
import com.getomnify.hackernews.adapters.StoryRecyclerAdapter;
import com.getomnify.hackernews.realm.Story;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoriesFragment extends Fragment {

    public static final String TAG = "StoriesFragment";

    /*Core Fields*/
    private Context context;
    private MainActivity activity;
    private View rootView;
    private Realm realm;

    /*Views*/
    private RecyclerView storiesRecycler;
    private SwipeRefreshLayout swipeRefresh;

    public StoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        activity = (MainActivity) getActivity();
        rootView = view;
        realm = Realm.getDefaultInstance();

        storiesRecycler = view.findViewById(R.id.fragment_stories_recycler);
        swipeRefresh = view.findViewById(R.id.fragment_stories_swipe_refresh);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        storiesRecycler.setLayoutManager(llm);


        long lastSync = PreferenceManager.getDefaultSharedPreferences(context).getLong("last_sync", 0);
        if (lastSync > 0)
            activity.actionBar.setSubtitle("Last updated - " + new SimpleDateFormat("h:mmaa d/MM/yy", Locale.getDefault()).format(new Date(lastSync)));
        else activity.actionBar.setSubtitle("Top Stories");

        fetchTopStories();


        storiesRecycler.setAdapter(new StoryRecyclerAdapter(realm.where(Story.class).findAll()));


        //Setting a change listener for all the stories that are in db
        realm.where(Story.class).findAll().addChangeListener(new RealmChangeListener<RealmResults<Story>>() {
            @Override
            public void onChange(@NonNull RealmResults<Story> stories) {
                if (!stories.isEmpty())
                    if (stories.size() % 10 == 0) {
                        swipeRefresh.setRefreshing(false);
                        storiesRecycler.swapAdapter(new StoryRecyclerAdapter(stories), true);
                    }
            }
        });


        //When refresh action is called, fetchStories again.
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTopStories();
                storiesRecycler.swapAdapter(new StoryRecyclerAdapter(realm.where(Story.class).findAll()), true);
            }
        });
    }


    void fetchTopStories()
    {
        if (context != null) {
            final RequestQueue queue = Volley.newRequestQueue(context);
            final String topStoriesURL = getString(R.string.base_url) + "topstories.json";

            Log.d(TAG, "fetchTopStories: fetching...");

            JsonArrayRequest topStoriesReq = new JsonArrayRequest(Request.Method.GET, topStoriesURL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response != null) {
                                swipeRefresh.setRefreshing(false);
                                Log.d(TAG, "onResponse: top stories count:" + response.length());
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putLong("last_sync", Calendar.getInstance().getTimeInMillis()).apply();
                                String baseURL = getString(R.string.base_url);
                                JsonObjectRequest downloadStoryReq;
                                String url;
                                final Story story = new Story();
                                for (int i = 0; i < response.length(); i++) {

                                    final long storyID = response.optLong(i);

                                    url = baseURL + "item/" + storyID + ".json";

                                    downloadStoryReq = new JsonObjectRequest(Request.Method.GET, url,
                                            null, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(final JSONObject response) {

                                            if (response.optString("type").equals("story")) {

                                                realm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(@NonNull Realm realm) {

                                                        story.setId(response.optLong("id"));
                                                        story.setSubmitter(response.optString("by"));
                                                        story.setCommentCount(response.optInt("descendants"));
                                                        story.setVotes(response.optInt("score"));
                                                        story.setTitle(response.optString("title"));
                                                        story.setTime(new Date(response.optLong("time") * 1000));
                                                        story.setUrl(response.optString("url"));

                                                        if (response.optJSONArray("kids") != null)
                                                            story.setKidsJSONArray(response.optJSONArray("kids").toString());

                                                        realm.copyToRealmOrUpdate(story);
                                                    }
                                                });
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e(TAG, error.getMessage());
                                        }
                                    });

                                    // Add the request to the RequestQueue.
                                    queue.add(downloadStoryReq);
                                }
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.getMessage());

                }
            });

            queue.add(topStoriesReq);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Preventing memory leaks
        realm.removeAllChangeListeners();
        if (!realm.isClosed())
            realm.close();
        activity.actionBar.setSubtitle("");
    }
}
