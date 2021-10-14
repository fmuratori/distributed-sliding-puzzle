package part1;

import java.util.*;

public class WordsCounter {

    public static String stringifyMap(Map<String, Integer> map, int count) {
        String output = "";
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            output += entry.getKey() + " -> " + entry.getValue() + "\n";
            i += 1;
            if (i >= count) {
                break;
            }
        }
        return output;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static List<String> processText(String text) {
        // replace newline char with space
        String newText = text.trim().replaceAll("\n", " ");
        // remove non alfa-numeric  chars
        newText = newText.replaceAll("[^a-zA-Z]", " "); //0-9
        // replace multiple consecutive spaces with a single char
        newText = newText.replaceAll("( )+", " ");
        String[] words = newText.split(" ");
        return new ArrayList<>(Arrays.asList(words));
    }

    public static List<String> filterStopWords(List<String> words, List<String> stopWords) {
        return words.stream().filter(stopWords::contains).toList();
    }

    public static void filterStopWords(Map<String, Integer> wordsCount, List<String> stopWords) {
        stopWords.forEach(wordsCount::remove);
    }

    public static Map<String, Integer> countWords(List<String> words) {
        Map<String, Integer> dictionary = new HashMap<>();
        for (String word : words) {
            dictionary.putIfAbsent(word, 0);
            dictionary.put(word, dictionary.get(word)+1);
        }
        return dictionary;
    }

    public static Map<String, Integer> sumCounters(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> map3 = new HashMap<>();

        for (String key : map1.keySet()) {
            map3.put(key, map1.get(key) + map2.getOrDefault(key, 0));
        }

        for (String key : map2.keySet()) {
            map3.putIfAbsent(key, map2.get(key));
        }

        return map3;
    }


}
