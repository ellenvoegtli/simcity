package mainCity.restaurants.jeffersonrestaurant;

import mainCity.restaurants.jeffersonrestaurant.*;

import java.util.*;

import mainCity.restaurants.jeffersonrestaurant.MarketRole;
import mainCity.restaurants.jeffersonrestaurant.WaiterRole;
import mainCity.restaurants.jeffersonrestaurant.CookRole.Order;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Cashier;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Customer;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Market;
import mainCity.restaurants.jeffersonrestaurant.interfaces.Waiter;
import agent.Agent;


public class CashierRole extends Agent implements Cashier{

	private String name;
	public List <Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	public List <Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	Timer timer = new Timer();
	public double profits;
	
	
	
	public class Bill{
		Market m;
		public boolean paid;
		public double amount;
		
		public Bill(double a, Market Ma){
			paid=false;
			amount=a;
			m=Ma;
			
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
	
	public CashierRole(String name){
		super();
		this.name=name;
		profits=0;
	}
	
	
	// Messages
	
	public void msgHereIsMarketBill(Map<String, Integer> inventory,
			double billAmount, String deliveryPerson) {
		// TODO generate a bill with proper data 
		
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
	
	public void msgHereIsMarketBill(int amount, Market m){
		Do("Cashier recieved bill from market");
		bills.add(new Bill(amount, m));
		stateChanged();
	}

	/* Scheduler */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(bills){
			for(Bill b:bills){
				if(!b.paid){
					b.paid=true;
					payMarket(b);
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
		return false;
	}


	

	//Actions

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
		
		b.m.msgHereIsMonies(b.amount);
		// TODO c
		
	}


	@Override
	public void ReadyToPay(Customer c) {
		
		
	}


	@Override
	public void HereIsMymoney(Customer c, double money) {
		
		
	}


	
		
}