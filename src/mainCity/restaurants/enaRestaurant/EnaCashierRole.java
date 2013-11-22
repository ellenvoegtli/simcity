package mainCity.restaurants.enaRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mainCity.restaurants.enaRestaurant.gui.HostGui;
import mainCity.restaurants.enaRestaurant.interfaces.Cashier;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Market;
import mainCity.restaurants.enaRestaurant.test.mock.EventLog;
import mainCity.restaurants.enaRestaurant.test.mock.LoggedEvent;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import mainCity.market.MarketDeliveryManRole;
import agent.Agent;

public class EnaCashierRole extends Agent implements Cashier{

		public List<Tab> Tabs= Collections.synchronizedList(new ArrayList<Tab>());
		public List<MarketTab> marketChecks = Collections.synchronizedList(new ArrayList<MarketTab>());
		public double restCash;
		public double marketBill;
		//public EnaMarketRole market;
		public EventLog log = new EventLog();
		private String name;
		public enum payStatus 
		{pending, billed, paying, paid, done};
		payStatus status = payStatus.pending;
		public enum marketPay
		{ pending, paying, paid};
		marketPay state = marketPay.pending;
		
				public HostGui hostGui;

		public EnaCashierRole(String name) 
		{
			super();

			this.name = name;
		}

		

		public String getName() {
			return name;
		}


		// Messages
		public void msgNoMoney()
		{
			print("Restaurant had no money");
			setRestCash(0);
		}
		public void msgRestMoney()
		{
			print("Restaurant has cash");
			setRestCash(100);
		}
		public void msgComputeBill(String choice, Customer c)
		{
			log.add(new LoggedEvent("Recieved message to compute the bill"));
			print("computing the bill for  " +c);
			Tabs.add(new Tab(choice, c, payStatus.pending));
			stateChanged();
		}
		public void msgHereIsMarketBill(Map<String,Integer> order, double bill, MarketDeliveryManRole name)
		{
			log.add(new LoggedEvent("recieved message to pay the market"));
			marketChecks.add(new MarketTab(name, bill, marketPay.pending));
			stateChanged();
			
		}//0000000000000000000 CHANGE ALL CODE AFTER THE MSGRESTOCKBILL IS CALLED 000000000000000000000000000000
		
		public void msgHereIsChange(double amount, MarketDeliveryManRole name)
		{
			setRestCash(getRestCash() + amount);
			stateChanged();
		}
		
		private double getRestCash() 
		{
			return restCash;
		}



		public void msgRestockBill(double reciept, Market ma)
		{
			log.add(new LoggedEvent("recieved message to pay the market"));
			marketChecks.add(new MarketTab(ma, reciept, marketPay.pending));
			//state = marketPay.pending;
			stateChanged();
			
		}
		
		public void msgPayment(String choice, double cash, Customer c)
		{
			synchronized(Tabs)
			{
			for(Tab tab:Tabs)
			{
				if(tab.c == c)
				{
					log.add(new LoggedEvent("Received payment message"));
					print("Payment recieved from customer");
					tab.paymentStat = payStatus.paid;
					stateChanged();
				}
				
			}
			}
		}
			

		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		
		
public boolean pickAndExecuteAnAction() 
{
		synchronized(marketChecks)
		{
			for(MarketTab checks : marketChecks)
			{
				if(checks.mState == marketPay.pending)
				{
					payTheMarket(checks);
					checks.mState = marketPay.paid;
				}
				return true;
				
			}
		}
		
		synchronized(Tabs)
		{
			for (Tab tab : Tabs) 
			{
				if (tab.paymentStat == payStatus.pending) 
				{
						tab.paymentStat = payStatus.billed;
						ComputeCheck(tab);
						return true;
				}
				if (tab.paymentStat == payStatus.billed)
				{
					System.out.println("tab has been computed, is billed");
					tab.paymentStat = payStatus.paying;
					GiveCheck(tab);
					return true;
				}
				if(tab.paymentStat == payStatus.paid)
				{					
					tab.paymentStat = payStatus.done;
					System.out.println("Let the cashier know the customer has paid");
					ComputeChange(tab);						
					return true;
				}
			}
			return false;
			
	}
}

