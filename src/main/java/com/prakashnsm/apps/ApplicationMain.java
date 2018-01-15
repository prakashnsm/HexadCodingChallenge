package com.prakashnsm.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prakashnsm.apps.ecs.Elevator;
import com.prakashnsm.apps.ecs.ElevatorManager;
import com.prakashnsm.apps.ecs.enums.ElevatorDirection;
import com.prakashnsm.apps.ecs.impl.ElevatorManagerImpl;
import com.prakashnsm.apps.ecs.model.PickupRequest;

public class ApplicationMain {
	private static Logger log = LoggerFactory.getLogger(ElevatorManagerImpl.class);
	public static void main(String[] args) throws InterruptedException {
		ElevatorManager elevatorManager = ElevatorManagerImpl.getInstance(10);
		
		Thread thread = new Thread(elevatorManager);
		thread.start();
		
		/*PickupRequest pickupRequest = new PickupRequest(5, ElevatorDirection.UP);
		elevatorManager.pickUp(pickupRequest);
		
		Thread.sleep(2000);
		
		PickupRequest pickupRequest1 = new PickupRequest(9, ElevatorDirection.UP);
		elevatorManager.pickUp(pickupRequest1);
		
		Thread.sleep(2000);
		
		PickupRequest pickupRequest2= new PickupRequest(3, ElevatorDirection.DOWN);
		elevatorManager.pickUp(pickupRequest2);
		
		Thread.sleep(2000);
		
		PickupRequest pickupRequest3 = new PickupRequest(7, ElevatorDirection.DOWN);
		elevatorManager.pickUp(pickupRequest3);
		
		Thread.sleep(2000);
		
		PickupRequest pickupRequest4 = new PickupRequest(8, ElevatorDirection.UP);
		elevatorManager.pickUp(pickupRequest4);*/
		/*for(int a=0; a<1; a++){
		new Thread(
			new Runnable() {
				
				@Override
				public void run() {
					for(int i=0; i < 25; i++){
						int val =  (int) ((Math.random() * 9) + 1);
						int indx =  (int) (Math.random() * 2);
						
						log.info("\n_____________________________________________________________________________________________________________________________");
						log.info("User Request triggered : on the floor " + val + " Direction "+ ElevatorDirection.values()[indx]);
						log.info("\n_____________________________________________________________________________________________________________________________\n");
						PickupRequest pickupRequest = new PickupRequest(val, ElevatorDirection.values()[indx]);
						ElevatorManagerImpl.getInstance().pickUp(pickupRequest);
						
						long slpTime =  (long) ((Math.random() * 20) + 1);
						try {
							Thread.sleep(slpTime * 100);
						} catch (InterruptedException e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}).start();
		}*/
		
		for(int i=0; i < 50; i++){
			int val =  (int) ((Math.random() * 9) + 1);
			int indx =  (int) (Math.random() * 2);
			
			log.info("\n_____________________________________________________________________________________________________________________________");
			log.info("User Request triggered : on the floor " + val + " Direction "+ ElevatorDirection.values()[indx]);
			log.info("\n_____________________________________________________________________________________________________________________________\n");
			PickupRequest pickupRequest = new PickupRequest(val, ElevatorDirection.values()[indx]);
			elevatorManager.pickUp(pickupRequest);
			
			long slpTime =  (long) ((Math.random() * 20) + 1);
			try {
				Thread.sleep(slpTime * 100);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
		
		PickupRequest pickupRequest = new PickupRequest(1, ElevatorDirection.UP);
		elevatorManager.pickUp(pickupRequest);
		Thread.sleep(2 * 100);
		PickupRequest pickupRequest1 = new PickupRequest(1, ElevatorDirection.UP);
		elevatorManager.pickUp(pickupRequest1);
		Thread.sleep(2 * 100);
		
		log.info("\n_____________________________________________________________________________________________________________________________");
		for (Elevator elevator: ElevatorManagerImpl.getElevatorList()) {
			log.info("Elevator[" + elevator.getId() + "] | current floor - " + 
					elevator.getCurrentFloor() + " | next move - " + elevator.getState());
		}		
		elevatorManager.setStopManager(true);
	}
}
