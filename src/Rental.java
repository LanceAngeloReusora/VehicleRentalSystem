public class Rental {

    private static final double DAMAGE_PENALTY = 500.00;

    private String rentalId;
    private Customer customer;
    private Bike bike;
    private Helmet helmet; // nullable — optional
    private int bookedHours;
    private int actualHours;
    private double baseCost;
    private double helmetFee;
    private double lateFee;
    private double damagePenalty;
    private double totalCost;
    private boolean isReturned;

    // Without helmet
    public Rental(String rentalId, Customer customer, Bike bike, int bookedHours) {
        this(rentalId, customer, bike, bookedHours, null);
    }

    // With optional helmet
    public Rental(String rentalId, Customer customer, Bike bike, int bookedHours, Helmet helmet) {
        this.rentalId = rentalId;
        this.customer = customer;
        this.bike = bike;
        this.helmet = helmet;
        this.bookedHours = bookedHours;
        this.actualHours = bookedHours;

        this.baseCost = bike.calculateRentalCost(bookedHours);
        this.helmetFee = (helmet != null) ? Helmet.getHelmetFee() : 0;
        this.lateFee = 0;
        this.damagePenalty = 0;
        this.totalCost = baseCost + helmetFee;
        this.isReturned = false;

        bike.setAvailable(false);

        if (helmet != null) {
            helmet.setAvailable(false);
        }
    }

    public String getRentalId() {
        return rentalId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Bike getBike() {
        return bike;
    }

    public Helmet getHelmet() {
        return helmet;
    }

    public int getBookedHours() {
        return bookedHours;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getHelmetFee() {
        return helmetFee;
    }

    public double getLateFee() {
        return lateFee;
    }

    public double getDamagePenalty() {
        return damagePenalty;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void returnBike(int actualHours, Bike.Condition condition) {
        // Mark returned immediately — if anything below throws, the rental
        // is still considered closed rather than left in a half-returned state.
        this.isReturned = true;

        this.actualHours = actualHours;
        bike.setCondition(condition);

        if (actualHours > bookedHours) {
            int extraHours = actualHours - bookedHours;
            this.lateFee = bike.getRatePerHour() * extraHours * 1.5;
        }

        if (condition == Bike.Condition.DAMAGED) {
            this.damagePenalty = DAMAGE_PENALTY;
            bike.setUnderMaintenance(true);
        } else {
            bike.setAvailable(true);
        }

        if (helmet != null) {
            helmet.setAvailable(true);
        }

        this.totalCost = baseCost + helmetFee + lateFee + damagePenalty;
    }

    public void displayRental() {
        System.out.println("Rental ID     : " + rentalId);
        customer.displayCustomer();
        bike.displayInfo();

        if (helmet != null) {
            System.out.println("Helmet        : " + helmet.getHelmetId()
                    + " (Size: " + helmet.getSize()
                    + ") | Fee: PHP " + helmetFee);
        } else {
            System.out.println("Helmet        : None");
        }

        System.out.println("Booked Hours  : " + bookedHours);
        System.out.println("Actual Hours  : " + actualHours);
        System.out.println("Base Cost     : PHP " + baseCost);

        if (helmetFee > 0)
            System.out.println("Helmet Fee    : PHP " + helmetFee);
        if (lateFee > 0)
            System.out.println("Late Fee      : PHP " + lateFee);
        if (damagePenalty > 0)
            System.out.println("Damage Penalty: PHP " + damagePenalty);

        System.out.println("Total Cost    : PHP " + totalCost);
        System.out.println("Status        : " + (isReturned ? "Returned" : "Active"));
    }
}