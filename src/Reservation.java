import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {

    private String reservationId;
    private Customer customer;
    private Bike bike;
    private Helmet helmet; // optional — may be null
    private LocalDateTime reservationDateTime;
    private int reservedHours;
    private boolean isActive;

    // Without helmet
    public Reservation(String reservationId, Customer customer, Bike bike,
            LocalDateTime reservationDateTime, int reservedHours) {
        this(reservationId, customer, bike, reservationDateTime, reservedHours, null);
    }

    // With optional helmet
    public Reservation(String reservationId, Customer customer, Bike bike,
            LocalDateTime reservationDateTime, int reservedHours, Helmet helmet) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.bike = bike;
        this.helmet = helmet;
        this.reservationDateTime = reservationDateTime;
        this.reservedHours = reservedHours;
        this.isActive = true;

        // Bike (and helmet if present) become unavailable once reserved
        bike.setAvailable(false);
        if (helmet != null)
            helmet.setAvailable(false);
    }

    // ---------- Getters ----------

    public String getReservationId() {
        return reservationId;
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

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public int getReservedHours() {
        return reservedHours;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getBookedHoursLabel() {
        return reservedHours;
    }

    // ---------- Cancel ----------

    public void cancelReservation() {
        isActive = false;
        bike.setAvailable(true);
        if (helmet != null)
            helmet.setAvailable(true);
        System.out.println("Reservation cancelled successfully.");
    }

    // ---------- Confirm → creates a Rental carrying the same helmet ----------

    public Rental confirmReservation() {
        if (!isActive) {
            System.out.println("Reservation is no longer active.");
            return null;
        }
        isActive = false;
        System.out.println("Reservation confirmed!");
        // Pass the reserved helmet (may be null) directly into the Rental.
        // Rental constructor will call bike.setAvailable(false) and
        // helmet.setAvailable(false) again — both are already false, so
        // the net effect is correct and idempotent.
        return new Rental("R" + reservationId, customer, bike, reservedHours, helmet);
    }

    // ---------- Display ----------

    public void displayReservation() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        System.out.println("\n===== RESERVATION DETAILS =====");
        System.out.println("Reservation ID : " + reservationId);
        customer.displayCustomer();
        bike.displayInfo();
        if (helmet != null)
            System.out.println("Helmet         : " + helmet.getHelmetId() + " (Size: " + helmet.getSize() + ")");
        System.out.println("Reserved Date  : " + reservationDateTime.format(format));
        System.out.println("Reserved Hours : " + reservedHours);
        System.out.println("Status         : " + (isActive ? "Active" : "Completed/Cancelled"));
        System.out.println("================================\n");
    }
}