package com.ssport.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

@JsonSerialize(using = CustomClientAuthenticationExceptionSerializer.class)
public class CustomClientAuthenticationException extends ClientAuthenticationException {

    private final ErrorCode code;

    public CustomClientAuthenticationException(String msg, Throwable t, ErrorCode code) {
        super(msg, t);
        this.code = code;
    }

    public CustomClientAuthenticationException(String msg, ErrorCode code) {
        super(msg);
        this.code = code;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return null;
    }

    public ErrorCode getCode() {
        return code;
    }

    public int getHttpErrorCode() {
        return 401;
    }
}
