package com.kwanhor.trace.server.error;

public class RestException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public RestException(String msg) {
		super(msg);
	}
}
