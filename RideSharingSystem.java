import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RideSharingSystem implements IRideSharingSystem {
    private RiderList riders;
    private DriverList drivers;
    private RideList rides;

    public RideSharingSystem() {
        this.riders = new RiderList();
        this.drivers = new DriverList();
        this.rides = new RideList();
    }

    // open file and read riders line by line
    public boolean loadRidersFromCSV(String ridersFilePath) {
        Scanner reader = null;
        try {
            reader = new Scanner(new File(ridersFilePath));
            while (reader.hasNextLine()) {
                String rowText = reader.nextLine();
                if (rowText == null || rowText.trim().isEmpty()) continue;
                
                String[] parts = rowText.split(",");
                if (parts.length < 5) continue;
                
                try {
                    int idVal = Integer.parseInt(parts[0].trim());
                    String nameVal = parts[1].trim();
                    String emailVal = parts[2].trim();
                    String phoneVal = parts[3].trim();
                    String cityVal = parts[4].trim();
                    
                    addRider(new Rider(idVal, nameVal, emailVal, phoneVal, cityVal));
                } catch (Exception parseErr) {
                    // skip bad row
                }
            }
            reader.close();
            return true;
        } catch (FileNotFoundException ex) {
            if (reader != null) reader.close();
            return false;
        }
    }

    // read drivers from file
    public boolean loadDriversFromCSV(String driversFilePath) {
        Scanner reader = null;
        try {
            reader = new Scanner(new File(driversFilePath));
            while (reader.hasNextLine()) {
                String rowText = reader.nextLine();
                if (rowText == null || rowText.trim().isEmpty()) continue;
                
                String[] parts = rowText.split(",");
                if (parts.length < 5) continue;
                
                try {
                    int idVal = Integer.parseInt(parts[0].trim());
                    String nameVal = parts[1].trim();
                    String phoneVal = parts[2].trim();
                    String plateVal = parts[3].trim();
                    VehicleType vType = VehicleType.valueOf(parts[4].trim());
                    
                    addDriver(new Driver(idVal, nameVal, phoneVal, plateVal, vType));
                } catch (Exception parseErr) {
                }
            }
            reader.close();
            return true;
        } catch (FileNotFoundException ex) {
            if (reader != null) reader.close();
            return false;
        }
    }

    // read rides from csv file
    public boolean loadRidesFromCSV(String ridesFilePath) {
        Scanner reader = null;
        try {
            reader = new Scanner(new File(ridesFilePath));
            while (reader.hasNextLine()) {
                String rowText = reader.nextLine();
                if (rowText == null || rowText.trim().isEmpty()) continue;
                
                String[] parts = rowText.split(",");
                try {
                    String rideType = parts[0].trim();
                    String pickupLoc = parts[1].trim();
                    IDateTime pTime = DateTime.parse(parts[2].trim());
                    IDateTime dTime = DateTime.parse(parts[3].trim());
                    String dropoffLoc = parts[4].trim();
                    int dId = Integer.parseInt(parts[5].trim());
                    String[] idsList = parts[6].trim().split(";");
                    
                    if (rideType.equals("PRIVATE")) {
                        int rId = Integer.parseInt(idsList[0].trim());
                        schedulePrivateRide(pickupLoc, pTime, dTime, dropoffLoc, rId, dId);
                    } else {
                        int[] groupIds = new int[idsList.length];
                        for (int i = 0; i < idsList.length; i++) {
                            groupIds[i] = Integer.parseInt(idsList[i].trim());
                        }
                        scheduleSharedRide(pickupLoc, pTime, dTime, dropoffLoc, groupIds, dId);
                    }
                } catch (Exception parseErr) {
                }
            }
            reader.close();
            return true;
        } catch (FileNotFoundException ex) {
            if (reader != null) reader.close();
            return false;
        }
    }

    public boolean addRider(IRider rider) {
        if (rider == null) return false;
        if (riders.findByEmail(rider.getEmail()) != null) return false;
        return riders.add(rider);
    }

    public boolean addDriver(IDriver driver) {
        if (driver == null) return false;
        if (drivers.findByVehiclePlate(driver.getVehiclePlate()) != null) return false;
        return drivers.add(driver);
    }

    public IRider searchRiderById(int riderId) {
        return riders.findById(riderId);
    }

    public IRider searchRiderByEmail(String email) {
        return riders.findByEmail(email);
    }

    public LinkedList<IRider> searchRidersByName(String fullName) {
        return riders.findByName(fullName);
    }

    public LinkedList<IRider> searchRidersByHomeCity(String homeCity) {
        return riders.findByHomeCity(homeCity);
    }

    public LinkedList<IRider> getAllRiders() {
        return riders.getAll();
    }

    public IDriver searchDriverById(int driverId) {
        return drivers.findById(driverId);
    }

    public IDriver searchDriverByVehiclePlate(String vehiclePlate) {
        return drivers.findByVehiclePlate(vehiclePlate);
    }

    public LinkedList<IDriver> searchDriversByVehicleType(VehicleType vehicleType) {
        return drivers.findByVehicleType(vehicleType);
    }

    public LinkedList<IDriver> getAllDrivers() {
        return drivers.getAll();
    }

    public LinkedList<IRide> searchRidesByPickupLocation(String pickupLocation) {
        return rides.findByPickupLocation(pickupLocation);
    }

    public LinkedList<IRide> searchRidesByRiderName(String riderName) {
        return rides.findByRiderName(riderName);
    }

    public LinkedList<IRide> getAllRidesAlphabetically() {
        return rides.getAllAlphabetically();
    }

    public LinkedList<IRider> getSharedRideParticipants(String pickupLocation) {
        LinkedList<IRider> outputList = new LinkedList<IRider>();
        LinkedList<IRide> foundRides = rides.findByPickupLocation(pickupLocation);
        
        if (foundRides == null || foundRides.empty()) return outputList;
        
        foundRides.findFirst();
        boolean finished = false;
        while (!finished) {
            IRide curRide = foundRides.retrieve();
            copyRiders(curRide, outputList);
            
            if (foundRides.last()) {
                finished = true;
            } else {
                foundRides.findNext();
            }
        }
        return outputList;
    }

    // schedule private ride with different logic structure
    public boolean schedulePrivateRide(String pickupLocation, IDateTime pickupTime,
                                       IDateTime dropoffTime, String dropoffLocation,
                                       int riderId, int driverId) {
        if (pickupTime == null || dropoffTime == null) return false;
        if (pickupTime.compareTo(dropoffTime) >= 0) return false;
        
        IRider rObj = riders.findById(riderId);
        IDriver dObj = drivers.findById(driverId);
        
        if (rObj == null || dObj == null) return false;
        
        boolean riderBusy = hasConflict(rObj, pickupTime, dropoffTime);
        boolean driverBusy = hasConflict(dObj, pickupTime, dropoffTime);
        
        if (riderBusy || driverBusy) return false;

        IRide newPrivRide = new PrivateRide(pickupLocation, pickupTime, dropoffTime, dropoffLocation, dObj, rObj);
        rides.addRide(newPrivRide);
        rObj.getRideHistory().insert(newPrivRide);
        dObj.getRideHistory().insert(newPrivRide);
        return true;
    }

    // schedule shared ride with structured checks
    public boolean scheduleSharedRide(String pickupLocation, IDateTime pickupTime,
                                      IDateTime dropoffTime, String dropoffLocation,
                                      int[] riderIds, int driverId) {
        if (pickupTime == null || dropoffTime == null || pickupTime.compareTo(dropoffTime) >= 0) return false;
        if (riderIds == null || riderIds.length < 2) return false;
        
        IDriver dObj = drivers.findById(driverId);
        if (dObj == null || hasConflict(dObj, pickupTime, dropoffTime)) return false;
        
        IRider[] groupRiders = new IRider[riderIds.length];
        for (int i = 0; i < riderIds.length; i++) {
            groupRiders[i] = riders.findById(riderIds[i]);
            if (groupRiders[i] == null) return false;
            if (hasConflict(groupRiders[i], pickupTime, dropoffTime)) return false;
            
            for (int k = 0; k < i; k++) {
                if (riderIds[k] == riderIds[i]) return false;
            }
        }

        SharedRide newSharedRide = new SharedRide(pickupLocation, pickupTime, dropoffTime, dropoffLocation, dObj);
        for (int i = 0; i < groupRiders.length; i++) {
            newSharedRide.addParticipant(groupRiders[i]);
            groupRiders[i].getRideHistory().insert(newSharedRide);
        }
        
        rides.addRide(newSharedRide);
        dObj.getRideHistory().insert(newSharedRide);
        return true;
    }

    // remove rider and clean his rides
    public boolean removeRider(int riderId) {
        IRider targetRider = riders.findById(riderId);
        if (targetRider == null) return false;
        
        LinkedList<IRide> hist = targetRider.getRideHistory();
        while (hist != null && !hist.empty()) {
            hist.findFirst();
            IRide rideItem = hist.retrieve();
            
            if (rideItem instanceof ISharedRide) {
                ISharedRide shRide = (ISharedRide) rideItem;
                shRide.removeParticipantById(riderId);
                removeFromHistory(targetRider, rideItem);
                if (shRide.isEmpty()) {
                    deleteRide(rideItem);
                }
            } else {
                deleteRide(rideItem);
            }
        }
        return riders.removeById(riderId);
    }

    // remove driver and delete all his rides
    public boolean removeDriver(int driverId) {
        IDriver targetDriver = drivers.findById(driverId);
        if (targetDriver == null) return false;
        
        LinkedList<IRide> hist = targetDriver.getRideHistory();
        while (hist != null && !hist.empty()) {
            hist.findFirst();
            deleteRide(hist.retrieve());
        }
        return drivers.removeById(driverId);
    }

    // check time conflict using flag logic
    private boolean hasConflict(IPerson personObj, IDateTime startT, IDateTime endT) {
        if (personObj == null) return false;
        LinkedList<IRide> histList = personObj.getRideHistory();
        if (histList == null || histList.empty()) return false;
        
        boolean conflictFound = false;
        histList.findFirst();
        boolean stopLoop = false;
        
        while (!stopLoop && !conflictFound) {
            IRide currentRide = histList.retrieve();
            if (overlaps(currentRide, startT, endT)) {
                conflictFound = true;
            } else {
                if (histList.last()) {
                    stopLoop = true;
                } else {
                    histList.findNext();
                }
            }
        }
        return conflictFound;
    }

    // check overlap condition separated
    private boolean overlaps(IRide existingRide, IDateTime newStart, IDateTime newEnd) {
        if (existingRide == null) return false;
        boolean startsBeforeEnd = newStart.compareTo(existingRide.getDropoffTime()) < 0;
        boolean endsAfterStart = existingRide.getPickupTime().compareTo(newEnd) < 0;
        return startsBeforeEnd && endsAfterStart;
    }

    // delete ride completely from system
    private void deleteRide(IRide targetRide) {
        if (targetRide == null) return;
        
        rides.removeRideById(targetRide.getRideId());
        
        if (targetRide.getDriver() != null) {
            removeFromHistory(targetRide.getDriver(), targetRide);
        }
        
        if (targetRide instanceof IPrivateRide) {
            IPrivateRide pRide = (IPrivateRide) targetRide;
            if (pRide.getRider() != null) {
                removeFromHistory(pRide.getRider(), targetRide);
            }
        } else if (targetRide instanceof ISharedRide) {
            ISharedRide sRide = (ISharedRide) targetRide;
            LinkedList<IRider> partList = sRide.getParticipants();
            if (partList != null && !partList.empty()) {
                partList.findFirst();
                boolean endParts = false;
                while (!endParts) {
                    removeFromHistory(partList.retrieve(), targetRide);
                    if (partList.last()) {
                        endParts = true;
                    } else {
                        partList.findNext();
                    }
                }
            }
        }
    }

    // remove specific ride from person history
    private void removeFromHistory(IPerson personObj, IRide targetRide) {
        if (personObj == null || targetRide == null) return;
        LinkedList<IRide> hCollection = personObj.getRideHistory();
        if (hCollection == null || hCollection.empty()) return;
        
        hCollection.findFirst();
        boolean finished = false;
        while (!finished) {
            IRide item = hCollection.retrieve();
            if (item != null && item.getRideId() == targetRide.getRideId()) {
                hCollection.remove();
                return;
            }
            if (hCollection.last()) {
                finished = true;
            } else {
                hCollection.findNext();
            }
        }
    }

    // copy participants without duplicates
    private void copyRiders(IRide rideItem, LinkedList<IRider> targetCol) {
        if (!(rideItem instanceof ISharedRide)) return;
        ISharedRide shObj = (ISharedRide) rideItem;
        LinkedList<IRider> pSource = shObj.getParticipants();
        if (pSource == null || pSource.empty()) return;
        
        pSource.findFirst();
        boolean done = false;
        while (!done) {
            addIfNew(targetCol, pSource.retrieve());
            if (pSource.last()) {
                done = true;
            } else {
                pSource.findNext();
            }
        }
    }

    // check if rider already in list before adding
    private void addIfNew(LinkedList<IRider> collectionRef, IRider riderItem) {
        if (riderItem == null) return;
        if (collectionRef.empty()) {
            collectionRef.insert(riderItem);
            return;
        }
        
        boolean alreadyExists = false;
        collectionRef.findFirst();
        boolean endCheck = false;
        
        while (!endCheck && !alreadyExists) {
            IRider currentItem = collectionRef.retrieve();
            if (currentItem != null && currentItem.getId() == riderItem.getId()) {
                alreadyExists = true;
            } else {
                if (collectionRef.last()) {
                    endCheck = true;
                } else {
                    collectionRef.findNext();
                }
            }
        }
        
        if (!alreadyExists) {
            collectionRef.insert(riderItem);
        }
    }
}
