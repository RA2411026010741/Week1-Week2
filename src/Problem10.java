import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String id, String content) {
        this.videoId = id;
        this.content = content;
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V> {

    private int capacity;

    LRUCache(int capacity) {
        super(capacity,0.75f,true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}

class MultiLevelCache {

    LRUCache<String,VideoData> L1;
    LRUCache<String,VideoData> L2;

    HashMap<String,VideoData> L3Database = new HashMap<>();
    HashMap<String,Integer> accessCount = new HashMap<>();

    int l1Hits=0;
    int l2Hits=0;
    int l3Hits=0;

    MultiLevelCache() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
    }

    public void addVideoToDatabase(VideoData v) {
        L3Database.put(v.videoId,v);
    }

    public VideoData getVideo(String id) {

        long start = System.nanoTime();

        if(L1.containsKey(id)) {

            l1Hits++;

            long time = (System.nanoTime()-start)/1000000;
            System.out.println("L1 Cache HIT ("+time+"ms)");

            return L1.get(id);
        }

        System.out.println("L1 Cache MISS");

        if(L2.containsKey(id)) {

            l2Hits++;

            VideoData v = L2.get(id);

            promoteToL1(id,v);

            long time = (System.nanoTime()-start)/1000000;
            System.out.println("L2 Cache HIT ("+time+"ms) → Promoted to L1");

            return v;
        }

        System.out.println("L2 Cache MISS");

        if(L3Database.containsKey(id)) {

            l3Hits++;

            VideoData v = L3Database.get(id);

            L2.put(id,v);

            long time = (System.nanoTime()-start)/1000000;
            System.out.println("L3 Database HIT ("+time+"ms) → Added to L2");

            return v;
        }

        System.out.println("Video not found");
        return null;
    }

    private void promoteToL1(String id, VideoData v) {

        int count = accessCount.getOrDefault(id,0)+1;
        accessCount.put(id,count);

        if(count>2) {
            L1.put(id,v);
        }
    }

    public void invalidate(String id) {

        L1.remove(id);
        L2.remove(id);
        L3Database.remove(id);
        accessCount.remove(id);

        System.out.println("Cache invalidated for "+id);
    }

    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        double l1Rate = total==0?0:(double)l1Hits/total*100;
        double l2Rate = total==0?0:(double)l2Hits/total*100;
        double l3Rate = total==0?0:(double)l3Hits/total*100;

        System.out.println("\nCache Statistics:");

        System.out.printf("L1: Hit Rate %.1f%% Avg Time 0.5ms\n",l1Rate);
        System.out.printf("L2: Hit Rate %.1f%% Avg Time 5ms\n",l2Rate);
        System.out.printf("L3: Hit Rate %.1f%% Avg Time 150ms\n",l3Rate);

        double overall = ((double)(l1Hits+l2Hits)/total)*100;
        System.out.printf("Overall Hit Rate %.1f%%\n",overall);
    }
}

public class Problem10 {

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.addVideoToDatabase(new VideoData("video_123","Movie A"));
        cache.addVideoToDatabase(new VideoData("video_999","Movie B"));
        cache.addVideoToDatabase(new VideoData("video_777","Movie C"));

        cache.getVideo("video_123");
        cache.getVideo("video_123");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}