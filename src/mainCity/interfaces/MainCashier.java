package mainCity.interfaces;
import java.util.Map;

import role.market.MarketDeliveryManRole;
import mainCity.interfaces.DeliveryMan;

public interface MainCashier {
	//public abstract void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, Market1DeliveryManRole deliveryPerson);
	public abstract void msgHereIsChange(double amount, DeliveryMan deliveryPerson);
	public abstract void msgNotEnoughMoney(double amountOwed, double amountPaid);
	
	public abstract void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, DeliveryMan deliveryPerson);
}