		// Actions

		private void ComputeCheck(Tab t)
		{
			t.setCheck(t.choice);
		}
		
		private void GiveCheck(Tab t)
		{
			t.setCheck(t.choice);
			t.c.getWaiter().msgHereIsBill(t.check, t.c);
		}
		
		private void ComputeChange(Tab t)
		{
			 double change = 0;
			 if(t.c.getDebt() != 0)
			 {
				 print("customer is paying for tab and previous debt");
				 change = t.c.getCash()- t.check - t.c.getDebt();
				 t.c.setDebt(0);
				 t.c.msgHereIsYourChange(change);
			 }
			 else if(t.c.getName().equals("debt") && t.c.getDebt() == 0)
			 {
				 t.c.msgHereIsYourChange(change);
				 print("Customer has $ " +t.c.getCash());
				 print("Customer still owes $ " + (t.check-t.c.getCash()));
				 t.c.setDebt(t.check-t.c.getCash());
				 
			 }
			 	 
			 else
			 {
				 change = t.c.getCash()-t.check;
				 t.c.msgHereIsYourChange(change);
				 Tabs.remove(t);
			 }
		}
		
		public void payTheMarket(MarketTab checks)
		{
			print("the restaurant has money : $ " +restCash);
			//^^^^^^^^^^^^^^^^^^^^^^^^ NON NORM IF RESTAURANT DOES NOT HAVE ENOUGH MONEY TO PAY THE MARKET BILL ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			/*if(restCash == 0)
			{
				print("The restaurant does not have any money to pay the market's bill");
				checks.deliveryMan.msgRestCantPay();
				print("Restaurant is looking for donations to pay off the debt and giving combinign it with coupons to even out tab");
				restCash = 54;
			}*/
			
				print("The cashier is has taken care of the market's bill for: $ " +checks.checks);
				restCash = restCash - checks.checks;
				checks.deliveryMan.msgHereIsPayment(checks.checks);
				print("The restaurant now has $ " +restCash);
				marketChecks.remove(checks);
			
		}
		
		
		

		//utilities
		public void setRestCash(double r)
		{
			restCash = r;
			
		}
		public void setGui(HostGui gui) 
		{
			hostGui = gui;
		}

		public HostGui getGui() 
		{
			return hostGui;
		}
		
		
		////HELPER CLASSES/////
			
	public class MarketTab
	{
		public Market ma;
		public double checks;
		public marketPay mState;
		public MarketDeliveryManRole deliveryMan;
		
		public MarketTab(Market mrk, double ch, marketPay mSt)
		{
			ma = mrk;
			checks = ch;
			mState = mSt;
		}
		
		public MarketTab(MarketDeliveryManRole name, double ch, marketPay mSt)
		{
			deliveryMan = name;
			checks = ch;
			mState = mSt;
		}
	}
		

	public class Tab 
	{
			String choice;
			public payStatus paymentStat;
			public Customer c;
			public MockCustomer mc;
			public double check; 
			
			
			public Tab(String ch, Customer cst, payStatus pymSt) 
			{
				this.choice = ch;
				this.c = cst;
				this.paymentStat = pymSt;
				
			}
			
			public double getPrice(String ch)
			{
				double cost = 0; 
				if (ch == "steak")
				{
					cost = 15.99;
				}
				if (ch == "porkchops")
				{
					cost = 10.99;
				}
				if(ch == "lamb")
				{
					cost= 5.99;
				}
				if(ch == "lambchops")
				{
					cost= 8.99;
				}
				
				return cost;
			}
			public void setCheck(String ch)
			{
				check = getPrice(ch);
			}

			public String toString() 
			{
				return "order " + choice;
			}
			
			
	}


	







}



