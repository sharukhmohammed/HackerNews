package com.getomnify.hackernews.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sharukh Mohammed on 29/05/18 at 14:44.
 */
public class Story extends RealmObject {

    @PrimaryKey
    private long id;

    private int commentCount;
    private String title;
    private Date time;

    private String url;
    private String submitter;
    private int votes;

    private String kidsJSONArray;
    private RealmList<Comment> comments;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public RealmList<Comment> getComments() {
        return comments;
    }

    public void setComments(RealmList<Comment> comments) {
        this.comments = comments;
    }

    public String getKidsJSONArray() {
        return kidsJSONArray;
    }

    public void setKidsJSONArray(String kidsJSONArray) {
        this.kidsJSONArray = kidsJSONArray;
    }
}
