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
	
	protected PersonAgent getPerson() {
		return person;
	}

	protected void setPerson(PersonAgent p) {
		person = p;
	}

	protected void setActive() {
		isActive = true;
	}

	protected void setInactive() {
		isActive = false;
	}

	protected boolean isActive() {
		return isActive;
	}

	protected void stateChanged() {
		person.stateChanged();
	}
}
