import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsSystem {

    HashMap<String, Integer> pageViews = new HashMap<>();
    HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    HashMap<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(PageEvent event) {

        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    public void getDashboard() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");

        int rank = 1;

        while (!pq.isEmpty() && rank <= 10) {

            Map.Entry<String, Integer> entry = pq.poll();

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url + " - " +
                    views + " views (" + unique + " unique)");

            rank++;
        }

        int total = 0;

        for (int count : trafficSources.values())
            total += count;

        System.out.println("\nTraffic Sources:");

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);
            double percent = ((double) count / total) * 100;

            System.out.printf("%s : %.1f%%\n", source, percent);
        }
    }
}

public class Problem5 {

    public static void main(String[] args) {

        AnalyticsSystem system = new AnalyticsSystem();

        system.processEvent(new PageEvent("/article/breaking-news", "user_1", "Google"));
        system.processEvent(new PageEvent("/article/breaking-news", "user_2", "Facebook"));
        system.processEvent(new PageEvent("/sports/championship", "user_3", "Google"));
        system.processEvent(new PageEvent("/sports/championship", "user_4", "Direct"));
        system.processEvent(new PageEvent("/article/breaking-news", "user_5", "Google"));
        system.processEvent(new PageEvent("/tech/ai-news", "user_6", "Other"));
        system.processEvent(new PageEvent("/tech/ai-news", "user_7", "Direct"));
        system.processEvent(new PageEvent("/tech/ai-news", "user_8", "Google"));
        system.processEvent(new PageEvent("/article/breaking-news", "user_9", "Facebook"));
        system.processEvent(new PageEvent("/sports/championship", "user_10", "Direct"));

        system.getDashboard();
    }
}