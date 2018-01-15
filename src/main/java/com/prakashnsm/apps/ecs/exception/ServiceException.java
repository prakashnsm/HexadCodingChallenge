package com.prakashnsm.apps.ecs.exception;

public class ServiceException extends Exception {

	private static final long serialVersionUID = -4837339771084948901L;
	
	public ServiceException() {
		super();
	}
	
	public ServiceException(String message) {
		super(message);
	}
	
	public ServiceException(String message, Throwable t) {
		super(message, t);
	}
	
}
