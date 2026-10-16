public class PrivateRide extends Ride implements IPrivateRide{
	
	private IRider rider;
	
	public PrivateRide (String pickL , String dropL , IDateTime pickup , IDateTime dropoff , IDriver d , int id, IRider r) {
		super (pickL , dropL , pickup , dropoff , d , id);
		rider = r;
	}


	public IRider getRider() {
		return rider;
	}

	
	public void setRider(IRider rider) {
		this.rider = rider;
	}

	
	public boolean hasRider(int riderId) {
		
		return rider != null && rider.getId() == riderId ;
	}
	
	public String toString() {
		return super.toString() + "\nRider: " + rider.toString();
	}

}
