package com.sw.vote.model.entity;

import javax.persistence.Id;

public class BugReport {

    @Id
    private String id;
    private String clientId;
    private String msg;

    public BugReport() {
    }

    public BugReport(String clientId, String msg) {
        this.clientId = clientId;
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMsg() {
        return msg;
    }

    @SuppressWarnings("unused")
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
