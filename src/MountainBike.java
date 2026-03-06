public class MountainBike extends Bike {

public MountainBike(String id, String brand) {
    super(id, brand, 50);
}

@Override
public double calculateRentalCost(int hours) {
    return getRatePerHour() * hours;
}
}
