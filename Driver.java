public class Driver extends Person implements IDriver{
	
	private String vehiclePlate;
	private VehicleType vehicleType;
	

	public Driver(String n, int id , String pn , String vp, String vt) {
		super(n, id, pn );
		vehiclePlate = vp;
		setVehiclePlate(vt);
		
		
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}


	public void setVehiclePlate(String vehiclePlate) {
		
		if (vehiclePlate.length() != 7)
			throw new IllegalArgumentException ("Vehicle plate should be 7 charachters.\n");
		
		for (int i = 0; i < 3 ; i ++) {
			if (!Character.isUpperCase(vehiclePlate.charAt(i)))
				throw new IllegalArgumentException ("First 3 characters must be upper case letters.\n");
		}
		
		for (int i = 3 ; i < 7 ; i++) {
			if (!Character.isDigit(vehiclePlate.charAt(i)))
				throw new IllegalArgumentException("Last 4 characters must be numbers.\n");
		}
					
		
		this.vehiclePlate = vehiclePlate;
	}


	public VehicleType getVehicleType() {
		return vehicleType;
	}


	public void setVehicleType(VehicleType vehicleType) {
		
	}

	public int compareTo(IDriver other) {
		
		if (ID == other.getId())
			return 0;
		
		return ID > other.getId()? 1 : -1;
	
	}
	
	public String toString() {
		
		return super.toString() + "\nVehicle Plate: " + vehiclePlate + "\nVehicle Type: " + vehicleType;
	}


}
