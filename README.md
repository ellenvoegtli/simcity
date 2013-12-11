team24
======

SimCity201 Project Repository for CS 201 students

## WORK DONE

#### Ellen :
  + Primarily responsible for the Market
    + Market is integrated with each restaurant. All market roles have been unit tested successfully
    + Market has scenarios for handling verification issues (of the amount a customer or business got charged, or the change they received), which have been addressed in the unit tests
    + Market roles go off-duty and shift change appropriately
  + Also responsible for: 
    + Market gui; 
    + Updates to my own restaurant (verification that all employees are present and the market is open, & integration with the market host/delivery man; updated GUI)
    + Setting up Trace/AlertLog system (with given CP code) and created CityGui trace panels layout;  
    + Setting up the CityGui Create panel, Control panel, Scenario panel, and Inventory panel displays and functionalities;
    + Creating and setting up the Contact List.

#### Ena :
+ Responsible for the housing and apartment set up within the city. Created  OccupantRole and LandLord Role to handle situations for people either renting or owning homes.
+ Responsible for gui for occupants and landlords within the homes as well as the layout of both houses and apartments through the home class 
+ Unit testing for both the occupant and landlord and their interactions within the homes
 + Implemented enaRestaurant into the city layout and integrated personAgent as well as the new city market to work within the original restaurant
+ added  tags throughout restaurant roles to be used along with the trace panels
+ added tags to occupantRole for trace panels


#### Marcus : 
  + Created the PersonAgent as well as the base Role from which every other role derived from
    1. Created all person AI decision-making, action priority, animation, interacting with buildings, and settings roles active
  + Created an algorithm to keep people from walking over buildings (works except when they want to become a car)
  + Created parser and function to populate people into the city on program run (staggers creation of people)
    1. Enabled ability to reset state of city when changing scenarios
  + Helped implement card layout and bringing individual restaurant GUI's into the city
  + Implemented shared data waiter and cook
  + Implemented functions for roles/workers to go off & on duty (with wages) based on city clock time and day

#### Jefferson : 
  + Responsible for the Bank as well as integration/functionality of jeffersonRestaurant
  + Scenarios are in place for handling no existing account when requesting transactions, paying back loans, and criteria for approval of loans
  + Establishment can be "closed" and turn away customers if not all essential employees are present (manager, teller, banker)
  + Defined Person rules for depositing and withdrawing (cash thresholds)
  + Implemented functions for businesses to acquire starting capital and direct deposit money
  + Added tags to Bank and my restaurant roles to be used with trace panels
  + Acquired,extracted, and resized all sprites and images (non-blocks) used in city both internal and external
    1. Visually upgraded external buildings drawn in MainCity
    1. Visually upgraded all internal building views
 
  
  

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
  + Added shared data for david restaurant 
  + Added Intersections, traffic behavior, and cars


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
	+ In the package mainCity.bank.test, you will find BankManagerTest.java, BankTellerTest.java, BankerTest.java, . Right click on the file, go to Run As -> JUnit Test.	
	
  + All scenarios are runnable under scenario tab in GUI.

## THINGS WE KNOW DON'T WORK / GENERAL NOTES ABOUT OUR V1 SUBMISSION
  + Some of our PersonAgents who take on jobs in certain restaurants will not be able to properly go off-duty after they end their shift. We figured out a way to do this successfully, but it hasn't been implemented everywhere due to time constraints
    + UPDATE: Most workplaces are now implementing going-off-duty functionalities properly.
  + An FYI: the way our hard-coded PersonAgents (from the configuration file) operate, their work duties are prioritized over their hunger, so even though you may tell them to get hungry, they won't act upon that until after their shift.
  + When adding Persons though the "Create" panel on the CityGui, if they have an occupation they will prioritize that over their hunger.
  + We do not have waiter breaks implemented to where they can leave the restaurant and do other things.
  + In the restaurant on the far right, if it ever gets stuck customers and 1 waiter, add a waiter and set his occupation as davidWaiter.
