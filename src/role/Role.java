package role;
import mainCity.Person;
import agent.Agent;

public abstract class Role extends Agent {
	protected Person person;
	protected boolean isActive;

	protected Role(Person p) {
		super();
		
		this.person = p;
		this.isActive = false;
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person p) {
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
