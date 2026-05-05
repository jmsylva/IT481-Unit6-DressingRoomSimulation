import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private final int numberOfRooms;
    private final int numberOfCustomers;
    private final int clothingItems;
    private final List<Customer> customers;

    public Scenario(int numberOfRooms, int numberOfCustomers, int clothingItems) {
        this.numberOfRooms = numberOfRooms;
        this.numberOfCustomers = numberOfCustomers;
        this.clothingItems = clothingItems;
        this.customers = new ArrayList<>();
    }

    public void runScenario(String scenarioName) {
        DressingRooms dressingRooms = new DressingRooms(numberOfRooms);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= numberOfCustomers; i++) {
            Customer customer = new Customer(i, dressingRooms, clothingItems);
            customers.add(customer);
            customer.start();
        }

        for (Customer customer : customers) {
            try {
                customer.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        int totalItems = 0;
        long totalWaitTime = 0;
        long totalUsageTime = 0;

        for (Customer customer : customers) {
            totalItems += customer.getNumberOfItems();
            totalWaitTime += customer.getWaitTime();
            totalUsageTime += customer.getUsageTime();
        }

        double averageItems = (double) totalItems / numberOfCustomers;
        double averageWaitTime = (double) totalWaitTime / numberOfCustomers;
        double averageUsageTime = (double) totalUsageTime / numberOfCustomers;

        System.out.println("\n" + scenarioName);
        System.out.println("Rooms Available: " + numberOfRooms);
        System.out.println("Customers: " + numberOfCustomers);
        System.out.println("Elapsed Time: " + elapsedTime + " ms");
        System.out.println("Average Items: " + String.format("%.2f", averageItems));
        System.out.println("Average Room Usage Time: " + String.format("%.2f", averageUsageTime) + " ms");
        System.out.println("Average Wait Time: " + String.format("%.2f", averageWaitTime) + " ms");
    }
}