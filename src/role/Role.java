package role;
import agent.Agent;
import mainCity.PersonAgent;

public abstract class Role extends Agent{
	protected PersonAgent person;
	protected boolean isActive;

	protected Role(PersonAgent p) {
		super();
		
		this.person = p;
		this.isActive = false;
	}
	
	public PersonAgent getPerson() {
		return person;
	}

	public void setPerson(PersonAgent p) {
		person = p;
	}

	public void setActive() {
		isActive = true;
	}

	public void setInactive() {
		System.out.println("============== ROLE: SET INACTIVE ================");
		isActive = false;
		this.person.roleInactive();
	}

	public boolean isActive() {
		return isActive;
	}

	protected void stateChanged() {
		person.stateChanged();
	}

	public abstract boolean pickAndExecuteAnAction();
	
	protected void addToCash(double amount) {
		person.setCash(person.getCash() + amount);
	}
	
	protected double getCash() {
		return person.getCash();
	}
	
	protected int getTime() {
		return person.getTime();
	}
	
	public int getShiftDuration() {
		return person.getWorkHours();
	}
}
