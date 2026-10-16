public abstract class Person implements IPerson {
	
	protected String name;
	final protected int ID;
	protected String PhoneNumber;
	protected LinkedList<IRide> rideHistory;
	
	
	public  Person (String n , int id , String pn , LinkedList<IRide> rh) {
		name = n;
		ID = id;
		setPhoneNumber(pn);
		rideHistory = new LinkedList<IRide>();
		
		}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getId() {
		return  ID;
	}


	public void setPhoneNumber(String phoneNumber) {
		
		if ( phoneNumber == null || phoneNumber.length() != 10 )
			throw new IllegalArgumentException ("Phone number must be 10 digits.");
			
		for (int i = 0; i < phoneNumber.length(); i++) {
			if ( ! Character.isDigit(phoneNumber.charAt(i)) )
				throw new IllegalArgumentException("Phone number must contain numbers only.");
			}
		PhoneNumber = phoneNumber;
		
	}
	
	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public LinkedList<IRide> getRideHistory() {
		return rideHistory;
	}
	
	public String totring() {
		return "Name: " + name + "\nID: " + ID + "\nPhone Number: " + PhoneNumber + "\nRide History: " + rideHistory.toString() + "\n";
	}
	
	
}

