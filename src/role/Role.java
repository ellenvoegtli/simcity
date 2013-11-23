package role;
import mainCity.PersonAgent;
import agent.Agent;

public abstract class Role extends Agent {
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
	
	protected double getCash() {
		return person.getCash();
	}
	
	protected int getTime() {
		return person.getTime();
	}	
}
