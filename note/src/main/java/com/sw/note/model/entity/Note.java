package com.sw.note.model.entity;

import javax.persistence.*;
import java.util.Date;

public class Note {
    @Id
    private Integer id;
    private String poster;
    private String title;
    private String content;
    private Date postTime;
    private Date editTime;
    private String ip;
    private String summary;
    private String tag;
    private Integer recommend;


    @SuppressWarnings("unused")
    public Note() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    @SuppressWarnings("unused")
    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSummary() {
        return summary;
    }

    @SuppressWarnings("unused")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTag() {
        return tag;
    }

    @SuppressWarnings("unused")
    public void setTag(String tag) {
        this.tag = tag;
    }

    @SuppressWarnings("unused")
    public Integer getRecommend() {
        return recommend;
    }

    @SuppressWarnings("unused")
    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }
}
