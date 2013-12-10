package role.bank;

import java.util.concurrent.Semaphore;

import role.Role;
import role.bank.BankCustomerRole.BankCustomerState;
import mainCity.PersonAgent;
import mainCity.bank.gui.BankCustomerGui;
import mainCity.bank.gui.BankRobberGui;
import mainCity.bank.interfaces.BankRobber;
import mainCity.bank.interfaces.BankTeller;
import mainCity.bank.interfaces.Banker;
import mainCity.gui.trace.AlertLog;
import mainCity.gui.trace.AlertTag;

public class BankRobberRole extends Role implements BankRobber {
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
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#msgLeftBank()
	 */
	public void msgLeftBank() {
		log("finished leaving/robbing bank");
		atHome.release();	
		brstate=BankRobberState.left;
		setInactive();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#msgAtBankManager()
	 */
	public void msgAtBankManager(){
		log("at bank manager");
		atBankManager.release();
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#msgWantToRobBank()
	 */
	public void msgWantToRobBank(){
		log("recieved msgWantToRobBank");
		brstate=BankRobberState.wantToRob;
		stateChanged();
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#msgOkayDontHurtMe(double)
	 */
	public void msgOkayDontHurtMe(double bounty){
		log("recieved money from bankmanager");
		p.setCash(p.getCash()+bounty);
		brstate=BankRobberState.paid;
		stateChanged();
	}


	//Scheduler
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#pickAndExecuteAnAction()
	 */
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
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#log(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#bankClosed()
	 */
	public boolean bankClosed() {
		if(bm != null && bm.isActive() && bm.isOpen()){
		log("robber checked and bank is open");
			return false;
		}
		
		log("robber checked and bank is closed");
		return true;
		}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#setGui(mainCity.bank.gui.BankRobberGui)
	 */
	public void setGui(BankRobberGui brGui2) {
		this.brGui = brGui2;
		
	}
	
	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#getGui()
	 */
	public BankRobberGui getGui() {
		return brGui;
		}

	/* (non-Javadoc)
	 * @see mainCity.bank.BankRobber#setBankManager(mainCity.bank.BankManagerRole)
	 */
	public void setBankManager(BankManagerRole bankmanager) {
		this.bm=bankmanager;
	}

	

}
