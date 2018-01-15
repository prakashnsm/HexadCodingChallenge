package com.prakashnsm.apps.ecs.impl;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prakashnsm.apps.ecs.Elevator;
import com.prakashnsm.apps.ecs.enums.ElevatorDirection;
import com.prakashnsm.apps.ecs.enums.ElevatorState;
import com.prakashnsm.apps.ecs.exception.RequestException;
import com.prakashnsm.apps.ecs.exception.ServiceException;

/**
 * @author prakashnsm
 * Date: 14-Jan-2018
 * Time: 12:26:17 pm
 * 
 */
public class ElevatorImpl implements Elevator {
	private Logger log = LoggerFactory.getLogger(ElevatorImpl.class);
	
	private int id;
	private int currentFloor = 0;
	private NavigableSet<Integer> destinationFloors;
	private NavigableSet<Integer> upWardDestinationFloors;
	private NavigableSet<Integer> downWardDestinationFloors;
	private NavigableMap<Integer, ElevatorDirection> requestedDirections;
	private ElevatorDirection direction;
	private ElevatorState state;

	private boolean exit = false;
	
	public ElevatorImpl(int id){
		this.setId(id);
		this.setState(ElevatorState.IDLE);
		this.destinationFloors = new ConcurrentSkipListSet<Integer>();
		this.upWardDestinationFloors = new ConcurrentSkipListSet<Integer>();
		this.downWardDestinationFloors  = new ConcurrentSkipListSet<Integer>();
		requestedDirections = new TreeMap<>();
		
		log.debug("Elevator["+id+"] is instantiated ");
	}
	
	@Override
	public void moveUp() {
		/*this.setState(ElevatorState.MOVING_UP);
		this.setDirection(ElevatorDirection.UP);
		log.info("Elevator[" + this.id + "] | current floor - " + 
				currentFloor + " | next move - " +  getState().getName());
		*/
		setStateAndDirection(this.currentFloor+1);
		this.currentFloor++;
	}

	@Override
	public void moveDown() {
		/*this.setState(ElevatorState.MOVING_DOWN);
		this.setDirection(ElevatorDirection.DOWN); 
		log.info("Elevator[" + this.id + "] | current floor - " + 
    			currentFloor + " | next move - " +  getState().getName());
    	*/
		setStateAndDirection(this.currentFloor-1);
		this.currentFloor--;
	}

	@Override
	public void addNewDestinatoin(Integer destination, ElevatorDirection direction) throws ServiceException, RequestException {
		log.info("Elevator[" +this.id + "] | new request to " + destination 
				+" floor | current floor - " + currentFloor);
		if(currentFloor > ElevatorManagerImpl.MAX_FLOOR_LIMIT || currentFloor < ElevatorManagerImpl.MIN_FLOOR_LIMIT) 
		{
			throw new RequestException("destination floor should be between " 
					+ ElevatorManagerImpl.MIN_FLOOR_LIMIT
					+ " to "+ ElevatorManagerImpl.MAX_FLOOR_LIMIT);
		}
		
		if( this.destinationFloors.size() <=0 || 
				(this.direction == ElevatorDirection.UP && destination > currentFloor) ||
						(this.direction == ElevatorDirection.DOWN && destination < currentFloor)){
			setStateAndDirection(destination);
			this.destinationFloors.add(destination);
			this.requestedDirections.put(destination, direction);
			
			if(this.direction == ElevatorDirection.UP && destination > currentFloor){
				this.upWardDestinationFloors.add(destination);
			}
			
			if(this.direction == ElevatorDirection.DOWN && destination < currentFloor){
				this.downWardDestinationFloors.add(destination);
			}
		}else{
			throw new ServiceException("Unable to service for "+ destination + " floor ");
		}
	}
	
