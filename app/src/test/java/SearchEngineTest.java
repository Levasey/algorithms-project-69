import hexlet.code.SearchEngine;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {

    @Test
    void testBasicSearch() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot that thing at me.";
        var doc3 = "I'm your shooter.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot");
        assertEquals(2, result.size());
        assertTrue(result.contains("doc1"));
        assertTrue(result.contains("doc2"));
        assertFalse(result.contains("doc3"));
    }

    @Test
    void testSearchWithPunctuation() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot that thing at me.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2)
        );

        // Поиск с восклицательным знаком
        List<String> result1 = SearchEngine.search(docs, "pint!");
        assertEquals(1, result1.size());
        assertEquals("doc1", result1.get(0));

        // Поиск с апострофом
        List<String> result2 = SearchEngine.search(docs, "can't");
        assertEquals(1, result2.size());
        assertEquals("doc1", result2.get(0));

        // Поиск с точкой
        List<String> result3 = SearchEngine.search(docs, "me.");
        assertEquals(1, result3.size());
        assertEquals("doc2", result3.get(0));
    }

    @Test
    void testCaseInsensitiveSearch() {
        var doc1 = "UPPER CASE TEXT";
        var doc2 = "lower case text";
        var doc3 = "Mixed Case Text";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        // Поиск в нижнем регистре
        List<String> result1 = SearchEngine.search(docs, "text");
        assertEquals(3, result1.size());

        // Поиск в верхнем регистре
        List<String> result2 = SearchEngine.search(docs, "TEXT");
        assertEquals(3, result2.size());

        // Поиск в смешанном регистре
        List<String> result3 = SearchEngine.search(docs, "TeXt");
        assertEquals(3, result3.size());
    }

    @Test
    void testSearchNonExistentWord() {
        var doc1 = "Some text here";
        var doc2 = "Another text";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2)
        );

        List<String> result = SearchEngine.search(docs, "nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void testEmptyDocs() {
        List<Map<String, String>> docs = List.of();
        List<String> result = SearchEngine.search(docs, "anyword");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchWithEmptyWord() {
        var doc1 = "Some text";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1)
        );

        List<String> result = SearchEngine.search(docs, "");
        assertTrue(result.isEmpty());

        List<String> result2 = SearchEngine.search(docs, "!!!");
        assertTrue(result2.isEmpty());

        List<String> result3 = SearchEngine.search(docs, "...");
        assertTrue(result3.isEmpty());
    }

    @Test
    void testSearchWithSpecialCharactersOnly() {
        var doc1 = "Some text";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1)
        );

        List<String> result = SearchEngine.search(docs, "!@#$%^&*()");
        assertTrue(result.isEmpty());
    }

    @Test
    void testDocumentWithoutIdOrText() {
        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", "valid text"),
                Map.of("id", "doc2"), // без текста
                Map.of("text", "text without id"), // без ID
                Map.of() // пустой документ
        );

        List<String> result = SearchEngine.search(docs, "valid");
        assertEquals(1, result.size());
        assertEquals("doc1", result.get(0));
    }

    @Test
    void testMultipleOccurrencesInSameDocument() {
        var doc1 = "test test test test test";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1)
        );

        List<String> result = SearchEngine.search(docs, "test");
        assertEquals(1, result.size());
        assertEquals("doc1", result.get(0));
    }

    @Test
    void testNumbersInText() {
        var doc1 = "My phone number is 123-4567";
        var doc2 = "The year is 2024";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2)
        );

        List<String> result1 = SearchEngine.search(docs, "123");
        assertEquals(1, result1.size());
        assertEquals("doc1", result1.get(0));

        List<String> result2 = SearchEngine.search(docs, "2024");
        assertEquals(1, result2.size());
        assertEquals("doc2", result2.get(0));

        // Поиск с дефисом
        List<String> result3 = SearchEngine.search(docs, "123-4567");
        assertEquals(1, result3.size());
        assertEquals("doc1", result3.get(0));
    }

    @Test
    void testSearchForWordWithUnderscores() {
        var doc1 = "variable_name test_word";
        var doc2 = "another text";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2)
        );

        // \w+ включает подчеркивания, так что это должно работать
        List<String> result1 = SearchEngine.search(docs, "variable_name");
        assertEquals(1, result1.size());
        assertEquals("doc1", result1.get(0));

        List<String> result2 = SearchEngine.search(docs, "test_word");
        assertEquals(1, result2.size());
        assertEquals("doc1", result2.get(0));
    }
}
