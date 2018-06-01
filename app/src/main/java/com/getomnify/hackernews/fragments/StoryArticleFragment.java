package com.getomnify.hackernews.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.getomnify.hackernews.R;
import com.getomnify.hackernews.activities.MainActivity;
import com.getomnify.hackernews.realm.Story;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoryArticleFragment extends Fragment {

    /*Core*/
    private Context context;
    private MainActivity activity;
    private View rootView;
    private Realm realm;
    private Story story;

    /*Views*/
    private WebView webView;

    public StoryArticleFragment() {
        // Required empty public constructor
    }

    public static StoryArticleFragment newInstance(long storyID) {
        Bundle args = new Bundle();
        args.putLong("storyID", storyID);
        StoryArticleFragment fragment = new StoryArticleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story_article, container, false);
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

                if (story != null)
                {

                    webView = view.findViewById(R.id.fragment_story_article_webView);

                    webView.loadUrl(story.getUrl());

                }

            }
        }

    }
}
