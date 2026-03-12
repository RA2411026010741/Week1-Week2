import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class FlashSaleInventoryManager {

    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new LinkedList<>());
    }

    public String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null)
            return "Product not found";

        return stock.get() + " units available";
    }

    public synchronized String purchaseItem(String productId, int userId) {

        AtomicInteger stock = inventory.get(productId);

        if (stock == null)
            return "Product not found";

        if (stock.get() > 0) {

            int remaining = stock.decrementAndGet();

            return "Success! User " + userId +
                    " purchased item. " + remaining + " units remaining";

        } else {

            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);

            return "Out of stock. User " + userId +
                    " added to waiting list. Position #" + queue.size();
        }
    }

    public void showWaitingList(String productId) {

        Queue<Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("No users in waiting list");
            return;
        }

        System.out.println("Waiting List:");
        for (Integer user : queue)
            System.out.println("User ID: " + user);
    }
}

public class Problem2 {

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB"));

        long start = System.nanoTime();

        for (int i = 1; i <= 105; i++) {

            System.out.println(
                    manager.purchaseItem("IPHONE15_256GB", 10000 + i)
            );
        }

        long end = System.nanoTime();

        System.out.println("\nExecution Time: " + (end - start) + " ns");

        manager.showWaitingList("IPHONE15_256GB");
    }
}