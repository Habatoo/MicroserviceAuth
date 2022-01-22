package com.ssport.exception;

import java.io.Serializable;


public class SportAppErrorResponse implements Serializable {

	private static final long serialVersionUID = 236664211L;

	private int code;

	private String description;

	public SportAppErrorResponse() {
	}

	public SportAppErrorResponse(Exception e) {
		this.code = ErrorCode.UNEXPECTED_ERROR.getHttpStatus().value();
		this.description = e.getMessage();
	}

	public SportAppErrorResponse(Exception e, ErrorCode code) {
		this.code = code.getCode();
		this.description = e.getMessage();
	}

	public SportAppErrorResponse(ErrorCode code, String message) {
		this.code = code.getCode();
		this.description = message;
	}
	
	public SportAppErrorResponse(SportAppException e) {
		this.code = e.getCode().getHttpStatus().value();
		this.description = e.getDescription();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
