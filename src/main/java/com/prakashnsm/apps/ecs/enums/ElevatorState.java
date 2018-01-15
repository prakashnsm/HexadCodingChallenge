package com.prakashnsm.apps.ecs.enums;

/**
 * @author prakashnsm
 * Date: 14-Jan-2018
 * Time: 12:12:21 pm
 * 
 */
public enum ElevatorState {
	MAINTAINANCE(1, "MAINTAINANCE"),
	MOVING_DOWN(2, "MOVING_DOWN"),
	MOVING_UP(3, "MOVING_UP"),
	WAITING(4, "WAITING"),
	IDLE(5, "IDLE");
    
	private int id;
	private String name;
	
    ElevatorState(int id, String name){
    	this.setId(id);
    	this.name = name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
