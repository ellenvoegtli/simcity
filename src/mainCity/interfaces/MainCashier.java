package mainCity.interfaces;
import java.util.Map;

import role.market.MarketDeliveryManRole;
import mainCity.market.*;

public interface MainCashier {
	public abstract void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, MarketDeliveryManRole deliveryPerson);
	
	public abstract void msgHereIsChange(double amount, MarketDeliveryManRole deliveryPerson);
}
