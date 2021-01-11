package com.sw.vote.model;

import com.alibaba.fastjson.JSON;

public class CtrlCode {
    private String identity;
    private String code;

    public CtrlCode() {
    }

    public CtrlCode(String identity, String code) {
        this.identity = identity;
        this.code = code;
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
