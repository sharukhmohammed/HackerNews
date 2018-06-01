package com.getomnify.hackernews.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getomnify.hackernews.R;
import com.getomnify.hackernews.activities.MainActivity;
import com.getomnify.hackernews.adapters.StoryCommentsAdapter;
import com.getomnify.hackernews.realm.Comment;
import com.getomnify.hackernews.realm.Story;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoryCommentsFragment extends Fragment {

    public static final String TAG = "StoriesFragment";

    /*Core Fields*/
    private MainActivity activity;
    private View rootView;
    private Realm realm;
    private Story story;
    private RequestQueue queue;

    /*Views*/
    private RecyclerView commentsRecycler;

    public StoryCommentsFragment() {
        // Required empty public constructor
    }

    public static StoryCommentsFragment newInstance(long storyID) {
        Bundle args = new Bundle();
        args.putLong("storyID", storyID);
        StoryCommentsFragment fragment = new StoryCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity();
        rootView = view;
        realm = Realm.getDefaultInstance();


        Bundle args = getArguments();
        if (args != null) {
            long storyID = args.getLong("storyID", -1);
            if (storyID != -1) {

                story = realm.where(Story.class)
                        .equalTo("id", storyID)
                        .findFirst();

                if (story != null) {
                    fetchComments();

                    commentsRecycler = view.findViewById(R.id.fragment_story_comments_recycler);
                    LinearLayoutManager llm = new LinearLayoutManager(activity);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);


                    RealmResults<Comment> storyComments = realm
                            .where(Comment.class)
                            .equalTo("parentID", storyID)
                            .findAll();


                    StoryCommentsAdapter adapter = new StoryCommentsAdapter(storyComments);

                    commentsRecycler.setAdapter(adapter);


                    paintComments();

                    realm.where(Comment.class)
                            .findAll().addChangeListener(new RealmChangeListener<RealmResults<Comment>>() {
                        @Override
                        public void onChange(@NonNull RealmResults<Comment> comments) {
                            paintComments();
                        }
                    });

                }
            } else Log.e(TAG, "No Story ID found");
        } else Log.e(TAG, "No Args found");

    }

    void paintComments()
    {
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        commentsRecycler.setLayoutManager(llm);

        RealmResults<Comment> storyComments = realm
                .where(Comment.class)
                .equalTo("parentID", story.getId())
                .findAll();


        StoryCommentsAdapter adapter = new StoryCommentsAdapter(storyComments);

        commentsRecycler.swapAdapter(adapter, false);
    }

    private void fetchComments() {


        queue = Volley.newRequestQueue(activity);
        try {
            final JSONArray commentIDs = new JSONArray(story.getKidsJSONArray());

            JsonObjectRequest commentRequest;
            long commentID;
            String commentURL;
            for (int i = 0; i < commentIDs.length(); i++) {
                commentID = commentIDs.optLong(i);
                commentURL = getString(R.string.base_url) + "item/" + commentID + ".json";


                commentRequest = new JsonObjectRequest(Request.Method.GET, commentURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        if (response != null) {
                            if (response.optString("type").equals("comment")) {
                                final Comment comment = new Comment();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(@NonNull Realm realm) {

                                        comment.setUserName(response.optString("by"));
                                        comment.setId(response.optLong("id"));
                                        if (response.has("kids"))
                                            comment.setKidsJSONArray(response.optJSONArray("kids").toString());
                                        comment.setParentID(response.optLong("parent"));
                                        comment.setText(response.optString("text"));
                                        comment.setTime(new Date(response.optLong("time") * 1000));


                                        Story parentStory = realm.where(Story.class)
                                                .equalTo("id", response.optLong("parent"))
                                                .findFirst();

                                        if (parentStory != null) {

                                            RealmResults<Comment> comments = realm.where(Comment.class)
                                                    .equalTo("parentID", parentStory.getId())
                                                    .findAll();

                                            RealmList<Comment> results = new RealmList<>();
                                            results.addAll(comments.subList(0, comments.size()));
                                            parentStory.setComments(results);

                                        } else Log.e(TAG, "Parent Story not found");


                                        realm.copyToRealmOrUpdate(comment);
                                        realm.copyToRealmOrUpdate(story);

                                        fetchChildComments(response);

                                        paintComments();
                                    }
                                });

                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(commentRequest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void fetchChildComments(final JSONObject response) {

        if (response.has("kids") && activity != null) {
            try {
                final JSONArray commentIDs = new JSONArray(story.getKidsJSONArray());

                JsonObjectRequest commentRequest;
                long commentID;
                String commentURL;
                for (int i = 0; i < commentIDs.length(); i++) {
                    commentID = commentIDs.optLong(i);
                    commentURL = getString(R.string.base_url) + "item/" + commentID + ".json";

                    commentRequest = new JsonObjectRequest(Request.Method.GET, commentURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            if (response != null) {
                                if (response.optString("type").equals("comment")) {
                                    final Comment comment = new Comment();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(@NonNull Realm realm) {

                                            comment.setUserName(response.optString("by"));
                                            comment.setId(response.optLong("id"));
                                            if (response.has("kids"))
                                                comment.setKidsJSONArray(response.optJSONArray("kids").toString());
                                            comment.setParentID(response.optLong("parent"));
                                            comment.setText(response.optString("text"));
                                            comment.setTime(new Date(response.optLong("time") * 1000));


                                            Comment parentComment = realm.where(Comment.class)
                                                    .equalTo("id", response.optLong("parent"))
                                                    .findFirst();

                                            if (parentComment != null) {

                                                RealmResults<Comment> replies = realm.where(Comment.class)
                                                        .equalTo("parentID", parentComment.getId())
                                                        .findAll();

                                                RealmList<Comment> results = new RealmList<>();
                                                results.addAll(replies.subList(0, replies.size()));
                                                parentComment.setReplies(results);

                                                Log.i(TAG, parentComment.getId() + "  Setting replies>" + results.size());

                                            } else Log.e(TAG, "Parent Comment not found");


                                            realm.copyToRealmOrUpdate(comment);
                                            fetchChildComments(response);

                                            paintComments();
                                        }
                                    });

                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    if (queue.getSequenceNumber() < 50)
                        queue.add(commentRequest);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        queue.stop();
        //queue.cancelAll(null);
    }
}
