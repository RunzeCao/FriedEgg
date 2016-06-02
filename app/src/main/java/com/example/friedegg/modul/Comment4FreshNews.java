package com.example.friedegg.modul;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CRZ on 2016/5/30 16:45.
 * Comment4FreshNews
 */
public class Comment4FreshNews extends Commentator implements Comparable, Commentable {
    //评论列表
    public static final String URL_COMMENTS = "http://jandan.net/?oxwlxojflwblxbsapi=get_post&include=comments&id=";
    //对新鲜事发表评论
    public static final String URL_PUSH_COMMENT = "http://jandan.net/?oxwlxojflwblxbsapi=respond.submit_comment";

    private int id;
    private String url;
    private String date;
    private String content;
    private String parent;
    private int parentId;
    private ArrayList<Comment4FreshNews> parentComments;
    private int vote_positive;

    public Comment4FreshNews() {
    }

    public static String getUrlComments(String id) {
        return URL_COMMENTS + id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public ArrayList<Comment4FreshNews> getParentComments() {
        return parentComments;
    }

    public void setParentComments(ArrayList<Comment4FreshNews> parentComments) {
        this.parentComments = parentComments;
    }

    public int getVote_positive() {
        return vote_positive;
    }

    public void setVote_positive(int vote_positive) {
        this.vote_positive = vote_positive;
    }


    @Override
    public int compareTo(Object another) {
        String anotherTimeString = ((Comment4FreshNews) another).getDate();
        String thisTimeString = getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date anotherDate = simpleDateFormat.parse(anotherTimeString);
            Date thisDate = simpleDateFormat.parse(thisTimeString);
            return -thisDate.compareTo(anotherDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }
    @Override
    public int getCommentFloorNum() {
        return getFloorNum();
    }

    @Override
    public String getCommentContent() {
        return getContent();
    }

    @Override
    public String getAuthorName() {
        return getName();
    }
}
