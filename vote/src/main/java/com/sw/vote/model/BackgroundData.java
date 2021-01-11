package com.sw.vote.model;

import java.util.List;

public class BackgroundData {
    private List<String> onlineIdList;
    private long mills;

    @SuppressWarnings("unused")
    public BackgroundData() {
    }

    public BackgroundData(List<String> onlineIdList, long mills) {
        this.onlineIdList = onlineIdList;
        this.mills = mills;
    }

    public List<String> getOnlineIdList() {
        return onlineIdList;
    }

    @SuppressWarnings("unused")
    public void setOnlineIdList(List<String> onlineIdList) {
        this.onlineIdList = onlineIdList;
    }

    public long getMills() {
        return mills;
    }

    public void setMills(long mills) {
        this.mills = mills;
    }
}
