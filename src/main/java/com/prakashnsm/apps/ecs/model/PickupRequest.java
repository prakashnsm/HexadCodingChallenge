package com.prakashnsm.apps.ecs.model;

import com.prakashnsm.apps.ecs.enums.ElevatorDirection;


/**
 * @author prakashnsm
 * Date: 14-Jan-2018
 * Time: 2:24:22 pm
 * 
 */
public class PickupRequest extends Request {

	private ElevatorDirection direction;
	
	public PickupRequest(int requestedFloor, ElevatorDirection direction){
		this.requestedFloor = requestedFloor;
		this.direction = direction;
	}
	
	public ElevatorDirection getDirection() {
		return direction;
	}
	public void setDirection(ElevatorDirection direction) {
		this.direction = direction;
	}
	
	
}
