import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class DressingRooms {
    private static DressingRooms instance;
    private final Semaphore rooms;
    private final int totalRooms;

    private DressingRooms() {
        this(3);
    }

    private DressingRooms(int numberOfRooms) {
        this.totalRooms = numberOfRooms;
        this.rooms = new Semaphore(numberOfRooms, true);
    }

    public static synchronized DressingRooms createInstance(int numberOfRooms) {
        instance = new DressingRooms(numberOfRooms);
        return instance;
    }

    public static DressingRooms getInstance() {
        if (instance == null) {
            instance = new DressingRooms();
        }
        return instance;
    }

    public void requestRoom() throws InterruptedException {
        rooms.acquire();
    }

    public void releaseRoom() {
        rooms.release();
    }

    public int getTotalRooms() {
        return totalRooms;
    }
}

class Customer extends Thread {
    private static final int SIMULATED_MINUTE_MS = 50;
    private final int customerId;
    private final int clothingItems;
    private final Random random;
    private long waitTimeMs;
    private long usageTimeMs;
    private final AtomicInteger finishedCount;

    public Customer(int customerId, int numberOfItems, AtomicInteger finishedCount, long seed) {
        this.customerId = customerId;
        this.random = new Random(seed + customerId);
        this.clothingItems = numberOfItems == 0 ? this.random.nextInt(6) + 1 : numberOfItems;
        this.finishedCount = finishedCount;
    }

    @Override
    public void run() {
        DressingRooms rooms = DressingRooms.getInstance();
        long requestStart = System.currentTimeMillis();
        try {
            rooms.requestRoom();
            waitTimeMs = System.currentTimeMillis() - requestStart;

            long usageStart = System.currentTimeMillis();
            for (int i = 0; i < clothingItems; i++) {
                int tryOnMinutes = random.nextInt(3) + 1;
                Thread.sleep(tryOnMinutes * SIMULATED_MINUTE_MS);
            }
            usageTimeMs = System.currentTimeMillis() - usageStart;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            rooms.releaseRoom();
            finishedCount.incrementAndGet();
        }
    }

    public int getClothingItems() {
        return clothingItems;
    }

    public long getWaitTimeMs() {
        return waitTimeMs;
    }

    public long getUsageTimeMs() {
        return usageTimeMs;
    }
}

class Scenario {
    private final int roomsAvailable;
    private final int customersCount;
    private final int numberOfItems;
    private final List<Customer> customers = new ArrayList<>();
    private long startTime;
    private long endTime;

    public Scenario(int roomsAvailable, int customersCount, int numberOfItems) {
        this.roomsAvailable = roomsAvailable;
        this.customersCount = customersCount;
        this.numberOfItems = numberOfItems;
        DressingRooms.createInstance(roomsAvailable);
        AtomicInteger finishedCount = new AtomicInteger(0);
        for (int i = 1; i <= customersCount; i++) {
            customers.add(new Customer(i, numberOfItems, finishedCount, 1000L + roomsAvailable));
        }
    }

    public void execute(String scenarioName) throws InterruptedException {
        startTime = System.currentTimeMillis();
        for (Customer customer : customers) {
            customer.start();
        }
        for (Customer customer : customers) {
            customer.join();
        }
        endTime = System.currentTimeMillis();
        printResults(scenarioName);
    }

    private void printResults(String scenarioName) {
        int totalItems = 0;
        long totalUsage = 0;
        long totalWait = 0;
        for (Customer customer : customers) {
            totalItems += customer.getClothingItems();
            totalUsage += customer.getUsageTimeMs();
            totalWait += customer.getWaitTimeMs();
        }
        double elapsedSeconds = (endTime - startTime) / 1000.0;
        double avgItems = (double) totalItems / customersCount;
        double avgUsageSeconds = (double) totalUsage / customersCount / 1000.0;
        double avgWaitSeconds = (double) totalWait / customersCount / 1000.0;
        System.out.printf("%s | Rooms: %d | Customers: %d | Avg Items: %.2f | Avg Usage: %.2fs | Avg Wait: %.2fs | Elapsed: %.2fs%n",
                scenarioName, roomsAvailable, customersCount, avgItems, avgUsageSeconds, avgWaitSeconds, elapsedSeconds);
    }
}

public class DressingRoomSimulation {
    public static void scenario01() throws InterruptedException {
        Scenario scenario = new Scenario(2, 20, 0);
        scenario.execute("Scenario 01");
    }

    public static void scenario02() throws InterruptedException {
        Scenario scenario = new Scenario(3, 20, 0);
        scenario.execute("Scenario 02");
    }

    public static void scenario03() throws InterruptedException {
        Scenario scenario = new Scenario(5, 20, 0);
        scenario.execute("Scenario 03");
    }

    public static void main(String[] args) throws InterruptedException {
        scenario01();
        scenario02();
        scenario03();
    }
}
