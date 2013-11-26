package mainCity.market.test.mock;


public class MockCustomer extends Mock implements Customer {

        /**
         * Reference to the Cashier under test that can be set by the unit test.
         */
        public Cashier cashier;
        public Employee employee;

        public MockCustomer(String name) {
                super(name);

        }
}