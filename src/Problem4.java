import java.util.*;

class PlagiarismDetector {

    private HashMap<String, Set<String>> index = new HashMap<>();
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();
    private int n = 5;

    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < n; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }

    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String gram : ngrams) {

            Set<String> docs = index.get(gram);

            if (docs != null) {

                for (String d : docs) {

                    if (!d.equals(docId)) {

                        matchCounts.put(d, matchCounts.getOrDefault(d, 0) + 1);
                    }
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (String doc : matchCounts.keySet()) {

            int matches = matchCounts.get(doc);
            double similarity = ((double) matches / ngrams.size()) * 100;

            System.out.println("Found " + matches + " matching n-grams with \"" + doc + "\"");
            System.out.printf("Similarity: %.2f%%", similarity);

            if (similarity > 60)
                System.out.println(" (PLAGIARISM DETECTED)");
            else if (similarity > 10)
                System.out.println(" (suspicious)");
            else
                System.out.println();
        }
    }
}

public class Problem4 {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "machine learning is a field of artificial intelligence that focuses on data analysis and pattern recognition";
        String essay2 = "machine learning is a branch of artificial intelligence that focuses on pattern recognition and data analysis";
        String essay3 = "the history of ancient civilizations includes egypt greece and rome which shaped the modern world";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay2);

        detector.analyzeDocument("essay_123.txt");
    }
}