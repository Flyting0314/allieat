package com.dona.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DonaException extends RuntimeException{

	public DonaException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DonaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public DonaException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public DonaException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public DonaException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
