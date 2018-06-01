package com.getomnify.hackernews.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getomnify.hackernews.R;
import com.getomnify.hackernews.activities.MainActivity;
import com.getomnify.hackernews.fragments.StoryTabsFragment;
import com.getomnify.hackernews.realm.Story;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmResults;

/**
 * Created by Sharukh Mohammed on 29/05/18 at 17:25.
 */
public class StoryRecyclerAdapter extends RecyclerView.Adapter<StoryRecyclerAdapter.StoryHolder> {

    private Context context;
    private RealmResults<Story> stories;

    public StoryRecyclerAdapter(RealmResults<Story> stories)
    {
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryRecyclerAdapter.StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new StoryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoryRecyclerAdapter.StoryHolder holder, int position) {

        final Story story = stories.get(position);
        if (story != null) {
            holder.votes.setText(String.valueOf(story.getVotes()));
            holder.title.setText(story.getTitle());
            holder.userName.setText(story.getSubmitter());
            holder.comments.setText(String.valueOf(story.getCommentCount()));
            holder.time.setText(new SimpleDateFormat("h:mmaa d-MMM", Locale.ENGLISH).format(story.getTime()));

            ((View) holder.title.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).replaceFragment(StoryTabsFragment.newInstance(story.getId()), StoryTabsFragment.TAG);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (stories == null)
            return 0;
        else return stories.size();
    }

    class StoryHolder extends RecyclerView.ViewHolder {

        TextView votes, title, userName, comments, time;


        StoryHolder(View itemView) {
            super(itemView);

            votes = itemView.findViewById(R.id.item_story_votes);
            title = itemView.findViewById(R.id.item_story_title);
            userName = itemView.findViewById(R.id.item_story_submitter);
            comments = itemView.findViewById(R.id.item_story_comments);
            time = itemView.findViewById(R.id.item_story_time);
        }
    }
}
