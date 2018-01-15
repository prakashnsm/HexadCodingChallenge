# HexadCodingChallenge

### Elevator and Elevator Manager


#### Multiple elevators (It's configurable, maximum 5)

When a pickup request arrives to a centre which manages several elevators, the pickup is assigned to the elevator with the shortest path to `requestedFloor`. 


## The algorithm

The algorithm is not complex and is well known: when the movement begins, it stays the same way in the same direction, attending to requests that adjust to the address and time. When there are no more requests or the movement limit is reached, the address changes and all requests are addressed in the opposite direction.


## The code

I tried to keep the code as good as possible. However, in the rush is not a priority, therefore, the code is definitely improved in many ways.

The number of Elevator can be configured in `ApplicationMain` through the method parameter` ElevatorMangerImpl`.`getInstance` as an integer value.

**How to test whats's done**

He did not have time to perform adequate tests, although he tried to do something. However, in the test directory there is an `ElevatorTest` and` ElevatorManagerTest` file where I started to `test ': actually running the simulation and observing its traces.