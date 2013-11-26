package mainCity.market.test.mock;




public class MockCashier extends Mock implements Cashier {
	public DeliveryMan deliveryMan;
    public Employee employee;
    public MarketGreeter greeter;

    public MockCashier(String name) {
            super(name);

    }
}