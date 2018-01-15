package com.prakashnsm.apps.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prakashnsm.apps.ecs.Elevator;
import com.prakashnsm.apps.ecs.ElevatorManager;
import com.prakashnsm.apps.ecs.enums.ElevatorDirection;
import com.prakashnsm.apps.ecs.exception.RequestException;
import com.prakashnsm.apps.ecs.impl.ElevatorManagerImpl;
import com.prakashnsm.apps.ecs.model.PickupRequest;


/**
 * @author prakashnsm
 * Date: 15-Jan-2018
 * Time: 2:14:50 am
 * 
 */
public class ElevatorManagerTest {
	
	private Logger log = LoggerFactory.getLogger(ElevatorManagerTest.class);

	  private ElevatorManager elevatorManager;
	  private List<Elevator> elevators;
	  
	  private int noOfElevators = 5;
	  @Before
	  public void initialize(){
		  log.info("elevatorManager instantiated with " + noOfElevators +" no(s) elevator");
	    	elevatorManager = ElevatorManagerImpl.getInstance(noOfElevators);
	    	elevators = ElevatorManagerImpl.getElevatorList();
	  }
	  
	  @Test
	  public void testElevatorInitializer(){
		  log.info("testElevatorInitializer");
		  Assert.assertEquals(noOfElevators, ElevatorManagerImpl.getElevatorList().size());
	  }
	  
	  @Test
	  public void testElevatorSetFloor() throws RequestException{
		  log.info("testElevatorSetFloor");
		  int[] a = new int[]{5,4,9, 1, 2};
		  
		  setElevatorFloors(a);
		  
		  Assert.assertEquals(a[0], elevators.get(0).getCurrentFloor());
		  Assert.assertEquals(a[1], elevators.get(1).getCurrentFloor());
		  Assert.assertEquals(a[2], elevators.get(2).getCurrentFloor());
	  }
	  
	  @Test
	  public void testElevatorPickupNearestOne() throws InterruptedException, RequestException{
		Thread thread = new Thread(elevatorManager);
		thread.start();
		Thread.sleep(1000);
		setElevatorFloors(new int[]{0,1,0,0,0});
		Thread.sleep(10000);
		PickupRequest pickupRequest = new PickupRequest(4, ElevatorDirection.UP);
		Elevator elevator = elevatorManager.pickUp(pickupRequest);
		Thread.sleep(1000);
		Assert.assertEquals(elevators.get(1).getId(), elevator.getId());
		Thread.sleep(2000);
	  }
	  
	  @Test
	  public void testMultipleRequestinSameDirectionWithGround() throws InterruptedException, RequestException{
		Thread thread = new Thread(elevatorManager);
		thread.start();
		setElevatorFloors(new int[]{0,0,0,0,0});
		Elevator elevator = elevatorManager.pickUp(new PickupRequest(8, ElevatorDirection.DOWN));
		Thread.sleep(500);
		Elevator elevator1 = elevatorManager.pickUp(new PickupRequest(4, ElevatorDirection.DOWN));
		Thread.sleep(2000);
		Assert.assertEquals(elevator.getId(), elevator1.getId());
		Thread.sleep(1000);
	  }
	  
	  @Test
	  public void testMultipleRequestinSameDirection() throws InterruptedException, RequestException{
		Thread thread = new Thread(elevatorManager);
		thread.start();
		setElevatorFloors(new int[]{1,7,9,1,2});
		Elevator elevator = elevatorManager.pickUp(new PickupRequest(8, ElevatorDirection.DOWN));
		Thread.sleep(500);
		Elevator elevator1 = elevatorManager.pickUp(new PickupRequest(4, ElevatorDirection.DOWN));
		Thread.sleep(500);
		Assert.assertEquals(elevator.getId(), elevators.get(1).getId());
		Assert.assertEquals(elevator1.getId(), elevators.get(4).getId());
		Thread.sleep(1000);
	  }
	  
	  @Test
	  public void testMultipleRequestinSameDirectionWith3() throws InterruptedException, RequestException{
		Thread thread = new Thread(elevatorManager);
		thread.start();
		setElevatorFloors(new int[]{0,0,0,0,0});
		Elevator elevator = elevatorManager.pickUp(new PickupRequest(8, ElevatorDirection.DOWN));
		Thread.sleep(500);
		Elevator elevator1 = elevatorManager.pickUp(new PickupRequest(4, ElevatorDirection.DOWN));
		Thread.sleep(500);
		Elevator elevator2 = elevatorManager.pickUp(new PickupRequest(9, ElevatorDirection.UP));
		Thread.sleep(5000);
		Assert.assertTrue(elevator.getId() == elevator1.getId() && elevator.getId() == elevator2.getId());
//		Assert.assertEquals(elevator.getId(), elevators.get(2).getId());
//		Assert.assertEquals(elevator1.getId(), elevators.get(4).getId());
//		Assert.assertEquals(elevator1.getId(), elevators.get(4).getId());
		Thread.sleep(10000);
	  }
	  
	  @Test
	  public void testMultipleRequestinSameDirectionFromTop() throws InterruptedException, RequestException{
		Thread thread = new Thread(elevatorManager);
		thread.start();
		setElevatorFloors(new int[]{10,10,10,10,10});
		Elevator elevator = elevatorManager.pickUp(new PickupRequest(8, ElevatorDirection.UP));
		Thread.sleep(500);
		Elevator elevator1 = elevatorManager.pickUp(new PickupRequest(4, ElevatorDirection.UP));
		Thread.sleep(2000);
		Assert.assertEquals(elevator.getId(), elevator1.getId());
		Thread.sleep(1000);
	  }
	  
	  @Test
	  public void testMultipleRequestinSameDirectionUp() throws InterruptedException, RequestException{
		Thread thread = new Thread(elevatorManager);
		thread.start();
		setElevatorFloors(new int[]{3, 1, 9, 10, 5});
		
		Elevator elevator = elevatorManager.pickUp(new PickupRequest(8, ElevatorDirection.UP));
		Thread.sleep(500);
		
		Elevator elevator1 = elevatorManager.pickUp(new PickupRequest(4, ElevatorDirection.UP));
		Thread.sleep(700);
		
		Elevator elevator2 = elevatorManager.pickUp(new PickupRequest(6, ElevatorDirection.UP));
		Thread.sleep(3000);
		
		Assert.assertEquals(elevator.getId(), elevators.get(2).getId());
		Assert.assertEquals(elevator1.getId(), elevators.get(0).getId());
		Assert.assertEquals(elevator2.getId(), elevators.get(4).getId());
		
		Thread.sleep(6000);
	  }
	  
	  public void setElevatorFloors(int[] a) throws RequestException{
		  int[] lp = new int[]{0,1,2, 3, 4};
		  
		  for (int i : lp) {
			  elevators.get(i).setCurrentFloor(a[i]);
		  }
	  }
}
