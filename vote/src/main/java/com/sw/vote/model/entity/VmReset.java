package com.sw.vote.model.entity;

import javax.persistence.Id;
import java.util.Date;

public class VmReset {

    @Id
    private String id;
    private String user;
    private String sortNo;
    private String status;
    private Date date;

    public VmReset() {
    }

    public VmReset(String user, String sortNo, String status, Date date) {
        this.user = user;
        this.sortNo = sortNo;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
