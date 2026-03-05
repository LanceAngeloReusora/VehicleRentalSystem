public class JapaneseBike extends Bike {
    public JapaneseBike(String id, String brand) {
        super(id, brand, 45); 
    }
    
    @Override
    public double calculateRentalCost(int hours) {
        return getRatePerHour() * hours;
    }
}
