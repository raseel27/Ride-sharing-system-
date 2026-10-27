public class SharedRide extends Ride implements ISharedRide{
	
	private LinkedList<IRider> riders = new LinkedList<IRider>(); // several riders
	
	
	public SharedRide (String pickL , String dropL , IDateTime pickup , IDateTime dropoff , IDriver d, int id) {
		super (pickL , dropL , pickup , dropoff , d , id);
		
	}

	public LinkedList<IRider> getParticipants() {
		return riders;
	}

	
	public boolean addParticipant(IRider rider) { //adds rider if id doesnt exist already
		if (rider == null || hasRider(rider.getId()))
			return false;
	
		riders.insert(rider);
		return true;
	}

	
	public boolean removeParticipantById(int riderId) {  //searches for a rider by id and removes if found
		
		if (riders.empty())
			return false;
			
		riders.findFirst();
		while ( !riders.last() ) {
			if (riders.retrieve().getId() == riderId) {
				riders.remove();
				return true; 
				}
			riders.findNext();
		}
		
		if (riders.retrieve().getId() == riderId) {
			riders.remove();
			return true;
			}
		return false;
	}

	public boolean isEmpty() {
		return riders.empty();
	}


	public boolean hasRider(int riderId) { //looks through the partcipants of the ride searching for a specific rider by id
		
		if ( riders.empty())
			return false;
		riders.findFirst();
		while (! riders.last() ) {
			if (riders.retrieve().getId() == riderId)
				return true;
			riders.findNext();
		}
		
		return riders.retrieve().getId() == riderId;
		
	}

}
