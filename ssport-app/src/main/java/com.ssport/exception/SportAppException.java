package com.ssport.exception;

public class SportAppException extends RuntimeException {

    private ErrorCode code;

    private String description;

    public SportAppException(ErrorCode code, String description) {
        this.code = code;
        this.description = description;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
