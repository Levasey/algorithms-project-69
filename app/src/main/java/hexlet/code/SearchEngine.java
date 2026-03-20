package hexlet.code;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    /** Слова: буквы, цифры, подчёркивание и апостроф (сокращения вроде can't, don't). */
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\w']+");

    /**
     * Обратный индекс: слово → (id документа → число вхождений в этом документе).
     */
    private static Map<String, Map<String, Integer>> buildInvertedIndex(List<Map<String, String>> docs) {
        Map<String, Map<String, Integer>> index = new HashMap<>();
        for (Map<String, String> doc : docs) {
            String text = doc.get("text");
            String id = doc.get("id");
            if (text == null || id == null || text.isEmpty()) {
                continue;
            }
            List<String> words = extractWords(text.toLowerCase());
            if (words.isEmpty()) {
                continue;
            }
            Map<String, Integer> freqInDoc = new HashMap<>();
            for (String w : words) {
                freqInDoc.merge(w, 1, Integer::sum);
            }
            for (Map.Entry<String, Integer> e : freqInDoc.entrySet()) {
                index.computeIfAbsent(e.getKey(), k -> new HashMap<>())
                        .merge(id, e.getValue(), Integer::sum);
            }
        }
        return index;
    }

    /**
     * Число слов в каждом документе (для нормализации TF).
     */
    private static Map<String, Integer> buildDocWordCounts(List<Map<String, String>> docs) {
        Map<String, Integer> counts = new HashMap<>();
        for (Map<String, String> doc : docs) {
            String text = doc.get("text");
            String id = doc.get("id");
            if (text == null || id == null || text.isEmpty()) {
                continue;
            }
            List<String> words = extractWords(text.toLowerCase());
            if (!words.isEmpty()) {
                counts.put(id, words.size());
            }
        }
        return counts;
    }

    private static int corpusDocCount(Map<String, Integer> docWordCounts) {
        return docWordCounts.size();
    }

    /**
     * Вклад TF-IDF для одного терма в документе: TF (доля вхождений в документе) × IDF.
     * IDF сглаживание: ln((N + 1) / (df + 1)).
     */
    private static double tfIdfForTerm(int termCountInDoc, int docWordCount, int nDocs, int docFreq) {
        if (termCountInDoc <= 0 || docWordCount <= 0 || nDocs <= 0) {
            return 0.0;
        }
        double tf = (double) termCountInDoc / docWordCount;
        double idf = Math.log((nDocs + 1.0) / (docFreq + 1.0));
        return tf * idf;
    }

    private static final class DocScore {
        private final int matchedTokens;
        private final int totalOccurrences;
        private final double tfidfSum;

        private DocScore(int matchedTokens, int totalOccurrences, double tfidfSum) {
            this.matchedTokens = matchedTokens;
            this.totalOccurrences = totalOccurrences;
            this.tfidfSum = tfidfSum;
        }
    }

    private static DocScore scoreDocument(
            Map<String, Map<String, Integer>> invertedIndex,
            Map<String, Integer> docWordCounts,
            int nDocs,
            String docId,
            List<String> searchWords) {
        int docWordCount = docWordCounts.getOrDefault(docId, 0);
        int matchedTokens = 0;
        int totalOccurrences = 0;
        double tfidfSum = 0.0;

        for (String searchWord : searchWords) {
            Map<String, Integer> postings = invertedIndex.get(searchWord);
            if (postings == null) {
                continue;
            }
            Integer count = postings.get(docId);
            if (count == null || count <= 0) {
                continue;
            }
            matchedTokens++;
            totalOccurrences += count;
            int df = postings.size();
            tfidfSum += tfIdfForTerm(count, docWordCount, nDocs, df);
        }

        if (matchedTokens == 0) {
            return null;
        }
        return new DocScore(matchedTokens, totalOccurrences, tfidfSum);
    }

    public static List<String> search(List<Map<String, String>> docs, String searchQuery) {
        List<String> searchWords = extractWords(searchQuery.toLowerCase());
        if (searchWords.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Map<String, Integer>> invertedIndex = buildInvertedIndex(docs);
        Map<String, Integer> docWordCounts = buildDocWordCounts(docs);
        int nDocs = corpusDocCount(docWordCounts);

        Map<String, Integer> docFirstIndex = new HashMap<>();
        for (int i = 0; i < docs.size(); i++) {
            String id = docs.get(i).get("id");
            if (id != null) {
                docFirstIndex.putIfAbsent(id, i);
            }
        }

        Set<String> candidateIds = new LinkedHashSet<>();
        for (String sw : searchWords) {
            Map<String, Integer> postings = invertedIndex.get(sw);
            if (postings != null) {
                candidateIds.addAll(postings.keySet());
            }
        }

        Map<String, DocScore> scoreById = new LinkedHashMap<>();
        for (String id : candidateIds) {
            DocScore score = scoreDocument(invertedIndex, docWordCounts, nDocs, id, searchWords);
            if (score != null) {
                scoreById.put(id, score);
            }
        }

        Comparator<Map.Entry<String, DocScore>> byRelevance = (a, b) -> {
            DocScore sa = a.getValue();
            DocScore sb = b.getValue();
            int cmp = Integer.compare(sb.matchedTokens, sa.matchedTokens);
            if (cmp != 0) {
                return cmp;
            }
            cmp = Integer.compare(sb.totalOccurrences, sa.totalOccurrences);
            if (cmp != 0) {
                return cmp;
            }
            cmp = Integer.compare(
                    docFirstIndex.getOrDefault(a.getKey(), Integer.MAX_VALUE),
                    docFirstIndex.getOrDefault(b.getKey(), Integer.MAX_VALUE));
            if (cmp != 0) {
                return cmp;
            }
            return Double.compare(sb.tfidfSum, sa.tfidfSum);
        };

        return scoreById.entrySet().stream()
                .sorted(byRelevance)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static List<String> extractWords(String input) {
        List<String> words = new ArrayList<>();
        var matcher = WORD_PATTERN.matcher(input);

        while (matcher.find()) {
            words.add(matcher.group());
        }

        return words;
    }
}
