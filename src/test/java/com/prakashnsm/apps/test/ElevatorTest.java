package com.prakashnsm.apps.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.prakashnsm.apps.ecs.Elevator;
import com.prakashnsm.apps.ecs.enums.ElevatorDirection;
import com.prakashnsm.apps.ecs.exception.RequestException;
import com.prakashnsm.apps.ecs.exception.ServiceException;
import com.prakashnsm.apps.ecs.impl.ElevatorImpl;

/**
 * @author prakashnsm
 * Date: 15-Jan-2018
 * Time: 2:10:52 am
 * 
 */
public class ElevatorTest {

	private Elevator elevator;
	
	  @Before
	  public void initializeElevator(){
	    elevator = new ElevatorImpl(0);
	  }
	  
	  @Test
	  public void testId(){
		  Assert.assertEquals(0, elevator.getId());
	  }
	  
	  @Test
	  public void testMoveAndCurrentFloor(){
		  Assert.assertEquals(0, elevator.getCurrentFloor());
		  
		  elevator.moveUp();
		  elevator.moveUp();
		  
		  Assert.assertEquals(2, elevator.getCurrentFloor());
		  
		  try {
			elevator.setCurrentFloor(6);
		} catch (RequestException e) {
			e.printStackTrace();
		}
		  elevator.moveDown();
		  elevator.moveDown();
		  elevator.moveDown();
		  
		  Assert.assertEquals(3, elevator.getCurrentFloor());
	  }
	  
	  @Test
	  public void testMoveAndDestinations() throws InterruptedException{
		  Thread t = new Thread(elevator);
		  t.start();
		  
		  try {
			  elevator.addNewDestinatoin(5, ElevatorDirection.UP);
			  elevator.addNewDestinatoin(2,ElevatorDirection.UP);
			  Thread.sleep(2500);
			  Assert.assertEquals(5, elevator.getCurrentFloor());
			  elevator.addNewDestinatoin(1, ElevatorDirection.UP);
			  Thread.sleep(5000);
			  
		  } catch (ServiceException | RequestException e) {
		  }
		  
		  Assert.assertEquals(1, elevator.getCurrentFloor());
		  Thread.sleep(2000);
	  }
	  
	  @Test
	  public void testFloorMaxLimit() throws InterruptedException{
		  try {
			elevator.setCurrentFloor(11);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof RequestException);
		}
	  }
	  
	  @Test
	  public void testFloorMinLimit() throws InterruptedException{
		  try {
			elevator.setCurrentFloor(-3);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof RequestException);
		}
	  }
}
