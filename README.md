team24
======

SimCity201 Project Repository for CS 201 students

## WORK DONE

#### Ellen :
  + Responsible for the Market (and the integration/functionality of my own restaurant, EllenRestaurant)
  + Market is integrated with each restaurant. All market roles have been unit tested (although I need to run more tests on EmployeeRole. There were specific issues with getting its other test scenarios to run)
  + Market has scenarios for handling verification issues (of the amount a customer or business got charged, or the change they received), which have been tested in the unit tests
  + Also responsible for: Market gui, setting up all Trace/AlertLog files (with given CP code) and CityGui trace panels, although not necessarily adding tags; setting up most of the CityGui Create/Control panel display and functionality

#### Ena :


#### Marcus : 
  + Created the PersonAgent as well as the base Role from which every other role derived from
    1. Created all person AI decisions, priority making, animation, interacting with buildings, and settings roles active
  + Created an algorithm to keep people from walking over buildings
  + Created parser and function to populate people into the city on program run
  + Helped implement card layout and bringing individual restaurant GUI's into the city
  + Implemented shared data waiter
  + Implemented functions for roles/workers to go off duty (with wages) based on city clock time

#### Jefferson : 
	+Responsible for the Bank as well as integration/functionality of jeffersonRestaurant
	+Scenarios are in place for handling no existing account when requesting transactions, paying back loans, and criteria for approval of loans
	+Establishment can be "closed" and turn away customers if not all essential employees are present (manager, teller, banker)
	+Defined Person rules for depositing and withdrawing (cash thresholds)
	+Visually upgraded external buildings drawn in MainCity


#### David :
  + Responsible for transportation 
    1. Created Bus that picks people up and drops them off at their desired destination
    1. Created road system, based on Crowley's demo code, on which only vehicles may travel (People can cross them, however)
	1. Created system of bus stops at which people may wait for the bus and be picked up / dropped off. (Created algorithm in which people calculate the bus stop that is nearest to them)
	1. Buses are fully unit tested
  + Fully integrated own restaurant into the city
  + Designed and implemented actual city's layout (animation panel)
    1. Created Building class and designated locations for each different building.
	1. Mapped out coordinates for roads, buildings, doorways, and bus stops
  + Helped implement card layout based on code in the simcity demo.


## HOW TO RUN OUR SYSTEM
  + Using Eclipse : 
  + To run our code, clone our repository from github into a directory, then import the project into Eclipse:
  + Click File -> New -> Other; Under the Java folder, pick "Java Project"
  + Uncheck "Use Default Location" and browse for the location of the cloned repository on your computer.
  + Press finish and the project should be loaded up in Eclipse.
  + In the mainCity.gui package, select "CityGui.java" in the Package Explorer and click Run as a Java Application

  + To run unit tests:
  	+ Add JUnit to the build path
  		+ Project -> Properties -> Java Build Path -> Libraries -> Add Library -> JUnit -> JUnit 4
  + The Market unit tests: 
  	+ In the package mainCity.market.test, you will find: GreeterTest.java, CashierTest.java, EmployeeTest.java, CustomerTest.java, and DeliveryTest.java. Open any of these and click run as a JUnit Test.

  + The Bus unit tests: 
	+ In the package mainCity.test, you will find BusTest.java. Right click on the file, go to Run As -> JUnit Test
  
  + The Banks unit tests: 
	+ In the package mainCity.bank.test, you will find BankManagerTest.java, BankTellerTest.java, BankerTest.java, and BankCustomerTest.java. Right click on the file, go to Run As -> JUnit Test.	

## THINGS WE KNOW DON'T WORK / GENERAL NOTES ABOUT OUR V1 SUBMISSION
  + Some of our PersonAgents who take on jobs in certain restaurants will not be able to properly go off-duty after they end their shift. We figured out a way to do this successfully, but it hasn't been implemented everywhere due to time constraints
  + Not all of our trace panels (buildings) have been populated with their AlertTags, due to time constraints 
  + An FYI: the way our hard-coded PersonAgents (from the configuration file) operate, their work duties are prioritized over their hunger, so even though you may tell them to get hungry, they won't act upon that until after their shift.
  + When adding Persons though the "Create" panel on the CityGui, they also will have an occupation and will prioritize that over their hunger.
  + We do not have waiter breaks implemented to where they can leave the restaurant and do other things.
  + Occasionally in our restaurant animation windows, our waiters seem to get stuck when they go to pick up another customer. We couldn't find any debugging issues with the thread being locked, and sometimes the animation would pick up again, indicating it wasn't actually a semaphore issue but an animation issue.
  + Some restaurants have yet to implement shared data waiters. 