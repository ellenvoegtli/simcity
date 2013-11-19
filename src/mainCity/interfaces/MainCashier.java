package mainCity.interfaces;
import java.util.HashMap;

public interface MainCashier {
	public abstract void msgHereIsMarketBill(HashMap<String, Integer> inventory, double billAmount, String deliveryPerson);
}
