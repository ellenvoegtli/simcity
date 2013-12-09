package mainCity.interfaces;

import mainCity.PersonAgent;
import mainCity.PersonAgent.CityLocation;

public interface PersonGuiInterface {

	public abstract void DoGoToLocation(PersonAgent.CityLocation destination);

	public abstract void DoGoToStop();

	public abstract void DoGoToLocationOnBus(
			PersonAgent.CityLocation destination);

	public abstract void DoGoInside();

	public abstract void DoGoOutside();

	public abstract CityLocation findNearestStop();

	public abstract void getInCar();
	
	public abstract void getOutOfCar();

	public abstract void DoGetOnRoad();

	public abstract void DoGoToLocationOnCar(CityLocation d);

	public abstract void AddCarToLane();

}