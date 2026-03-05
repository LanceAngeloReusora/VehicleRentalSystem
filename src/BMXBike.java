public class BMXBike extends Bike {

    public BMXBike(String id, String brand) {
        super(id, brand, 40); 
}

@Override
public double calculateRentalCost(int hours) {
return getRatePerHour() * hours;
}
}
