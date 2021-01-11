package com.sw.note.model.entity;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

public class Comment {
    @Id
    private Integer id;
    private Integer thread;
    private String comment;
    private Date commentTime;
    private Date editTime;
    private String ip;

    @Transient
    private int operable;

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public String getComment() {
        return comment;
    }

    @SuppressWarnings("unused")
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
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

    public int getOperable() {
        return operable;
    }

    public void setOperable(int operable) {
        this.operable = operable;
    }
}
