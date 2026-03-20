package hexlet.code;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static List<String> search(List<Map<String, String>> docs, String searchQuery) {
        List<String> searchWords = extractWords(searchQuery.toLowerCase());
        if (searchWords.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Map<String, Integer>> invertedIndex = buildInvertedIndex(docs);
        Map<String, Integer> relevanceMap = new HashMap<>();

        Set<String> candidateIds = new LinkedHashSet<>();
        for (String sw : searchWords) {
            Map<String, Integer> postings = invertedIndex.get(sw);
            if (postings != null) {
                candidateIds.addAll(postings.keySet());
            }
        }

        for (String id : candidateIds) {
            int relevance = relevanceFromIndex(invertedIndex, id, searchWords);
            if (relevance > 0) {
                relevanceMap.put(id, relevance);
            }
        }

        return relevanceMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
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

    private static int relevanceFromIndex(
            Map<String, Map<String, Integer>> invertedIndex,
            String docId,
            List<String> searchWords) {
        int uniqueWordsFound = 0;
        int totalOccurrences = 0;

        for (String searchWord : searchWords) {
            Map<String, Integer> postings = invertedIndex.get(searchWord);
            if (postings == null) {
                continue;
            }
            Integer count = postings.get(docId);
            if (count != null && count > 0) {
                uniqueWordsFound++;
                totalOccurrences += count;
            }
        }

        if (uniqueWordsFound == 0) {
            return 0;
        }

        return uniqueWordsFound * 1000 + totalOccurrences;
    }
}
