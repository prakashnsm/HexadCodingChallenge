package com.prakashnsm.apps.ecs.exception;

public class RequestException extends Exception {

	private static final long serialVersionUID = -4837339771084948901L;
	
	public RequestException() {
		super();
	}
	
	public RequestException(String message) {
		super(message);
	}
	
	public RequestException(String message, Throwable t) {
		super(message, t);
	}
	
}
