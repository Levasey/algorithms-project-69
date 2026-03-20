import hexlet.code.SearchEngine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchEngineFuzzySearchTest {

    @Test
    void fuzzySearchExampleFromAssignment() {
        var doc1 = "I can't shoot straight unless I've had a pint!";
        var doc2 = "Don't shoot shoot shoot that thing at me.";
        var doc3 = "I'm your shooter.";

        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", doc1),
                Map.of("id", "doc2", "text", doc2),
                Map.of("id", "doc3", "text", doc3)
        );

        List<String> result = SearchEngine.search(docs, "shoot at me");

        assertEquals(2, result.size());
        assertEquals("doc2", result.get(0));
        assertEquals("doc1", result.get(1));
        assertFalse(result.contains("doc3"));
    }

    @Test
    void fuzzySearchSortsByMatchedWordCountFirst() {
        // doc1: только одно слово из запроса
        // doc2: два слова из запроса — выше, даже если вхождений меньше
        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", "foo foo foo foo"),
                Map.of("id", "doc2", "text", "foo bar")
        );

        List<String> result = SearchEngine.search(docs, "foo bar baz");

        assertEquals(2, result.size());
        assertEquals("doc2", result.get(0));
        assertEquals("doc1", result.get(1));
    }

    @Test
    void fuzzySearchThenSortsByTotalOccurrences() {
        // Одинаковое число разных слов запроса, разная сумма вхождений
        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", "a b"),
                Map.of("id", "doc2", "text", "a a b b")
        );

        List<String> result = SearchEngine.search(docs, "a b");

        assertEquals(2, result.size());
        assertEquals("doc2", result.get(0));
        assertEquals("doc1", result.get(1));
    }

    @Test
    void fuzzySearchExcludesDocumentsWithNoQueryWords() {
        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", "alpha beta"),
                Map.of("id", "doc2", "text", "gamma delta"),
                Map.of("id", "doc3", "text", "alpha only")
        );

        List<String> result = SearchEngine.search(docs, "beta gamma");

        assertEquals(2, result.size());
        assertTrue(result.contains("doc1"));
        assertTrue(result.contains("doc2"));
        assertFalse(result.contains("doc3"));
    }

    @Test
    void fuzzySearchIsCaseInsensitive() {
        List<Map<String, String>> docs = List.of(
                Map.of("id", "doc1", "text", "Hello WORLD"),
                Map.of("id", "doc2", "text", "WORLD world HELLO")
        );

        List<String> result = SearchEngine.search(docs, "hello world");

        assertEquals(2, result.size());
        // doc2: два вхождения world и одно hello — выше сумма вхождений при тех же двух словах запроса
        assertEquals("doc2", result.get(0));
        assertEquals("doc1", result.get(1));
    }
}
