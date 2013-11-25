package mainCity.restaurants.jeffersonrestaurant;

import java.util.*;

import role.Role;
import role.market.MarketDeliveryManRole;
import mainCity.PersonAgent;
import mainCity.restaurants.jeffersonrestaurant.JeffersonWaiterRole;
import mainCity.restaurants.jeffersonrestaurant.JeffersonCookRole.Order;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Cashier;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import agent.Agent;


public class JeffersonCashierRole extends Role implements Cashier{

	private PersonAgent p;
	private String name;
	public List <Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	public List <Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	Timer timer = new Timer();
	public double profits;
	private boolean onDuty;
	
	
	
	
	
	public class Bill{
		
		public boolean paid;
		public double amount;
		public boolean needverify;
		Map<String, Integer> inventory;
		MarketDeliveryManRole deliveryPerson;
		
		
		public Bill(double a,  Map<String,Integer> inven,MarketDeliveryManRole dp ){
			paid=false;
			needverify=false;
			amount=a;
			
			inventory=inven;
			deliveryPerson=dp;
			
		}
		
		
	}
	
	public class Check{
		int tableNumber;
		Waiter w;
		public boolean processed;
		public boolean paid;
		public boolean complete;
		
		public Check(int t, Waiter wa){
			tableNumber=t;
			w=wa;
			processed=false;
			paid=false;
			complete=false;
		}
		
	}
	
	public JeffersonCashierRole(PersonAgent p,String name){
		super(p);
		this.name=name;
		profits=0;
	}
	
	
	// Messages
	
	public void msgGoOffDuty() {
		onDuty = false;
		stateChanged();
	}
	

	public void msgHereIsMarketBill(Map<String, Integer> inventory,
			double billAmount, MarketDeliveryManRole deliveryPerson) {
			bills.add(new Bill(billAmount, inventory, deliveryPerson));
			stateChanged();
		
	}



	public void msgHereIsChange(double amount,
			MarketDeliveryManRole deliveryPerson) {
			profits +=amount;
			for(Bill b:bills){
				if(b.amount==amount){
					b.needverify=true;
				}
			}
			
			stateChanged();
		
	}


	
	public void msgCustWantsCheck (int table, String Choice, Waiter w){
	
		checks.add(new Check(table,w));
		stateChanged();
	
	}

	
	public void msgHereisPayment(int table, Waiter w, double moneyPaid){
		synchronized (checks) {
		
			for(Check c:checks){
				if(c.tableNumber==table){
					c.paid=true;
					profits=profits+moneyPaid;
					Do("Customer payment recieved");
					stateChanged();	
				}	
			}
		}	
	}
	/*
	public void msgHereIsMarketBill(int amount, Market m){
		Do("Cashier recieved bill from market");
		bills.add(new Bill(amount, m));
		stateChanged();
	}
	*/
	/* Scheduler */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(bills){
			for(Bill b:bills){
				if(!b.paid){
					b.paid=true;
					verify(b);
					payMarket(b);
					return true;
				}
				if(b.needverify){
					b.needverify=false;
					tellDeliveryManVerified(b);
					return true;
				}
				
			}
		}
		
		synchronized(checks){
			for(Check c:checks){
				if(!c.processed){
					c.processed=true;
					processCheck(c);
					return true;
				}	
			}
		}	
		
		synchronized(checks){
			for(Check c:checks){
				if(c.paid && !c.complete){
					c.complete=true;
					completePayment(c);
					return true;
				}
			}
		}
		
		if(!onDuty){
			DoLeaveRestaurant();
			
		}
		
		return false;
	}


	



	//Actions

	private void DoLeaveRestaurant() {
		setInactive();
		onDuty=true;
		
	}


	private void tellDeliveryManVerified(Bill b) {
		b.deliveryPerson.msgChangeVerified("jeffersonrestaurant");
		
	}
	
	private void completePayment(Check c) {
		Do("Payment process cleared");
		c.w.msgPaymentComplete(c.tableNumber);
		
	}


	private void processCheck(final Check c) {
		timer.schedule(new TimerTask() {
			
			public void run() {
				print("Done processing check for table " + c.tableNumber);
				c.w.msgCheckPrinted(c.tableNumber);
				
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void payMarket (Bill b){
		Do("Paying Market");
		profits=profits-b.amount;
		if (profits<0){
			Do("Profits are in the negative");
		}
		
		//b.m.msgHereIsMonies(b.amount);
		// TODO deliveryperson.msgHereIsPayment(double)
		b.deliveryPerson.msgHereIsPayment(b.amount,"jeffersonrestaurant");
		
	}

	private void verify(Bill b){
		//TODO complete this once we established global prices from Ellen
		
	}

	@Override
	public void ReadyToPay(Customer c) {
		
		
	}


	@Override
	public void HereIsMymoney(Customer c, double money) {
		
		
	}



	
		
}