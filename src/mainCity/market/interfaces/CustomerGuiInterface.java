package mainCity.market.interfaces;

import java.util.Map;


public interface CustomerGuiInterface {
	
	public abstract void DoGoToMarket();
	public abstract void DoGoToCashier();
	public abstract void DoGoToEmployeeStation(int x, int y);
	public abstract void DoGoToWaitingArea();
	public abstract void DoExitMarket();
	
	public abstract int getWaitingPosX();
	public abstract int getWaitingPosY();
	public abstract boolean goInside(Map<String, Integer> m);
	public abstract boolean goInside();
	
}