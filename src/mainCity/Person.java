package mainCity;

import mainCity.PersonAgent.ActionType;
import mainCity.gui.Building;
import role.Role;

public interface Person {
	public abstract void updateOccupation(String o, int b, int e);

	public abstract void msgAtDestination();

	public abstract void msgArrivedAtDestination();

	public abstract void msgNeedToFix();

	public abstract void msgBusHasArrived();

	public abstract void msgGoToWork();

	public abstract void msgGotHungry();

	public abstract void msgGoToRestaurant();

	public abstract void msgGoToMarket();

	public abstract void msgGoHome();

	public abstract void msgGoToBank(String purpose);

	public abstract void addRole(ActionType type, Role role);

	public abstract void roleInactive();

	public abstract int getTime();

	public abstract int getWorkHours();

	public abstract double getCash();

	public abstract void setCash(double d);

	public abstract Building getHomePlace();
}