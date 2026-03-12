import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
    boolean isEnd = false;
}

class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    public void insertQuery(String query, int freq) {

        frequencyMap.put(query, freq);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query, freq);
        }

        node.isEnd = true;
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> heap =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : node.queries.entrySet()) {

            heap.offer(entry);

            if (heap.size() > 10)
                heap.poll();
        }

        List<String> result = new ArrayList<>();

        while (!heap.isEmpty())
            result.add(heap.poll().getKey());

        Collections.reverse(result);

        return result;
    }

    public void updateFrequency(String query) {

        int freq = frequencyMap.getOrDefault(query, 0) + 1;
        insertQuery(query, freq);

        System.out.println("Frequency updated: " + query + " → " + freq);
    }
}

public class Problem7 {

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insertQuery("java tutorial", 1234567);
        system.insertQuery("javascript", 987654);
        system.insertQuery("java download", 456789);
        system.insertQuery("java 21 features", 100);

        List<String> results = system.search("jav");

        System.out.println("Suggestions:");

        int rank = 1;

        for (String s : results) {
            System.out.println(rank + ". " + s);
            rank++;
        }

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");
    }
}