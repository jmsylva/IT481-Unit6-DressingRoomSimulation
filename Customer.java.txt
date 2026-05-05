import java.util.Random;

public class Customer extends Thread {
    private final int customerId;
    private final DressingRooms dressingRooms;
    private final int numberOfItems;
    private long waitTime;
    private long usageTime;

    public Customer(int customerId, DressingRooms dressingRooms, int clothingItems) {
        this.customerId = customerId;
        this.dressingRooms = dressingRooms;

        if (clothingItems == 0) {
            this.numberOfItems = new Random().nextInt(6) + 1;
        } else {
            this.numberOfItems = clothingItems;
        }
    }

    @Override
    public void run() {
        long waitStart = System.currentTimeMillis();

        dressingRooms.requestRoom(customerId);

        waitTime = System.currentTimeMillis() - waitStart;
        long useStart = System.currentTimeMillis();

        Random random = new Random();

        for (int i = 1; i <= numberOfItems; i++) {
            int tryOnTime = random.nextInt(3) + 1;

            try {
                Thread.sleep(tryOnTime * 100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        usageTime = System.currentTimeMillis() - useStart;
        dressingRooms.releaseRoom(customerId);
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public long getUsageTime() {
        return usageTime;
    }
}