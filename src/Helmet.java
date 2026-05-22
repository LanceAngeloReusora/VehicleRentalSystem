public class Helmet {

    public enum Size {
        SMALL, MEDIUM, LARGE
    }

    private static final double HELMET_FEE = 50.00;

    private String helmetId;
    private Size size;
    private boolean isAvailable;

    public Helmet(String helmetId, Size size) {
        this.helmetId = helmetId;
        this.size = size;
        this.isAvailable = true;
    }

    public String getHelmetId() {
        return helmetId;
    }

    public Size getSize() {
        return size;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    // Stock is derived at runtime from the live helmet list in BikeRentalService,
    // so we no longer need a static counter that could go stale across instances.
    public static double getHelmetFee() {
        return HELMET_FEE;
    }

    public void displayInfo() {
        System.out.println("Helmet ID : " + helmetId
                + " | Size: " + size
                + String.format(" | Fee: PHP %.2f", HELMET_FEE)
                + " | Status: " + (isAvailable ? "Available" : "In Use"));
    }
}