import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {

    private String reservationId;
    private Customer customer;
    private Bike bike;
    private LocalDateTime reservationDateTime;
    private int reservedHours;
    private boolean isActive;

    public Reservation(String reservationId, Customer customer, Bike bike,
            LocalDateTime reservationDateTime, int reservedHours) {

        this.reservationId = reservationId;
        this.customer = customer;
        this.bike = bike;
        this.reservationDateTime = reservationDateTime;
        this.reservedHours = reservedHours;
        this.isActive = true;

        // Bike becomes unavailable once reserved
        bike.setAvailable(false);
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

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public int getReservedHours() {
        return reservedHours;
    }

    public boolean isActive() {
        return isActive;
    }

    // ---------- Cancel Reservation ----------

    public void cancelReservation() {
        isActive = false;
        bike.setAvailable(true);

        System.out.println("Reservation cancelled successfully.");
    }

    // ---------- Confirm Reservation ----------

    public Rental confirmReservation() {

        if (!isActive) {
            System.out.println("Reservation is no longer active.");
            return null;
        }

        isActive = false;

        System.out.println("Reservation confirmed!");

        return new Rental(
                "R" + reservationId,
                customer,
                bike,
                reservedHours);
    }

    // ---------- Display Reservation ----------

    public void displayReservation() {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

        System.out.println("\n===== RESERVATION DETAILS =====");
        System.out.println("Reservation ID : " + reservationId);

        customer.displayCustomer();

        bike.displayInfo();

        System.out.println("Reserved Date  : "
                + reservationDateTime.format(format));

        System.out.println("Reserved Hours : " + reservedHours);

        System.out.println("Status          : "
                + (isActive ? "Active" : "Completed/Cancelled"));

        System.out.println("================================\n");
    }

    public int getBookedHoursLabel() {
        return reservedHours;
    }
}