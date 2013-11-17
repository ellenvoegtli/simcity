package mainCity.restaurants.EllenRestaurant.interfaces;

import mainCity.restaurants.EllenRestaurant.*;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
//what the customer receives from the Waiter and Cashier
public interface Customer {
		public String getName();
		
		public abstract void gotHungry();
		public abstract void msgRestaurantFull();
		public abstract void msgFollowMe(EllenMenu menu, int tablenum, Waiter w);
		public abstract void msgWhatDoYouWant();
		public abstract void msgHereIsYourFood(String choice);
		public abstract void msgOutOfFoodPleaseReorder(EllenMenu menu);
		
		
	
	
	
        /**
         * @param total The cost according to the cashier
         *
         * Sent by the cashier prompting the customer's money after the customer has approached the cashier.
         */
		public abstract void msgHereIsCheck(int amount);

        /**
         * @param total change (if any) due to the customer
         *
         * Sent by the cashier to end the transaction between him and the customer. total will be >= 0 .
         */
		public abstract void msgHereIsChange(int cashChange);


        /**
         * @param remaining_cost how much money is owed
         * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
         */
		public abstract void msgNotEnoughCash(int cashOwed);

}