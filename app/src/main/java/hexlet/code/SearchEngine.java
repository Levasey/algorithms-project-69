package hexlet.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchEngine {
    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");

    public static List<String> search(List<Map<String, String>> docs, String word) {
        Map<String, Integer> relevanceMap = new HashMap<>();

        // Обрабатываем искомое слово
        String processedWord = extractWord(word);
        if (processedWord.isEmpty()) {
            return new ArrayList<>();
        }

        // Проходим по всем документам и считаем релевантность
        for (Map<String, String> doc : docs) {
            String text = doc.get("text");
            String id = doc.get("id");

            if (text == null || id == null) {
                continue;
            }

            // Подсчитываем количество вхождений слова в документе
            int relevance = countWordOccurrences(text.toLowerCase(), processedWord);

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

    // Метод для извлечения слова из строки (без знаков препинания)
    private static String extractWord(String input) {
        var matcher = WORD_PATTERN.matcher(input.toLowerCase());
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    // Метод для подсчета вхождений слова в текст
    private static int countWordOccurrences(String text, String word) {
        int count = 0;
        var matcher = WORD_PATTERN.matcher(text);

        while (matcher.find()) {
            if (matcher.group().equals(word)) {
                count++;
            }
        }

        return count;
    }
}