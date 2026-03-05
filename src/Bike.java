public abstract class Bike {

private String bikeId;
private String brand;
private double ratePerHour;
private boolean isAvailable;

public Bike(String bikeId, String brand, double ratePerHour) {
    this.bikeId = bikeId;
    this.brand = brand;
    this.ratePerHour = ratePerHour;
    this.isAvailable = true;
}

public String getBikeId() {
    return bikeId;
}

public String getBrand() {
    return brand;
}

public double getRatePerHour() {
    return ratePerHour;
}

public boolean isAvailable() {
    return isAvailable;
}

public void setAvailable(boolean available) {
    this.isAvailable = available;
}

public abstract double calculateRentalCost(int hours);

public void displayInfo() {
    System.out.println("ID: " + bikeId + " | Brand: " + brand + " | Rate/hr: " + ratePerHour + " | Available: " + isAvailable);
}
}
