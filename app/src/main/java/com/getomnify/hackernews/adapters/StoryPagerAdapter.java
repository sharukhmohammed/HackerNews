package com.getomnify.hackernews.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.getomnify.hackernews.fragments.StoryArticleFragment;
import com.getomnify.hackernews.fragments.StoryCommentsFragment;
import com.getomnify.hackernews.realm.Story;

import java.util.Locale;

/**
 * Created by Sharukh Mohammed on 31/05/18 at 15:45.
 */
public class StoryPagerAdapter extends FragmentPagerAdapter {
    private Story story;

    public StoryPagerAdapter(FragmentManager fm, Story story) {
        super(fm);
        this.story = story;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return StoryCommentsFragment.newInstance(story.getId());
            case 1:
                return StoryArticleFragment.newInstance(story.getId());
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return String.format(Locale.ENGLISH, "Comments (%d)", story.getCommentCount());
            case 1:
                if (story.getUrl() != null)
                    if (!story.getUrl().isEmpty())
                        return "Article";
            default:
                return super.getPageTitle(position);

        }
    }
}
