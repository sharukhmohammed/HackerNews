package com.getomnify.hackernews.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getomnify.hackernews.R;
import com.getomnify.hackernews.helpers.Utils;
import com.getomnify.hackernews.realm.Comment;

import org.json.JSONArray;
import org.json.JSONException;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Sharukh Mohammed on 31/05/18 at 17:40.
 */
public class StoryCommentsAdapter extends RecyclerView.Adapter<StoryCommentsAdapter.Holder> {

    private Context context;
    private final RealmResults<Comment> comments;

    public StoryCommentsAdapter(RealmResults<Comment> comments)
    {
        this.comments = comments;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        Comment comment = comments.get(position);
        if (comment != null) {
            holder.submitter.setText(comment.getUserName());
            holder.text.setText(Html.fromHtml(comment.getText()));
            holder.time.setText(Utils.df.format(comment.getTime()));

            RealmList<Comment> replies = comment.getReplies();

            if (replies != null) {

                LinearLayoutManager llm = new LinearLayoutManager(context);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                holder.repliesRecycler.setLayoutManager(llm);

                holder.repliesRecycler.setAdapter(new StoryCommentsAdapter(replies.where().findAll()));
            }
            else Log.i("Comments","No replies");
        }

    }

    @Override
    public int getItemCount() {
        if(comments==null)
        return 0;
        else return comments.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RecyclerView repliesRecycler;
        TextView submitter, text, time;

        Holder(View itemView) {
            super(itemView);
            submitter = itemView.findViewById(R.id.item_comment_submitter);
            text = itemView.findViewById(R.id.item_comment_text);
            time = itemView.findViewById(R.id.item_comment_time);
            repliesRecycler = itemView.findViewById(R.id.item_comment_reply_recycler);

        }
    }
}
