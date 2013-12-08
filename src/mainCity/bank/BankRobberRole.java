package mainCity.bank;

import java.util.concurrent.Semaphore;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.BankCustomerRole.BankCustomerState;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.bank.gui.BankRobberGui;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;

public class BankRobberRole extends Role {
		PersonAgent p;
		String name;
		BankManagerRole bm;
		BankRobberGui brGui;
		
		private Semaphore atHome = new Semaphore(0,false);
		
		private Semaphore atBankManager = new Semaphore(0, false);
		
		
		public enum BankRobberState{ none,wantToRob, moving, talking, paid, leaving,left	}
		
		BankRobberState brstate=BankRobberState.none;
		
		
	public BankRobberRole(PersonAgent p, String name) {
		super(p);
		log("bank robber initiated");
		this.p=p;
		this.name=name;
		
	}

	//Messages
	public void msgLeftBank() {
		log("finished leaving/robbing bank");
		atHome.release();	
		brstate=BankRobberState.left;
		setInactive();
	}
	
	public void msgAtBankManager(){
		log("at bank manager");
		atBankManager.release();
	}
	
	public void msgWantToRobBank(){
		log("recieved msgWantToRobBank");
		brstate=BankRobberState.wantToRob;
		stateChanged();
		
	}
	
	public void msgOkayDontHurtMe(double bounty){
		log("recieved money from bankmanager");
		p.setCash(p.getCash()+bounty);
		brstate=BankRobberState.paid;
		stateChanged();
	}


	//Scheduler
	public boolean pickAndExecuteAnAction() {
		
		if(brstate==BankRobberState.wantToRob){
			brstate=BankRobberState.moving;
			robBank();
			return true;
		}
		
		if(brstate==BankRobberState.paid){
			brstate=BankRobberState.leaving;
			doLeaveBank();
			
			return true;
			
		}

		
		
		return false;
	}
	
	//Actions
	
	public void log(String s){
        AlertLog.getInstance().logMessage(AlertTag.BANK, this.getName(), s);
        AlertLog.getInstance().logMessage(AlertTag.BANK_ROBBER, this.getName(), s);
	}
	
	private void robBank(){
		log("robbing bank");
		brGui.doGoToBankManager();
		try {
			atBankManager.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bm.msgGiveMeTheMoney(this,5000);
		
	}
	private void doLeaveBank() {
		log("leaving bank");
		brGui.DoLeaveBank();
		try {
			atHome.acquire();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		
	}	

	public boolean bankClosed() {
		if(bm != null && bm.isActive() && bm.isOpen()){
		log("robber checked and bank is open");
			return false;
		}
		
		log("robber checked and bank is closed");
		return true;
		}

	public void setGui(BankRobberGui brGui2) {
		this.brGui = brGui2;
		
	}
	
	public BankRobberGui getGui() {
		return brGui;
		}

	public void setBankManager(BankManagerRole bankmanager) {
		this.bm=bankmanager;
	}

	

}
