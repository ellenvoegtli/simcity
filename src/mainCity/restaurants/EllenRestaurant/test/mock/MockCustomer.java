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
		public void msgFollowMe(EllenMenu menu, int tablenum, Waiter w){
			
		}
		public void msgWhatDoYouWant(){
			
		}
		public void msgHereIsYourFood(String choice){
			
		}
		public void msgOutOfFoodPleaseReorder(EllenMenu menu){
			
		}
        

        @Override
        public void msgHereIsCheck(int amount) {
                log.add(new LoggedEvent("Received HereIsCheck from waiter. Amount = "+ amount));

                /*
                if(this.name.toLowerCase().contains("thief")){
                        //test the non-normative scenario where the customer has no money if their name contains the string "theif"
                        cashier.IAmShort(this, 0);

                }
                *///else 
                if (this.getName().toLowerCase().contains("rich")){
                        //test the non-normative scenario where the customer overpays if their name contains the string "rich"
                        //cashier.msgHereIsPayment(this, Math.ceil(amount));
                	cashier.msgHereIsPayment(amount, (int) Math.ceil(amount), this);

                }else{
                        //test the normative scenario
                        cashier.msgHereIsPayment(amount, amount, this);		//??
                }
        }

        @Override
        public void msgHereIsChange(int cashChange) {
                log.add(new LoggedEvent("Received HereIsChange from cashier. Change = $"+ cashChange));
        }

        @Override
        public void msgNotEnoughCash(int cashOwed) {
                log.add(new LoggedEvent("Received NotEnoughCash from cashier. Debt = $"+ cashOwed));
        }

}