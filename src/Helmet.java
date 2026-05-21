public class Helmet {

    public enum Size {
        SMALL, MEDIUM, LARGE
    }

    private static final double HELMET_FEE = 50.00;
    
    // Track the global stock count directly inside the class
    private static int availableStock = 30; 

    private String helmetId;
    private Size size;
    private boolean isAvailable;

    public Helmet(String helmetId, Size size) {
        this.helmetId = helmetId;
        this.size = size;
        this.isAvailable = true;
    }

    public String getHelmetId() { return helmetId; }
    public Size getSize() { return size; }
    public boolean isAvailable() { return isAvailable; }
    
    // Updates the available stock pool when availability changes
    public void setAvailable(boolean available) { 
        if (this.isAvailable && !available) {
            availableStock--; // Helmet went from available to rented
        } else if (!this.isAvailable && available) {
            availableStock++; // Helmet was returned
        }
        this.isAvailable = available; 
    }

    // Static getter to check how many of the 30 helmets are left
    public static int getAvailableStock() { return availableStock; }
    public static double getHelmetFee() { return HELMET_FEE; }

    public void displayInfo() {
        System.out.println("Helmet ID : " + helmetId
                + " | Size: " + size
                + String.format(" | Fee: PHP %.2f", HELMET_FEE)
                + " | Status: " + (isAvailable ? "Available" : "In Use")
                + " | Total Stock Left: " + availableStock);
    }
}