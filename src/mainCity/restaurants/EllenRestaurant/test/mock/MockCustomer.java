package mainCity.restaurants.EllenRestaurant.test.mock;


import mainCity.restaurants.EllenRestaurant.*;
import mainCity.restaurants.EllenRestaurant.interfaces.*;


/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

        /**
         * Reference to the Cashier under test that can be set by the unit test.
         */
        public Cashier cashier;
        public Waiter waiter;

        public MockCustomer(String name) {
                super(name);

        }
        
        
        public void gotHungry(){
        	
        }
		public void msgRestaurantFull(){
			
		}
		@Override
		public void msgFollowMe(EllenMenu menu, int tablenum, Waiter w){
			log.add(new LoggedEvent("Received msgFollowMe from " + w.getName()));
		}
		@Override
		public void msgWhatDoYouWant(){
			log.add(new LoggedEvent("Received msgWhatDoYouWant"));
		}
		@Override
		public void msgHereIsYourFood(String choice){
			log.add(new LoggedEvent("Received my order of " + choice));
		}
		public void msgOutOfFoodPleaseReorder(EllenMenu menu){
			
		}
        

        @Override
        public void msgHereIsCheck(double amount) {
                log.add(new LoggedEvent("Received HereIsCheck from waiter. Amount = "+ amount));

                if (this.getName().toLowerCase().contains("rich")){
                        //test the non-normative scenario where the customer overpays if their name contains the string "rich"
                	cashier.msgHereIsPayment(amount, (double) Math.ceil(amount), this);

                }else{
                        //test the normative scenario
                        cashier.msgHereIsPayment(amount, amount, this);
                }
        }

        @Override
        public void msgHereIsChange(double cashChange) {
                log.add(new LoggedEvent("Received HereIsChange from cashier. Change = $"+ cashChange));
        }

        @Override
        public void msgNotEnoughCash(double cashOwed) {
                log.add(new LoggedEvent("Received NotEnoughCash from cashier. Debt = $"+ cashOwed));
        }

}