package housing.Interfaces;

import housing.OccupantRole;
import housing.LandlordRole;
import housing.gui.LandlordGui;

public interface landLord {


	public abstract void msgPleaseFix(Occupant occp, String appName);

	public abstract void msgAtDestination();

	void msgPleaseFix(OccupantRole occp, String appName);







}