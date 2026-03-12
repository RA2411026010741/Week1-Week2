import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    int maxTokens;
    double refillRate;
    double tokens;
    long lastRefillTime;

    TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {

        refill();

        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }

        return false;
    }

    void refill() {

        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        double newTokens = seconds * refillRate;

        tokens = Math.min(maxTokens, tokens + newTokens);

        lastRefillTime = now;
    }

    int remainingTokens() {
        return (int) tokens;
    }

    long retryAfter() {

        if (tokens >= 1)
            return 0;

        double needed = 1 - tokens;
        return (long) (needed / refillRate);
    }
}

class RateLimiter {

    ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();

    int limit = 1000;
    double refillRate = 1000.0 / 3600;

    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(limit, refillRate));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" + bucket.remainingTokens() + " requests remaining)";
        }

        return "Denied (0 requests remaining, retry after "
                + bucket.retryAfter() + "s)";
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = bucket.maxTokens - bucket.remainingTokens();
        int limit = bucket.maxTokens;

        long resetTime = System.currentTimeMillis() / 1000 + bucket.retryAfter();

        System.out.println("{used: " + used +
                ", limit: " + limit +
                ", reset: " + resetTime + "}");
    }
}

public class Problem6 {

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {

            System.out.println(
                    limiter.checkRateLimit(client)
            );
        }

        limiter.getRateLimitStatus(client);
    }
}