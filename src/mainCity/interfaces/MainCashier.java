package mainCity.interfaces;
import java.util.Map;

public interface MainCashier {
	public abstract void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, String deliveryPerson);
}
