package mainCity.market2.interfaces;


public interface CustomerGuiInterface {
	
	public abstract void DoGoToMarket();
	public abstract void DoGoToCashier();
	public abstract void DoGoToEmployeeStation(int x, int y);
	public abstract void DoGoToWaitingArea();
	public abstract void DoExitMarket();
	
	public abstract int getWaitingPosX();
	public abstract int getWaitingPosY();
	public abstract boolean goInside();
	
}