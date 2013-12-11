package role.enaRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import role.Role;
import role.market.MarketDeliveryManRole;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.DeliveryMan;
import mainCity.restaurants.enaRestaurant.gui.EnaHostGui;
import mainCity.restaurants.enaRestaurant.interfaces.Cashier;
import mainCity.restaurants.enaRestaurant.interfaces.Customer;
import mainCity.restaurants.enaRestaurant.interfaces.Market;
import mainCity.restaurants.enaRestaurant.test.mock.EventLog;
import mainCity.restaurants.enaRestaurant.test.mock.LoggedEvent;
import mainCity.restaurants.enaRestaurant.test.mock.MockCustomer;
import agent.Agent;

public class EnaCashierRole extends Role implements Cashier{

		public List<Tab> Tabs= Collections.synchronizedList(new ArrayList<Tab>());
		public List<MarketTab> marketChecks = Collections.synchronizedList(new ArrayList<MarketTab>());
		public double restCash;
		public double marketBill;
		private EnaHostRole host;
		public EventLog log = new EventLog();
		private String name;
		private boolean onDuty;
		private boolean entered;
		public enum payStatus 
		{pending, billed, paying, paid, done};
		payStatus status = payStatus.pending;
		public enum marketPay
		{ pending, paying, paid};
		marketPay state = marketPay.pending;
		
				public EnaHostGui hostGui;

		public EnaCashierRole( PersonAgent p, String name) 
		{
			super(p);

			this.name = name;
			onDuty = true;
			entered= true;
		}

		

		public String getName() {
			return name;
		}

		//for alert log trace statements
				public void log(String s){
			        AlertLog.getInstance().logMessage(AlertTag.ENA_RESTAURANT, this.getName(), s);
			        AlertLog.getInstance().logMessage(AlertTag.ENA_COOK, this.getName(), s);
				}
			
		// Messages
		public void msgNoMoney()
		{
			log("Received msgNoMoney");
		}
		public void msgRestMoney()
		{
			log( "Received msg that restaurant has cash");
			setRestCash(100);
		}
		public void msgComputeBill(String choice, Customer c)
		{
			log.add(new LoggedEvent("Recieved message to compute the bill"));
			log( "Received msgComputeBill");
			Tabs.add(new Tab(choice, c, payStatus.pending));
			stateChanged();
		}
		public void msgHereIsMarketBill(Map<String,Integer> order, double bill, DeliveryMan deliveryPerson)
		{
			log.add(new LoggedEvent("recieved message to pay the market"));
			log("Received msgHereIsMarketBill");

			marketChecks.add(new MarketTab(deliveryPerson, bill, marketPay.pending));
			stateChanged();
			
		}//0000000000000000000 CHANGE ALL CODE AFTER THE MSGRESTOCKBILL IS CALLED 000000000000000000000000000000
		
		
		public void msgHereIsChange(double amount, DeliveryMan deliveryPerson) 
		{
			log("Received msgHereIsChange");

			setRestCash(getRestCash() + amount);
			stateChanged();
			deliveryPerson.msgChangeVerified("enaRestaurant");
		}
		
		private double getRestCash() 
		{
			return restCash;
		}

		public void msgNotEnoughMoney(double amountOwed, double amountPaid) 
		{
				for(MarketTab tab : marketChecks)
				{
					if(tab.checks == amountPaid);
					{
						tab.deliveryMan.msgIOweYou(amountOwed, "enaRestaurant");
					}
				}
		}
		
		public void msgGoOffDuty(double amount){
			addToCash(amount);
			ContactList.getInstance().getBank().directDeposit("enaRestaurant", restCash);
			onDuty = false;
			stateChanged();
		}

		/*public void msgRestockBill(double reciept, Market ma)
		{
			log.add(new LoggedEvent("recieved message to pay the market"));
			log("receieved message to pay the market");
			marketChecks.add(new MarketTab(ma, reciept, marketPay.pending));
			//state = marketPay.pending;
			stateChanged();
			
		}*/
		
		public void msgPayment(String choice, double cash, Customer c)
		{
			synchronized(Tabs)
			{
			for(Tab tab:Tabs)
			{
				if(tab.c == c)
				{
					log.add(new LoggedEvent("Received payment message"));
					log( "payment recieved from customer");

					//log("Payment recieved from customer");
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
	if(entered) {
		print("Withdrawing $1200 from bank");
		ContactList.getInstance().getBank().directWithdraw("enaRestaurant", 1200);
		ContactList.getInstance().setEnaCashier(this);
		restCash = 1200;
		entered = false;
	}
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
				{		log("Tab has beeen computed, is billed");

					//log("tab has been computed, is billed");
					tab.paymentStat = payStatus.paying;
					GiveCheck(tab);
					return true;
				}
				if(tab.paymentStat == payStatus.paid)
				{					
					tab.paymentStat = payStatus.done;
					//log("Let the cashier know the customer has paid");
					log("tell the cashier the customer has paid");

					ComputeChange(tab);						
					return true;
				}
			}
			
			
			if(Tabs.isEmpty() && !onDuty) {
				setInactive();
				onDuty = true;
				entered = true;
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
					log("The customer has paid the previous debt");

				 //log("customer is paying for tab and previous debt");
				 change = t.c.getCash()- t.check - t.c.getDebt();
				 t.c.setDebt(0);
				 t.c.msgHereIsYourChange(change);
			 }
			 else if(t.c.getName().equals("debt") && t.c.getDebt() == 0)
			 {
				 t.c.msgHereIsYourChange(change);
				 log("Customer has $ " +t.c.getCash());
				 log("Customer still owes $ " + (t.check-t.c.getCash()));
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
			log("the restaurant has money : $ " +restCash);
			//^^^^^^^^^^^^^^^^^^^^^^^^ NON NORM IF RESTAURANT DOES NOT HAVE ENOUGH MONEY TO PAY THE MARKET BILL ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
			/*if(restCash < checks.check)
			{
				log("The restaurant does not have any money to pay the market's bill");
				checks.deliveryMan.msgRestCantPay();
				log("Restaurant is looking for donations to pay off the debt and giving combinign it with coupons to even out tab");
				restCash = 54;
			}*/
			
				log("The cashier is has taken care of the market's bill for: $ " +checks.checks);
				restCash = restCash - checks.checks;
				checks.deliveryMan.msgHereIsPayment(checks.checks, "enaRestaurant");
				log("The restaurant now has $ " +restCash);
				marketChecks.remove(checks);
			
		}
		
		
		

		//utilities
		public void setRestCash(double r)
		{
			restCash = r;
			
		}
		public void setGui(EnaHostGui gui) 
		{
			hostGui = gui;
		}

		public EnaHostGui getGui() 
		{
			return hostGui;
		}
		
		
		////HELPER CLASSES/////
			
	public class MarketTab
	{
		public Market ma;
		public double checks;
		public marketPay mState;
		public DeliveryMan deliveryMan;
		
		public MarketTab(Market mrk, double ch, marketPay mSt)
		{
			ma = mrk;
			checks = ch;
			mState = mSt;
		}
		
		public MarketTab(DeliveryMan name, double ch, marketPay mSt)
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


	public void setHost(EnaHostRole host) 
	{
		this.host = host;		
	}



	


	public void deductCash(double payroll) {
		// TODO Auto-generated method stub
		
	}




}



