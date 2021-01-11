package com.sw.vote.model.entity;

import javax.persistence.Id;

public class CtrlClient {
    @Id
    private String identity;
    private String uname;
    private Integer startNum;
    private Integer endNum;
    private String workerId;
    private Integer workerInput;
    private Integer tail;
    private Integer timeout;
    private Integer autoVote;
    private Integer overAuto;
    private String user;
    private Integer sort;
    private Integer state;

    public CtrlClient() {
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @SuppressWarnings("unused")
    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Integer getStartNum() {
        return startNum;
    }

    @SuppressWarnings("unused")
    public void setStartNum(Integer startNum) {
        this.startNum = startNum;
    }

    @SuppressWarnings("unused")
    public Integer getEndNum() {
        return endNum;
    }

    public void setEndNum(Integer endNum) {
        this.endNum = endNum;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public Integer getWorkerInput() {
        return workerInput;
    }

    @SuppressWarnings("unused")
    public void setWorkerInput(Integer workerInput) {
        this.workerInput = workerInput;
    }

    public Integer getTail() {
        return tail;
    }

    @SuppressWarnings("unused")
    public void setTail(Integer tail) {
        this.tail = tail;
    }

    public Integer getTimeout() {
        return timeout;
    }

    @SuppressWarnings("unused")
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getAutoVote() {
        return autoVote;
    }

    @SuppressWarnings("unused")
    public void setAutoVote(Integer autoVote) {
        this.autoVote = autoVote;
    }

    public Integer getOverAuto() {
        return overAuto;
    }

    public void setOverAuto(Integer overAuto) {
        this.overAuto = overAuto;
    }

    @SuppressWarnings("unused")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
