package role;
import mainCity.PersonAgent;
import agent.Agent;

public abstract class Role extends Agent {
	PersonAgent person;
	boolean isActive;

	Role(PersonAgent p, boolean a) {
		super();
		
		this.person = p;
		this.isActive = a;
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
/*
	protected void stateChanged() {
		person.stateChanged();
	}
*/
	public abstract boolean pickAndExecuteAnAction();
}
