package com.sw.vote.model.entity;

public class ClientData {
    private String id;
    private String date;
    private String reward;
    private String detail;
    private String ip;
    private String location;
    private String userId;
    private Integer sortNo;


    public ClientData() {
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public Integer getSortNo() {
        return sortNo;
    }

    @SuppressWarnings("unused")
    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
