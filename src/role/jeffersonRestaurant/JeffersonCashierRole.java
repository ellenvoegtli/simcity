package role.jeffersonRestaurant;

import java.util.*;

import role.Role;
import role.jeffersonRestaurant.JeffersonWaiterRole;
import role.jeffersonRestaurant.JeffersonCookRole.Order;
import role.market1.Market1DeliveryManRole;
import mainCity.PersonAgent;
import mainCity.contactList.ContactList;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;
import mainCity.interfaces.DeliveryMan;
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
		DeliveryMan deliveryPerson;
		
		
		public Bill(double a,  Map<String,Integer> inven,DeliveryMan dp ){
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
		onDuty=true;
		this.name=name;
		profits=0;
	}
	
	
	// Messages


	public void msgNotEnoughMoney(double amountOwed, double amountPaid) {
		// TODO Auto-generated method stub
		
	}

	public void msgGoOffDuty(double d) {
		addToCash(d);
		ContactList.getInstance().getBank().directDeposit("jeffersonrestaurant", profits*.5);
		profits = profits*.5;
		onDuty = false;
		stateChanged();
	}
	

	public void msgHereIsMarketBill(Map<String, Integer> inventory,
			double billAmount, DeliveryMan deliveryPerson) {
			log("recieved market bill");
			bills.add(new Bill(billAmount, inventory, deliveryPerson));
			stateChanged();
		
	}



	public void msgHereIsChange(double amount,
			DeliveryMan deliveryPerson) {
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
					log("Customer payment recieved");
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
		
		if(profits<=0){
			acquireFunds();
			return true;
			
		}
		
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
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_RESTAURANT, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.JEFFERSON_CASHIER, this.getName(), s);
	}
	private void acquireFunds(){
		ContactList.getInstance().getBank().directWithdraw("jeffersonrestaurant", 500);
		profits+=500;
		log("withdrawing money from bank for restaurant");
	}

	private void DoLeaveRestaurant() {
		setInactive();
		onDuty=true;
		
	}


	private void tellDeliveryManVerified(Bill b) {
		b.deliveryPerson.msgChangeVerified("jeffersonrestaurant");
		
	}
	
	private void completePayment(Check c) {
		log("Payment process cleared");
		c.w.msgPaymentComplete(c.tableNumber);
		
	}


	private void processCheck(final Check c) {
		timer.schedule(new TimerTask() {
			
			public void run() {
				log("Done processing check for table " + c.tableNumber);
				c.w.msgCheckPrinted(c.tableNumber);
				
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void payMarket (Bill b){
		log("Paying Market");
		profits=profits-b.amount;
		if (profits<0){
			log("Profits are in the negative");
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


	public void deductCash(double payroll) {
		profits-=payroll;
	}






	
		
}