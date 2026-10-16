public class SharedRide extends Ride implements ISharedRide{
	
	private LinkedList<IRider> riders = new LinkedList<IRider>();
	
	
	public SharedRide (String pickL , String dropL , IDateTime pickup , IDateTime dropoff , IDriver d, int id) {
		super (pickL , dropL , pickup , dropoff , d , id);
		
	}

	public LinkedList<IRider> getParticipants() {
		return riders;
	}

	
	public boolean addParticipant(IRider rider) {
		if (rider == null || hasRider(rider.getId()))
			return false;
	
		riders.insert(rider);
		return true;
	}

	
	public boolean removeParticipantById(int riderId) { 
		
		if (riders == null || riders.empty())
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


	public boolean hasRider(int riderId) {
		
		if (riders == null || riders.empty())
			return false;
		
		while (! riders.last() ) {
			if (riders.retrieve().getId() == riderId)
				return true;
		}
		
		if (riders.retrieve().getId() == riderId)
			return true;
		
		return false;
	}

}
