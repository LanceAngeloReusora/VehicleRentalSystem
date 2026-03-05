public class Receipt {

    public static void printReceipt(Rental rental) {
        System.out.println("\n===== RECEIPT =====");
        rental.displayRental();
        System.out.println("====================\n");
    }
}