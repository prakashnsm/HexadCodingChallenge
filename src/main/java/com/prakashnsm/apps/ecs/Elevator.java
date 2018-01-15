package com.prakashnsm.apps.ecs;

import com.prakashnsm.apps.ecs.enums.ElevatorDirection;
import com.prakashnsm.apps.ecs.enums.ElevatorState;
import com.prakashnsm.apps.ecs.exception.RequestException;
import com.prakashnsm.apps.ecs.exception.ServiceException;

/**
 * @author prakashnsm
 * Date: 14-Jan-2018
 * Time: 11:48:32 am
 * 
 */
public interface Elevator extends Runnable {
	  public void moveUp();
	  public void moveDown();
	  public void addNewDestinatoin(Integer destination, ElevatorDirection direction) throws ServiceException, RequestException;
	  public void setExit(boolean exit);
	  public void setCurrentFloor(int currentFloor) throws RequestException;
	  
	  public int getCurrentFloor();
	  public int getId();
	  public ElevatorState getState();
	  public ElevatorDirection getDirection();
	  
	  
}
