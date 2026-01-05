import hexlet.code.SearchEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchEngineTest {

    private List<Map<String, String>> testData;

    @BeforeEach
    void setUp() {
        testData = new ArrayList<>();

        Map<String, String> doc1 = new HashMap<>();
        doc1.put("id", "1");
        doc1.put("name", "John Doe");
        doc1.put("email", "john@example.com");
        testData.add(doc1);

        Map<String, String> doc2 = new HashMap<>();
        doc2.put("id", "2");
        doc2.put("name", "Jane Smith");
        doc2.put("email", "jane@example.com");
        doc2.put("phone", "123-456-7890");
        testData.add(doc2);

        Map<String, String> doc3 = new HashMap<>();
        doc3.put("id", "3");
        doc3.put("name", "Bob Johnson");
        doc3.put("email", "bob@example.com");
        testData.add(doc3);
    }

    @Test
    void testSearchExistingKey() {
        List<String> result = SearchEngine.search(testData, "email");

        assertEquals(3, result.size());
        assertTrue(result.contains("john@example.com"));
        assertTrue(result.contains("jane@example.com"));
        assertTrue(result.contains("bob@example.com"));
    }

    @Test
    void testSearchKeyPresentInSomeDocuments() {
        List<String> result = SearchEngine.search(testData, "phone");

        assertEquals(1, result.size());
        assertEquals("123-456-7890", result.get(0));
    }

    @Test
    void testSearchNonExistingKey() {
        List<String> result = SearchEngine.search(testData, "address");

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testSearchWithEmptyList() {
        List<Map<String, String>> emptyList = new ArrayList<>();
        List<String> result = SearchEngine.search(emptyList, "email");

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void testSearchWithNullKey() {
        // Этот тест проверяет поведение с null ключом
        // В текущей реализации он вернет пустой список, так как null не будет найден
        List<String> result = SearchEngine.search(testData, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchWithEmptyKey() {
        List<String> result = SearchEngine.search(testData, "");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchCaseSensitive() {
        Map<String, String> doc = new HashMap<>();
        doc.put("NAME", "Test User");
        doc.put("name", "Different Case");
        List<Map<String, String>> list = new ArrayList<>();
        list.add(doc);

        List<String> result = SearchEngine.search(list, "name");

        assertEquals(1, result.size());
        assertEquals("Different Case", result.get(0));
    }

    @Test
    void testSearchMultipleValuesSameKey() {
        Map<String, String> doc = new HashMap<>();
        doc.put("id", "1");
        doc.put("tag", "java");
        doc.put("tag", "python"); // HashMap перезапишет предыдущее значение

        List<Map<String, String>> list = new ArrayList<>();
        list.add(doc);

        List<String> result = SearchEngine.search(list, "tag");

        assertEquals(1, result.size());
        assertEquals("python", result.get(0)); // Последнее значение сохранится
    }

    @Test
    void testSearchWithDuplicateValues() {
        Map<String, String> doc1 = new HashMap<>();
        doc1.put("email", "test@example.com");

        Map<String, String> doc2 = new HashMap<>();
        doc2.put("email", "test@example.com"); // Дубликат значения

        List<Map<String, String>> list = new ArrayList<>();
        list.add(doc1);
        list.add(doc2);

        List<String> result = SearchEngine.search(list, "email");

        assertEquals(2, result.size());
        assertEquals("test@example.com", result.get(0));
        assertEquals("test@example.com", result.get(1));
    }

    @Test
    void testSearchPreservesOrder() {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> doc1 = new HashMap<>();
        doc1.put("id", "first");

        Map<String, String> doc2 = new HashMap<>();
        doc2.put("id", "second");

        Map<String, String> doc3 = new HashMap<>();
        doc3.put("id", "third");

        list.add(doc1);
        list.add(doc2);
        list.add(doc3);

        List<String> result = SearchEngine.search(list, "id");

        assertEquals(3, result.size());
        assertEquals("first", result.get(0));
        assertEquals("second", result.get(1));
        assertEquals("third", result.get(2));
    }

    @Test
    void testSearchWithSpecialCharactersInKey() {
        Map<String, String> doc = new HashMap<>();
        doc.put("key-with-dash", "value1");
        doc.put("key_with_underscore", "value2");
        doc.put("key.with.dot", "value3");
        doc.put("key space", "value4");

        List<Map<String, String>> list = new ArrayList<>();
        list.add(doc);

        List<String> result1 = SearchEngine.search(list, "key-with-dash");
        assertEquals(1, result1.size());
        assertEquals("value1", result1.get(0));

        List<String> result2 = SearchEngine.search(list, "key_with_underscore");
        assertEquals(1, result2.size());
        assertEquals("value2", result2.get(0));

        List<String> result3 = SearchEngine.search(list, "key.with.dot");
        assertEquals(1, result3.size());
        assertEquals("value3", result3.get(0));

        List<String> result4 = SearchEngine.search(list, "key space");
        assertEquals(1, result4.size());
        assertEquals("value4", result4.get(0));
    }

    @Test
    void testSearchReturnsNewListInstance() {
        List<String> result = SearchEngine.search(testData, "email");

        // Проверяем, что возвращается новый список (не тот же объект)
        List<String> anotherResult = SearchEngine.search(testData, "email");
        assertTrue(result != anotherResult);

        // Можем модифицировать результат, не влияя на другие вызовы
        result.add("extra");
        assertEquals(4, result.size());
        assertEquals(3, anotherResult.size());
    }
}
