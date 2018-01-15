package com.prakashnsm.apps.ecs;

import com.prakashnsm.apps.ecs.model.PickupRequest;

/**
 * @author prakashnsm
 * Date: 14-Jan-2018
 * Time: 11:49:04 am
 * 
 */
public interface ElevatorManager extends Runnable {
	
	public boolean isStopManager();

	public void setStopManager(boolean stopManager);
	
	public Elevator pickUp(PickupRequest pickupRequest);
}
