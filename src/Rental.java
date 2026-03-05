public class Rental {

    private String rentalId;
    private Customer customer;
    private Bike bike;
    private int hours;
    private double totalCost;

    public Rental(String rentalId, Customer customer, Bike bike, int hours) {
        this.rentalId = rentalId;
        this.customer = customer;
        this.bike = bike;
        this.hours = hours;
        this.totalCost = bike.calculateRentalCost(hours);
        bike.setAvailable(false);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void displayRental() {
        System.out.println("===== RENTAL DETAILS =====");
        System.out.println("Rental ID: " + rentalId);
        customer.displayCustomer();
        bike.displayInfo();
        System.out.println("Hours: " + hours);
        System.out.println("Total Cost: " + totalCost);
    }
}