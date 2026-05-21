import java.util.ArrayList;

public class BikeRentalService {

    private ArrayList<Bike> bikes = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<Rental> rentals = new ArrayList<>();
    private ArrayList<Reservation> reservations = new ArrayList<>();
    private ArrayList<Helmet> helmets = new ArrayList<>();

    // ---------- Add / Register ----------

    public boolean addBike(Bike bike) {
        if (findBikeById(bike.getBikeId()) != null) {
            System.out.println("  [!] Bike ID \"" + bike.getBikeId() + "\" already exists. Skipping.");
            return false;
        }
        bikes.add(bike);
        return true;
    }

    public boolean registerCustomer(Customer customer) {
        if (findCustomer(customer.getCustomerId()) != null) {
            System.out.println("  [!] Customer ID \"" + customer.getCustomerId() + "\" already exists.");
            return false;
        }
        customers.add(customer);
        return true;
    }

    public void addRental(Rental rental) {
        rentals.add(rental);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void addHelmet(Helmet helmet) {
        for (Helmet h : helmets) {
            if (h.getHelmetId().equals(helmet.getHelmetId())) {
                System.out.println("  [!] Helmet ID \"" + helmet.getHelmetId() + "\" already exists. Skipping.");
                return;
            }
        }
        helmets.add(helmet);
    }

    // ---------- Display ----------

    public void displayAvailableBikes() {
        System.out.println("\n===== AVAILABLE BIKES =====");
        boolean found = false;
        for (Bike bike : bikes) {
            if (bike.isAvailable()) {
                bike.displayInfo();
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No bikes available at the moment.");
        }
        System.out.println();
    }

    public void displayAvailableHelmets() {
        System.out.println("\n===== AVAILABLE HELMETS =====");
        boolean found = false;
        for (Helmet helmet : helmets) {
            if (helmet.isAvailable()) {
                helmet.displayInfo();
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No helmets available at the moment.");
        }
        System.out.println();
    }

    public void displayActiveRentals() {
        System.out.println("\n===== ACTIVE RENTALS =====");
        boolean found = false;
        for (Rental rental : rentals) {
            if (!rental.isReturned()) {
                rental.displayRental();
                System.out.println("--------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No active rentals.");
        }
        System.out.println();
    }

    public void displayActiveReservations() {
        System.out.println("\n===== ACTIVE RESERVATIONS =====");
        boolean found = false;
        for (Reservation reservation : reservations) {
            if (reservation.isActive()) {
                reservation.displayReservation();
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No active reservations.");
        }
        System.out.println();
    }

    public void displayRentalHistory(String customerId) {
        Customer customer = findCustomer(customerId);

        if (customer == null) {
            System.out.println("  [!] Customer not found.\n");
            return;
        }

        System.out.println("\n===== RENTAL HISTORY =====");
        System.out.println("Customer : " + customer.getName());
        System.out.println("ID       : " + customer.getCustomerId());
        System.out.println("Contact  : " + customer.getContact());
        System.out.println("--------------------------");

        int totalRentals = 0;
        double totalSpent = 0;
        boolean found = false;

        for (Rental rental : rentals) {
            if (rental.getCustomer().getCustomerId().equals(customerId)) {
                System.out.println("Rental ID : " + rental.getRentalId());
                System.out.println("Bike      : " + rental.getBike().getBrand()
                        + " (" + rental.getBike().getBikeId() + ")"
                        + " - " + rental.getBike().getType());
                if (rental.getHelmet() != null) {
                    System.out.println("Helmet    : " + rental.getHelmet().getHelmetId()
                            + " | Fee: PHP " + rental.getHelmetFee());
                }
                System.out.println("Hours     : " + rental.getBookedHours());
                System.out.println("Cost      : PHP " + rental.getTotalCost());
                System.out.println("Status    : " + (rental.isReturned() ? "Returned" : "Active"));
                if (rental.getLateFee() > 0) {
                    System.out.println("Late Fee  : PHP " + rental.getLateFee());
                }
                if (rental.getDamagePenalty() > 0) {
                    System.out.println("Damage Fee: PHP " + rental.getDamagePenalty());
                }
                System.out.println("--------------------------");
                totalRentals++;
                totalSpent += rental.getTotalCost();
                found = true;
            }
        }

        if (!found) {
            System.out.println("  No rental records found for this customer.");
        } else {
            System.out.println("Total Rentals : " + totalRentals);
            System.out.println("Total Spent   : PHP " + totalSpent);
        }

        System.out.println("==========================\n");
    }

    // ---------- Search ----------

    public Customer findCustomer(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(id)) return c;
        }
        return null;
    }

    public Bike findBikeById(String id) {
        for (Bike b : bikes) {
            if (b.getBikeId().equals(id)) return b;
        }
        return null;
    }

    public Bike findBike(String id) {
        for (Bike b : bikes) {
            if (b.getBikeId().equals(id) && b.isAvailable()) return b;
        }
        return null;
    }

    public Rental findActiveRental(String rentalId) {
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId) && !r.isReturned()) return r;
        }
        return null;
    }

    public Reservation findActiveReservation(String reservationId) {
        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId) && r.isActive()) return r;
        }
        return null;
    }

    public Helmet findAvailableHelmet(String helmetId) {
        for (Helmet h : helmets) {
            if (h.getHelmetId().equals(helmetId) && h.isAvailable()) return h;
        }
        return null;
    }

    public boolean hasAvailableHelmet() {
        for (Helmet h : helmets) {
            if (h.isAvailable()) return true;
        }
        return false;
    }

    // ---------- Maintenance ----------

    public void displayBikesUnderMaintenance() {
        System.out.println("\n===== BIKES UNDER MAINTENANCE =====");
        boolean found = false;
        for (Bike bike : bikes) {
            if (bike.isUnderMaintenance()) {
                bike.displayInfo();
                found = true;
            }
        }
        if (!found) {
            System.out.println("  No bikes are currently under maintenance.");
        }
        System.out.println();
    }

    public boolean flagForMaintenance(String bikeId) {
        Bike bike = findBikeById(bikeId);
        if (bike == null) {
            System.out.println("  [!] Bike ID \"" + bikeId + "\" not found.");
            return false;
        }
        if (bike.isUnderMaintenance()) {
            System.out.println("  [!] Bike \"" + bikeId + "\" is already under maintenance.");
            return false;
        }
        bike.setUnderMaintenance(true);
        System.out.println("  [✓] Bike \"" + bikeId + "\" flagged for maintenance and marked unavailable.");
        return true;
    }

    public boolean clearFromMaintenance(String bikeId) {
        Bike bike = findBikeById(bikeId);
        if (bike == null) {
            System.out.println("  [!] Bike ID \"" + bikeId + "\" not found.");
            return false;
        }
        if (!bike.isUnderMaintenance()) {
            System.out.println("  [!] Bike \"" + bikeId + "\" is not under maintenance.");
            return false;
        }
        bike.setUnderMaintenance(false);
        bike.setCondition(Bike.Condition.GOOD);
        bike.setAvailable(true);
        System.out.println("  [✓] Bike \"" + bikeId + "\" cleared from maintenance and is now available.");
        return true;
    }

    // ---------- List Getters (used by GUI) ----------
    public ArrayList<Bike> getBikes()                  { return bikes; }
    public ArrayList<Customer> getCustomers()           { return customers; }
    public ArrayList<Rental> getRentals()               { return rentals; }
    public ArrayList<Reservation> getReservations()     { return reservations; }
    public ArrayList<Helmet> getHelmets()               { return helmets; }
}