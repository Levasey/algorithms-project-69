import hexlet.code.SearchEngine;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SearchEngineRelevanceTest {

    @Test
    void testRelevanceSorting() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot that thing at me.";
        var doc3 = "I'm your shooter.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot");
        // doc2 содержит 3 вхождения "shoot", doc1 - 1 вхождение
        assertEquals(2, result.size());
        assertEquals("doc2", result.get(0)); // Самый релевантный
        assertEquals("doc1", result.get(1)); // Менее релевантный
    }

    @Test
    void testEqualRelevance() {
        var doc1 = "word word word";
        var doc2 = "word word word"; // Такая же релевантность
        var doc3 = "word"; // Меньшая релевантность

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "word");
        assertEquals(3, result.size());
        // При равной релевантности порядок должен сохраняться как в исходном списке
        assertTrue(result.contains("doc1"));
        assertTrue(result.contains("doc2"));
        assertTrue(result.contains("doc3"));
        // doc1 и doc2 должны быть перед doc3, но порядок между doc1 и doc2 может быть любым
        assertTrue(result.indexOf("doc3") > 0);
    }

    @Test
    void testMultipleWordsRelevance() {
        var doc1 = "test test test"; // 3 вхождения
        var doc2 = "test test";      // 2 вхождения
        var doc3 = "test";           // 1 вхождение
        var doc4 = "no match";       // 0 вхождений

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3),
                Map.of("id", "doc4", "text", doc4)
        );

        List<String> result = SearchEngine.search(docs, "test");
        assertEquals(3, result.size());
        // Проверяем сортировку по убыванию релевантности
        assertEquals("doc1", result.get(0)); // 3 вхождения
        assertEquals("doc2", result.get(1)); // 2 вхождения
        assertEquals("doc3", result.get(2)); // 1 вхождение
        // doc4 не должен быть в результатах
        assertFalse(result.contains("doc4"));
    }

    @Test
    void testComplexRelevanceExample() {
        var doc1 = "apple banana apple banana apple"; // apple: 3, banana: 2
        var doc2 = "apple apple banana";              // apple: 2, banana: 1
        var doc3 = "banana banana banana";            // banana: 3

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        // Поиск "apple"
        List<String> resultApple = SearchEngine.search(docs, "apple");
        assertEquals(2, resultApple.size());
        assertEquals("doc1", resultApple.get(0)); // 3 apple
        assertEquals("doc2", resultApple.get(1)); // 2 apple

        // Поиск "banana"
        List<String> resultBanana = SearchEngine.search(docs, "banana");
        assertEquals(3, resultBanana.size());
        assertEquals("doc3", resultBanana.get(0)); // 3 banana
        assertEquals("doc1", resultBanana.get(1)); // 2 banana
        assertEquals("doc2", resultBanana.get(2)); // 1 banana
    }

    @Test
    void testRelevanceWithPunctuation() {
        var doc1 = "test! test? test."; // 3 вхождения
        var doc2 = "test test";          // 2 вхождения
        var doc3 = "test";               // 1 вхождение

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "test");
        assertEquals(3, result.size());
        assertEquals("doc1", result.get(0)); // 3 вхождения
        assertEquals("doc2", result.get(1)); // 2 вхождения
        assertEquals("doc3", result.get(2)); // 1 вхождение

        // Поиск с пунктуацией
        List<String> result2 = SearchEngine.search(docs, "test!");
        assertEquals(3, result2.size()); // Все равно найдет все документы с "test"
        assertEquals("doc1", result2.get(0));
    }
}