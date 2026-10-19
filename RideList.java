public class RideList implements IRideList {
    private Node<IRide> head;
    private int size;

    public RideList() {
        head = null;
        size = 0;
    }

    public boolean addRide(IRide ride) {
    	if (ride == null) {
            return false;
        }

        Node<IRide> check = head;

        while (check != null) {
            if (check.data.getRideId() == ride.getRideId()) {
                return false;
            }
            check = check.next;
        }

        Node<IRide> newNode = new Node<IRide>(ride);

        if (head == null) {
            head = newNode;
        }

        else if (ride.compareTo(head.data) < 0) {
            newNode.next = head;
            head = newNode;
        }

        else {
            Node<IRide> current = head;

            while (current.next != null &&
                   current.next.data.compareTo(ride) <= 0) {
                current = current.next;
            }

            newNode.next = current.next;
            current.next = newNode;
        }

        size++;
        return true;
    }

    public boolean removeRideById(int rideId) {
    	if (head == null) {
            return false;
        }

        if (head.data.getRideId() == rideId) {
            head = head.next;
            size--;
            return true;
        }

        Node<IRide> current = head;

        while (current.next != null) {

            int nextRideId = current.next.data.getRideId();

            if (nextRideId == rideId) {
                current.next = current.next.next;
                size--;
                return true;
            }

            current = current.next;
        }
    	return false;
    }

    public LinkedList<IRide> getAllAlphabetically() {
    	LinkedList<IRide> sortedRides = new LinkedList<IRide>();
        Node<IRide> current = head;

        // Copy all rides in their alphabetical order
        while (current != null) {
            sortedRides.insert(current.data);
            current = current.next;
        }

        return sortedRides;
    }

    public LinkedList<IRide> findByPickupLocation(String pickupLocation) {
    	LinkedList<IRide> matchingRides = new LinkedList<IRide>();

        if (pickupLocation == null) {
            return matchingRides;
        }

        Node<IRide> current = head;

        while (current != null) {

            if (pickupLocation.equalsIgnoreCase(current.data.getPickupLocation())) {
                matchingRides.insert(current.data);
            }

            current = current.next;
        }

        return matchingRides;
    }

    public LinkedList<IRide> findByRiderName(String riderFullName) {
    	LinkedList<IRide> matchingRides = new LinkedList<IRide>();

        // Check if the name is null
        if (riderFullName == null) {
            return matchingRides;
        }

        Node<IRide> current = head;

        // Search through all rides
        while (current != null) {

            if (hasRiderNamed(current.data, riderFullName)) {
                matchingRides.insert(current.data);
            }

            current = current.next;
        }

        return matchingRides;	 
    }

    private boolean hasRiderNamed(IRide ride, String name) {
    	if (ride == null || name == null) {
            return false;
        }

        if (ride instanceof IPrivateRide) {

            IPrivateRide privateRide = (IPrivateRide) ride;
            IRider rider = privateRide.getRider();

            return rider != null && name.equals(rider.getName());
        }

        if (ride instanceof ISharedRide) {

            ISharedRide sharedRide = (ISharedRide) ride;
            LinkedList<IRider> participants = sharedRide.getParticipants();

            if (participants == null || participants.empty()) {
                return false;
            }

            participants.findFirst();

            while (true) {

                IRider currentRider = participants.retrieve();

                if (currentRider != null &&
                    name.equals(currentRider.getName())) {
                    return true;
                }

                if (participants.last()) {
                    break;
                }

                participants.findNext();
            }
        }

        return false;
    }

    public int size() {
        return size;
    }
}
