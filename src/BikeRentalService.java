import java.util.ArrayList;

public class BikeRentalService {

    private ArrayList<Bike> bikes = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();

    public void addBike(Bike bike) {
        bikes.add(bike);
    }

    public void registerCustomer(Customer customer) {
        customers.add(customer);
    }

    public void displayAvailableBikes() {
        for (Bike bike : bikes) {
            if (bike.isAvailable()) {
                bike.displayInfo();
            }
        }
    }

    public Customer findCustomer(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public Bike findBike(String id) {
        for (Bike b : bikes) {
            if (b.getBikeId().equals(id) && b.isAvailable()) {
                return b;
            }
        }
        return null;
    }
}