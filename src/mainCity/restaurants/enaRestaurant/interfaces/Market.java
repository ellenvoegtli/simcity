package mainCity.restaurants.enaRestaurant.interfaces;

public interface Market
{
	public abstract void msgPaidMarketBill(double checks);

	public abstract void msgRestCantPay();
	public abstract void msgHereIsPayment(int amount);

	

}
