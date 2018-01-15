package com.prakashnsm.apps.ecs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prakashnsm.apps.ecs.Elevator;
import com.prakashnsm.apps.ecs.ElevatorManager;
import com.prakashnsm.apps.ecs.enums.ElevatorDirection;
import com.prakashnsm.apps.ecs.enums.ElevatorState;
import com.prakashnsm.apps.ecs.exception.RequestException;
import com.prakashnsm.apps.ecs.exception.ServiceException;
import com.prakashnsm.apps.ecs.model.PickupRequest;

/**
 * @author prakashnsm
 * Date: 14-Jan-2018
 * Time: 12:26:09 pm
 * 
 */
public class ElevatorManagerImpl implements ElevatorManager {
	private Logger log = LoggerFactory.getLogger(ElevatorManagerImpl.class);
	
	private boolean stopManager;
	
	private Queue<PickupRequest> pickupRequests = new LinkedList<PickupRequest>();
	private static List<Elevator> elevatorList = new ArrayList<Elevator>();
	
    private static Map<Integer, Elevator> upMovingMap = new HashMap<Integer, Elevator>();
    private static Map<Integer, Elevator> downMovingMap = new HashMap<Integer, Elevator>();
	
	private static ElevatorManagerImpl instance;
	
	private static int MAX_ELEVATOR_LIMIT = 5;
	public static int MAX_FLOOR_LIMIT = 10;
	public static int MIN_FLOOR_LIMIT = 0;
	
	private ElevatorManagerImpl() {
	}
	
	public static ElevatorManagerImpl getInstance(){
		return getInstance(MAX_ELEVATOR_LIMIT);
	}
	
	public static ElevatorManagerImpl getInstance(int noOfElevators){
		if(instance == null){
			instance = new ElevatorManagerImpl();
			MAX_ELEVATOR_LIMIT = noOfElevators <= MAX_ELEVATOR_LIMIT ? noOfElevators : MAX_ELEVATOR_LIMIT;
			initializeElevators(MAX_ELEVATOR_LIMIT);
		}
		return instance;
	}
	
	private static void initializeElevators(int noOfElevators){
		for(int i=0; i<noOfElevators; i++){
            Elevator elevator = new ElevatorImpl(i);
            Thread t = new Thread(elevator);
            t.start();
            
            elevatorList.add(elevator);
            ElevatorManagerImpl.updateElevatorLists(elevator);
        }
	}
	
	public synchronized Elevator pickUp(PickupRequest pickupRequest){
		pickupRequests.add(pickupRequest);
		return pickNearestElevator(pickupRequests.poll());
	}
	
	private synchronized Elevator pickNearestElevator(PickupRequest pickupRequest){
		int requestFloor = pickupRequest.getRequestedFloor();
		Integer bestFit = null;
		Double closest = Math.floor(Math.random() * elevatorList.size());
		int i = 0;
		for (Elevator elevator : elevatorList) {
			int currFloor = elevator.getCurrentFloor();
			int distance = Math.abs(requestFloor - currFloor);
			
			if((elevator.getState().equals(ElevatorState.MOVING_UP) 
					&&  currFloor <= requestFloor)
					|| (elevator.getState().equals(ElevatorState.MOVING_DOWN)
							&& currFloor >= requestFloor)
							|| elevator.getState().equals(ElevatorState.IDLE)){
				// Checks if the same direction
				if((pickupRequest.getDirection() == elevator.getDirection() 
						|| elevator.getState().equals(ElevatorState.IDLE))
						&& (bestFit == null || distance < Math.abs(requestFloor - 
								elevatorList.get(bestFit).getCurrentFloor()))){
					bestFit = i;
				}
			}
			
			if(distance < Math.abs(requestFloor - 
					elevatorList.get(closest.intValue()).getCurrentFloor())){
				closest = new Double(i);
			}
			
			if(bestFit == null) bestFit = closest.intValue();
			
			i++;
		}
		try {
			elevatorList.get(bestFit).addNewDestinatoin(requestFloor, pickupRequest.getDirection());
		} catch (ServiceException | RequestException e) {
		}
		return elevatorList.get(bestFit);
		
/*		
		Elevator  bestFitElevator =  null;
		if(pickupRequest.getDirection() == ElevatorDirection.UP){
			for (int id : upMovingMap.keySet()) {
				Elevator elevator = upMovingMap.get(id);
				if(checkBestFit(bestFitElevator, elevator, pickupRequest)){
					bestFitElevator = elevator;
				}
			}
		}
		else if(pickupRequest.getDirection() == ElevatorDirection.DOWN){
			for (int id : downMovingMap.keySet()) {
				Elevator elevator = downMovingMap.get(id);
				if(checkBestFit(bestFitElevator, elevator, pickupRequest)){
					bestFitElevator = elevator;
				}
			}
		}
		try {
			bestFitElevator.addNewDestinatoin(pickupRequest.getRequestedFloor(), pickupRequest.getDirection());
		} catch (ServiceException | RequestException e) {
		}
		return bestFitElevator;
*/
		
	}
	
	private boolean checkBestFit(Elevator bestFitElevator, Elevator elevator, PickupRequest pickupRequest){
		int currFloor = elevator.getCurrentFloor();
		int requestFloor = pickupRequest.getRequestedFloor();
		int distance = Math.abs(requestFloor - currFloor);
		
		return (elevator.getState().equals(ElevatorState.MOVING_UP) 
				&&  currFloor <= requestFloor)
				|| (elevator.getState().equals(ElevatorState.MOVING_DOWN)
					&& currFloor >= requestFloor)
					|| elevator.getState().equals(ElevatorState.IDLE)
					&& (pickupRequest.getDirection() == elevator.getDirection() 
					|| elevator.getState().equals(ElevatorState.IDLE))
					&& (bestFitElevator == null || distance < Math.abs(requestFloor - 
							bestFitElevator.getCurrentFloor()));
	}
	
    public static synchronized void updateElevatorLists(Elevator elevator){
        if(elevator.getState().equals(ElevatorState.MOVING_UP)){
            upMovingMap.put(elevator.getId(), elevator);
            downMovingMap.remove(elevator.getId());
        } else if(elevator.getState().equals(ElevatorState.MOVING_DOWN)){
            downMovingMap.put(elevator.getId(), elevator);
            upMovingMap.remove(elevator.getId());
        } else if (elevator.getState().equals(ElevatorState.IDLE)){
            upMovingMap.put(elevator.getId(), elevator);
            downMovingMap.put(elevator.getId(),elevator);
        } else if (elevator.getState().equals(ElevatorState.MAINTAINANCE)){
            upMovingMap.remove(elevator.getId());
            downMovingMap.remove(elevator.getId());
        }
    }

	@Override
	public void run() {
		stopManager =  false;
        while(true){
            try {
                Thread.sleep(100);
                if(stopManager && pickupRequests.size()<=0){
                	for (Elevator elevator : elevatorList) {
                		elevator.setExit(true);
					}
                    break;
                }
                /*if(pickupRequests.size()>0){
                	pickNearestElevator(pickupRequests.poll());
                }*/
            } catch (InterruptedException e){
                log.error(e.getMessage(), e);
            }
        }
	}

	public boolean isStopManager() {
		return stopManager;
	}

	public void setStopManager(boolean stopManager) {
		this.stopManager = stopManager;
	}

	public static List<Elevator> getElevatorList() {
		return elevatorList;
	}
}
