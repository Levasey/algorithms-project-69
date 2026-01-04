package hexlet.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEngine {
    public static List<String> search(List<Map<String, String>> list, String word) {
        List<String> result = new ArrayList<String>();
        for (Map<String, String> map : list) {
            if (map.containsKey(word)) {
                result.add(map.get(word));
            }
        }
        return result;
    }
}
