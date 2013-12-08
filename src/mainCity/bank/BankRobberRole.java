package mainCity.bank;

import java.util.concurrent.Semaphore;

import role.Role;
import mainCity.PersonAgent;
import mainCity.bank.BankCustomerRole.BankCustomerState;
import mainCity.bank.gui.BankRobberGui;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;

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
		Do("bank robber initiated");
		this.p=p;
		this.name=name;
		
	}

	//Messages
	public void msgLeftBank() {
		atHome.release();		
	}
	
	public void msgAtBankManager(){
		atBankManager.release();
	}
	
	public void msgWantToRobBank(){
		brstate=BankRobberState.wantToRob;
		stateChanged();
		
	}
	
	public void msgOkayDontHurtMe(double bounty){
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

		
		
		return false;
	}
	
	//Actions
	
	private void robBank(){
		brGui.doGoToBankManager();
		try {
			atBankManager.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean bankClosed() {
		if(bm != null && bm.isActive() && bm.isOpen()){
			return false;
		}
		
		Do("customer checked and bank is closed");
		return true;
		}

	

}
