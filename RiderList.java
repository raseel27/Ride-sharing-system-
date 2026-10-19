public class RiderList implements IRiderList {
    private Node<IRider> head;
    private int size;

    public RiderList() {
        head = null;
        size = 0;
    }

    public boolean add(IRider rider) {
    	//check if the rider is null
    	if(rider == null) {
        return false;
    }
    	//the list is empty
    	if (head == null) {
            head = new Node<IRider>(rider);
            size++;
            return true;
        }
    	
    	if (rider.compareTo(head.data) < 0) {
            Node<IRider> newNode = new Node<IRider>(rider);
            newNode.next  = head;
            head =  newNode;
            size++ ;
            return true;
        }
    	Node<IRider> current = head;

        while (current.next != null &&
               current.next.data.compareTo(rider) < 0) {
            current = current.next;
        }
        if (current.data.compareTo(rider) == 0 || (current.next != null && current.next.data.compareTo(rider) == 0)) {
                return false;
            }
        Node<IRider> newNode = new Node<IRider>(rider);
        newNode.next = current.next;
        current.next = newNode;
        size++;

        return true;
    }
    
    public IRider findById(int riderId) {
    	Node<IRider> current = head;

        while (current != null) {

            int currentId = current.data.getId();
            if (currentId == riderId) {
                return current.data;
            }
            //stop searching if we passed the rider id
            if (currentId > riderId) {
                return null;
            }

            current = current.next;
        }
        return null;
    }

    public LinkedList<IRider> findByName(String fullName) {
    	LinkedList<IRider> matches = new LinkedList<IRider>();
    	if (fullName == null) {
            return matches;
        }
    	Node<IRider> current = head;
    	while (current != null) {

            if (fullName.equals(current.data.getName())) {
                matches.insert(current.data);
            }

            current = current.next;
        }

        return matches;
    }

    public IRider findByEmail(String email) {
    	if (email == null) {
            return null;
        }

        Node<IRider> current = head;

        while (current != null) {

            if (email.equalsIgnoreCase(current.data.getEmail())) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    
    public LinkedList<IRider> findByHomeCity(String homeCity) {
    	LinkedList<IRider> matches = new LinkedList<IRider>();

        if (homeCity == null) {
            return matches;
        }

        Node<IRider> current = head;
        while (current != null) {

            if (homeCity.equalsIgnoreCase(current.data.getHomeCity())) {
                matches.insert(current.data);
            }

            current = current.next;
        }

        return matches;
    }

    public LinkedList<IRider> getAll() {
    	LinkedList<IRider> allRiders = new LinkedList<IRider>();
        Node<IRider> current = head;
        while (current != null) {
            allRiders.insert(current.data);
            current = current.next;
        }

        return allRiders;
    }

    public boolean removeById(int riderId) {
    	if (head == null) {
            return false;
        }

        if (head.data.getId() == riderId) {
            head = head.next;
            size--;
            return true;
        }

        if (head.data.getId() > riderId) {
            return false;
        }

        Node<IRider> current = head;

        while (current.next != null) {

            int nextId = current.next.data.getId();

            if (nextId == riderId) {
                current.next = current.next.next;
                size--;
                return true;
            }

            if (nextId > riderId) {
                return false;
            }

            current = current.next;
        }
    	return false;
    }

    public boolean removeByEmail(String email) {
    	if (email == null || head == null) {
            return false;
        }

        if (email.equalsIgnoreCase(head.data.getEmail())) {
            head = head.next;
            size--;
            return true;
        }

        Node<IRider> current = head;

        while (current.next != null) {

            if (email.equalsIgnoreCase(current.next.data.getEmail())) {
                current.next = current.next.next;
                size--;
                return true;
            }

            current = current.next;
        }
    	return false;
    }

    public int removeByName(String fullName) {
    	if (fullName == null) {
            return 0;
        }

        int removedCount = 0;

        while (head != null && fullName.equals(head.data.getName())) {
            head = head.next;
            removedCount++;
        }

        Node<IRider> current = head;

        while (current != null && current.next != null) {

            if (fullName.equals(current.next.data.getName())) {
                current.next = current.next.next;
                removedCount++;
            } else {
                current = current.next;
            }
        }

        size -= removedCount;

        return removedCount;
    }

    public int removeByHomeCity(String homeCity) {
    	if (homeCity == null) {
            return 0;
        }

        int removedCount = 0;

        while (head != null &&
               homeCity.equalsIgnoreCase(head.data.getHomeCity())) {

            head = head.next;
            removedCount++;
        }

        Node<IRider> current = head;

        while (current != null && current.next != null) {

            if (homeCity.equalsIgnoreCase(current.next.data.getHomeCity())) {
                current.next = current.next.next;
                removedCount++;
            } else {
                current = current.next;
            }
        }

        size -= removedCount;

        return removedCount;
    }

    public int size() {
     return size;
    }
}

