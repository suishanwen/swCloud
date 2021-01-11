package com.sw.vote.model;


import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    public static final String LIMIT_EXCEEDED = "LIMIT_EXCEEDED";
    public static final String SERVICE_UNRELIABLE = "SERVICE_UNRELIABLE";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String SERVER_ERROR = "SERVER_ERROR";
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2332608236621015980L;

    private HttpStatus httpStatus;
    private String code;

    public BusinessException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }


    public BusinessException(HttpStatus httpStatus, String code, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
