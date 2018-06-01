package com.getomnify.hackernews.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sharukh Mohammed on 29/05/18 at 16:55.
 */
public class Comment extends RealmObject {

    @PrimaryKey
    private long id;

    private String userName;
    private long parentID;
    private String text;
    private Date time;
    private String kidsJSONArray;


    private RealmList<Comment> replies;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getParentID() {
        return parentID;
    }

    public void setParentID(long parentID) {
        this.parentID = parentID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public RealmList<Comment> getReplies() {
        return replies;
    }

    public void setReplies(RealmList<Comment> replies) {
        this.replies = replies;
    }

    public String getKidsJSONArray() {
        return kidsJSONArray;
    }

    public void setKidsJSONArray(String kidsJSONArray) {
        this.kidsJSONArray = kidsJSONArray;
    }
}