	private void setStateAndDirection(int destination){
		if(destination > currentFloor){
    		this.setState(ElevatorState.MOVING_UP);
    		this.setDirection(ElevatorDirection.UP);
    		log.info("Elevator[" + this.id + "] | current floor - " + 
        			currentFloor + " | next move - " +  getState().getName());
    	}else if(destination < currentFloor){
    		this.setState(ElevatorState.MOVING_DOWN);
    		this.setDirection(ElevatorDirection.DOWN);
    		log.info("Elevator[" + this.id + "] | current floor - " + 
        			currentFloor + " | next move - " +  getState().getName());
    	}else if(destination == currentFloor){
    		this.setState(ElevatorState.IDLE);
    		//this.setDirection(ElevatorDirection.HOLD);
    	}
	}
	
    public void move(){
        synchronized (ElevatorManagerImpl.getInstance()){
        	int target = this.getDestValue(false);
        	if(ElevatorState.IDLE == this.getState()) log.info("Elevator[" + this.id + "] | current floor - " + 
        			currentFloor + " | Current State - " +  getState().getName());
        	if(target > currentFloor){
        		this.moveUp();
        	}else if(target < currentFloor){
        		this.moveDown();
        	}else if(target == currentFloor){
        		this.setState(ElevatorState.IDLE);
        		//this.setDirection(ElevatorDirection.HOLD);
        		int dest = this.getDestValue(true);
        		
        		log.info("Elevator[" + this.id + "] | current floor - " + 
            			currentFloor + " destination reached " +  dest);
        	}
        	ElevatorManagerImpl.updateElevatorLists(this);
        }
	}
    
    private int getDestValue(boolean poll){
    	if((this.getState() == ElevatorState.MOVING_UP 
    			|| this.getState() == ElevatorState.IDLE) && this.direction == ElevatorDirection.UP){
    		if(this.requestedDirections.firstEntry().getValue() == ElevatorDirection.UP )
    			return poll? this.destinationFloors.pollFirst() : this.destinationFloors.first();
    		else
    			return poll? this.destinationFloors.pollLast() : this.destinationFloors.last();
    	}
    	else if((this.getState() == ElevatorState.MOVING_DOWN
    			|| this.getState() == ElevatorState.IDLE) && this.direction == ElevatorDirection.DOWN){
    		if(this.requestedDirections.firstEntry().getValue() == ElevatorDirection.DOWN )
    			return poll? this.destinationFloors.pollLast() : this.destinationFloors.last();
    		else
    			return poll? this.destinationFloors.pollFirst() : this.destinationFloors.first();
    	}
    	return 0;
    }

	@Override
	public void run() {
		log.debug("Elevator["+ this.id +"] is started ");
		
		while(!exit){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}

			if(destinationFloors.size() > 0){
				this.move();
			}else if(this.state != ElevatorState.IDLE){
				this.setState(ElevatorState.IDLE);
			}
		}
	}

	@Override
	public void setExit(boolean exit){
		this.exit = exit;
	};

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) throws RequestException {
		if(currentFloor > ElevatorManagerImpl.MAX_FLOOR_LIMIT || currentFloor < ElevatorManagerImpl.MIN_FLOOR_LIMIT) 
		{
			throw new RequestException("destination floor should be between " 
					+ ElevatorManagerImpl.MIN_FLOOR_LIMIT
					+ " to "+ ElevatorManagerImpl.MAX_FLOOR_LIMIT);
		}
		this.currentFloor = currentFloor;
	}

	public NavigableSet<Integer> getDestinationFloors() {
		return destinationFloors;
	}

	public void setDestinationFloors(NavigableSet<Integer> destinationFloors) {
		this.destinationFloors = destinationFloors;
	}

	@Override
	public ElevatorState getState() {
		return state;
	}

	public void setState(ElevatorState state) {
		this.state = state;
	}

	public ElevatorDirection getDirection() {
		return direction;
	}

	public void setDirection(ElevatorDirection direction) {
		this.direction = direction;
	}

}
