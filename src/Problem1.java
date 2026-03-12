import java.util.*;

public class Problem1 {

    private HashMap<String, Integer> usernameMap;
    private HashMap<String, Integer> attemptFrequency;

    public Problem1() {
        usernameMap = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.put(
                username,
                attemptFrequency.getOrDefault(username, 0) + 1
        );
        return !usernameMap.containsKey(username);
    }

    public boolean register(String username, int userId) {
        if (checkAvailability(username)) {
            usernameMap.put(username, userId);
            return true;
        }
        return false;
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String candidate = username + i;

            if (!usernameMap.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        if (username.contains("_")) {

            String candidate = username.replace("_", ".");

            if (!usernameMap.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted;
    }

    public static void main(String[] args) {

        Problem1 checker = new Problem1();

        checker.register("john_doe", 101);
        checker.register("alex99", 102);

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));

        System.out.println(checker.suggestAlternatives("john_doe"));

        System.out.println("Most attempted: " + checker.getMostAttempted());
    }
}