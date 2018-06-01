package com.getomnify.hackernews.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getomnify.hackernews.R;
import com.getomnify.hackernews.activities.MainActivity;
import com.getomnify.hackernews.adapters.StoryPagerAdapter;
import com.getomnify.hackernews.helpers.Utils;
import com.getomnify.hackernews.realm.Story;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoryTabsFragment extends Fragment {
    public static final String TAG = "StoryTabsFragment";

    /*Core Fields*/
    private Context context;
    private MainActivity activity;
    private View rootView;
    private Realm realm;
    private Story story;

    /*Views*/
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public StoryTabsFragment() {
        // Required empty public constructor
    }

    public static StoryTabsFragment newInstance(long storyID) {
        Bundle args = new Bundle();
        args.putLong("storyID", storyID);
        StoryTabsFragment fragment = new StoryTabsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_story_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
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

                    ((TextView) view.findViewById(R.id.fragment_story_title)).setText(story.getTitle());
                    ((TextView) view.findViewById(R.id.fragment_story_submitter)).setText(String.format("by %s", story.getSubmitter()));
                    ((TextView) view.findViewById(R.id.fragment_story_url)).setText(story.getUrl());
                    ((TextView) view.findViewById(R.id.fragment_story_time)).setText(Utils.df.format(story.getTime()));

                    viewPager = view.findViewById(R.id.fragment_story_viewPager);
                    StoryPagerAdapter viewPagerAdapter = new StoryPagerAdapter(getChildFragmentManager(), story);
                    viewPager.setAdapter(viewPagerAdapter);
                    tabLayout = view.findViewById(R.id.fragment_story_tabLayout);
                    tabLayout.setupWithViewPager(viewPager);

                }
            } else Log.e(TAG, "No Story ID found");
        } else Log.e(TAG, "No Args found");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
