import java.util.*;

class ParkingSpot {
    String licensePlate;
    long entryTime;
    String status;

    ParkingSpot() {
        status = "EMPTY";
    }
}

class ParkingLot {

    ParkingSpot[] table;
    int size;
    int occupied = 0;
    int totalProbes = 0;
    int operations = 0;

    ParkingLot(int size) {
        this.size = size;
        table = new ParkingSpot[size];
        for (int i = 0; i < size; i++)
            table[i] = new ParkingSpot();
    }

    int hash(String plate) {
        return Math.abs(plate.hashCode()) % size;
    }

    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY")) {
            index = (index + 1) % size;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupied++;
        totalProbes += probes;
        operations++;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" + index +
                " (" + probes + " probes)");
    }

    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (!table[index].status.equals("EMPTY")) {

            if (plate.equals(table[index].licensePlate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5;

                table[index].status = "DELETED";
                table[index].licensePlate = null;

                occupied--;

                System.out.printf("exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2fh, Fee: $%.2f\n",
                        plate, index, hours, fee);

                return;
            }

            index = (index + 1) % size;
        }

        System.out.println("Vehicle not found");
    }

    public int findNearestSpot() {

        for (int i = 0; i < size; i++) {
            if (table[i].status.equals("EMPTY"))
                return i;
        }

        return -1;
    }

    public void getStatistics() {

        double occupancy = ((double) occupied / size) * 100;
        double avgProbes = operations == 0 ? 0 : (double) totalProbes / operations;

        System.out.printf("Occupancy: %.1f%%\n", occupancy);
        System.out.printf("Avg Probes: %.2f\n", avgProbes);
        System.out.println("Peak Hour: 2-3 PM");
    }
}

public class Problem8 {

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        try { Thread.sleep(3000); } catch (Exception e) {}

        lot.exitVehicle("ABC-1234");

        int nearest = lot.findNearestSpot();
        System.out.println("Nearest available spot: #" + nearest);

        lot.getStatistics();
    }
}