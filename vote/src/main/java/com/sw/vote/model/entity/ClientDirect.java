package com.sw.vote.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class ClientDirect {

    @Id
    private String id;
    private String userId;
    private int sortNo;
    private String instance;
    private String resetId;
    private String projectName;
    private String workerId;
    private String success;
    private String reward;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String direct;
    private String version;
    private String restart;

    @Transient
    private boolean $synchronized = true;
    @Transient
    private String $sid = "";

    public final static String splitter = "-->";

    public ClientDirect() {
    }

    public ClientDirect(String id, String userId, int sortNo) {
        this.id = id;
        this.userId = userId;
        this.sortNo = sortNo;
    }


    public boolean diff(Map<Object, Object> map) {
        return !workerId.equals(map.get("workerId")) ||
                !projectName.equals(map.get("projectName")) ||
                !success.equals(map.get("success")) ||
                !reward.equals(map.get("reward"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDirect that = (ClientDirect) o;
        return workerId.equals(that.workerId) &&
                projectName.equals(that.projectName) &&
                success.equals(that.success) &&
                reward.equals(that.reward);
    }


    @Override
    public int hashCode() {
        return Objects.hash(workerId, projectName, success, reward);
    }

    @Override
    public String toString() {
        return "ClientDirect{" +
                "workerId='" + workerId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", success='" + success + '\'' +
                ", reward='" + reward + '\'' +
                '}';
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

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReward() {
        return reward;
    }

    @SuppressWarnings("unused")
    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRestart() {
        return restart;
    }

    public void setRestart(String restart) {
        this.restart = restart;
    }

    public boolean is$synchronized() {
        return $synchronized;
    }

    public void set$synchronized(boolean $synchronized) {
        this.$synchronized = $synchronized;
    }

    public String getResetId() {
        return resetId;
    }

    public void setResetId(String resetId) {
        this.resetId = resetId;
    }

    public String get$sid() {
        return $sid;
    }

    public void set$sid(String $sid) {
        this.$sid = $sid;
    }
}
