package mainCity.test;

public class Mock {
	public String name;
	public EventLog log = new EventLog();

	public Mock(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getName() + ": " + name;
	}
	
	public EventLog getLog() {
		return log;
	}
}
