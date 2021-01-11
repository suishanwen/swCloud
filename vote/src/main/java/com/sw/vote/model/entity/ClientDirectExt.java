package com.sw.vote.model.entity;

import javax.persistence.Id;
import javax.persistence.Transient;

public class ClientDirectExt {

    @Id
    private String id;
    private String userId;
    private int sortNo;
    private String area;
    private String remoteIp;
    private String adslUser;
    private String adslPwd;
    private String openingTime;
    private String expireTime;

    @Transient
    private String location;
    @Transient
    private String instance;
    @Transient
    private String resetId;

    public ClientDirectExt() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getAdslUser() {
        return adslUser;
    }

    public void setAdslUser(String adslUser) {
        this.adslUser = adslUser;
    }

    public String getAdslPwd() {
        return adslPwd;
    }

    public void setAdslPwd(String adslPwd) {
        this.adslPwd = adslPwd;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getResetId() {
        return resetId;
    }

    public void setResetId(String resetId) {
        this.resetId = resetId;
    }
}
