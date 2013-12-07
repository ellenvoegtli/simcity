package mainCity.interfaces;
import java.util.Map;

import role.market1.Market1DeliveryManRole;
import mainCity.market1.*;

public interface MainCashier {
	public abstract void msgHereIsMarketBill(Map<String, Integer> inventory, double billAmount, Market1DeliveryManRole deliveryPerson);
	public abstract void msgHereIsChange(double amount, Market1DeliveryManRole deliveryPerson);
	public abstract void msgNotEnoughMoney(double amountOwed, double amountPaid);
}
