package hexlet.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");

    public static List<String> search(List<Map<String, String>> docs, String searchQuery) {
        Map<String, Integer> relevanceMap = new HashMap<>();

        // Разбиваем поисковый запрос на слова
        List<String> searchWords = extractWords(searchQuery.toLowerCase());
        if (searchWords.isEmpty()) {
            return new ArrayList<>();
        }

        // Проходим по всем документам и считаем релевантность
        for (Map<String, String> doc : docs) {
            String text = doc.get("text");
            String id = doc.get("id");

            if (text == null || id == null || text.isEmpty()) {
                continue;
            }

            // Извлекаем слова из текста документа
            List<String> documentWords = extractWords(text.toLowerCase());
            if (documentWords.isEmpty()) {
                continue;
            }

            // Подсчитываем количество вхождений слова в документе
            int relevance = calculateRelevance(documentWords, searchWords);

            // Сохраняем релевантность для документа
            if (relevance > 0) {
                relevanceMap.put(id, relevance);
            }
        }

        // Сортируем документы по релевантности (по убыванию)
        return relevanceMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Метод для извлечения слов из строки
    private static List<String> extractWords(String input) {
        List<String> words = new ArrayList<>();
        var matcher = WORD_PATTERN.matcher(input);

        while (matcher.find()) {
            words.add(matcher.group());
        }

        return words;
    }

    // Метод для расчета релевантности документа
    private static int calculateRelevance(List<String> documentWords, List<String> searchWords) {
        int uniqueWordsFound = 0;
        int totalOccurrences = 0;

        // Считаем для каждого искомого слова
        for (String searchWord : searchWords) {
            boolean wordFound = false;
            int wordCount = 0;

            for (String docWord : documentWords) {
                if (docWord.equals(searchWord)) {
                    wordFound = true;
                    wordCount++;
                }
            }

            if (wordFound) {
                uniqueWordsFound++;
                totalOccurrences += wordCount;
            }
        }

        // Если не нашли хотя бы одно слово, релевантность = 0
        if (uniqueWordsFound == 0) {
            return 0;
        }

        // Сначала считаем количество найденных уникальных слов,
        // затем общее количество вхождений
        return uniqueWordsFound * 1000 + totalOccurrences;
    }
}