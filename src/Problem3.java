import java.util.*;

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, int ttl) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttl * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private int capacity;
    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            long end = System.nanoTime();
            double time = (end - start) / 1000000.0;
            return "Cache HIT → " + entry.ipAddress + " (" + time + " ms)";
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 300);
        cache.put(domain, newEntry);

        long end = System.nanoTime();
        double time = (end - start) / 1000000.0;

        return "Cache MISS → Query upstream → " + ip + " (" + time + " ms)";
    }

    private String queryUpstreamDNS(String domain) {

        Random r = new Random();
        return "172.217.14." + (200 + r.nextInt(50));
    }

    public void cleanExpiredEntries() {

        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }
}

public class Problem3 {

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache(5);

        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("openai.com"));
        System.out.println(cache.resolve("github.com"));

        Thread.sleep(2000);

        System.out.println(cache.resolve("google.com"));

        cache.cleanExpiredEntries();

        cache.getCacheStats();
    }
}