import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

class FraudDetector {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                System.out.println("TwoSum Match → (" + other.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }

    public void findTwoSumWithTimeWindow(int target, long windowMillis) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                if (Math.abs(t.time - other.time) <= windowMillis) {

                    System.out.println("Time Window Match → (" + other.id + ", " + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }
    }

    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.print("Duplicate → Amount/Merchant: " + key + " Accounts: ");

                for (Transaction t : list)
                    System.out.print(t.account + " ");

                System.out.println();
            }
        }
    }

    public void findKSum(int k, int target) {

        List<Integer> current = new ArrayList<>();

        kSumHelper(0, k, target, current);
    }

    private void kSumHelper(int index, int k, int target, List<Integer> current) {

        if (k == 0 && target == 0) {
            System.out.println("KSum Match → " + current);
            return;
        }

        if (k == 0 || index >= transactions.size())
            return;

        Transaction t = transactions.get(index);

        current.add(t.id);
        kSumHelper(index + 1, k - 1, target - t.amount, current);

        current.remove(current.size() - 1);
        kSumHelper(index + 1, k, target, current);
    }
}

public class Problem9 {

    public static void main(String[] args) {

        FraudDetector detector = new FraudDetector();

        long baseTime = System.currentTimeMillis();

        detector.addTransaction(new Transaction(1, 500, "StoreA", "acc1", baseTime));
        detector.addTransaction(new Transaction(2, 300, "StoreB", "acc2", baseTime + 900000));
        detector.addTransaction(new Transaction(3, 200, "StoreC", "acc3", baseTime + 1800000));
        detector.addTransaction(new Transaction(4, 500, "StoreA", "acc4", baseTime + 2000000));

        System.out.println("Two-Sum:");
        detector.findTwoSum(500);

        System.out.println("\nTwo-Sum within 1 hour:");
        detector.findTwoSumWithTimeWindow(500, 3600000);

        System.out.println("\nDuplicate Detection:");
        detector.detectDuplicates();

        System.out.println("\nK-Sum (k=3, target=1000):");
        detector.findKSum(3, 1000);
    }
}