package hexlet.code;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SearchEngine {
    private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");

    public static List<String> search(List<Map<String, String>> docs, String word) {
        List<String> result = new ArrayList<>();

        // Обрабатываем искомое слово
        String processedWord = extractWord(word);
        if (processedWord.isEmpty()) {
            return result;
        }

        // Проходим по всем документам
        for (Map<String, String> doc : docs) {
            String text = doc.get("text");
            String id = doc.get("id");

            if (text == null || id == null) {
                continue;
            }

            // Создаем множество терминов из текста документа
            Set<String> terms = extractWords(text);

            // Проверяем наличие слова в документе
            if (terms.contains(processedWord)) {
                result.add(id);
            }
        }

        return result;
    }

    // Метод для извлечения слова из строки (без знаков препинания)
    private static String extractWord(String input) {
        var matcher = WORD_PATTERN.matcher(input.toLowerCase());
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    // Метод для извлечения всех слов из текста
    private static Set<String> extractWords(String text) {
        Set<String> words = new HashSet<>();
        var matcher = WORD_PATTERN.matcher(text.toLowerCase());
        while (matcher.find()) {
            words.add(matcher.group());
        }
        return words;
    }
}