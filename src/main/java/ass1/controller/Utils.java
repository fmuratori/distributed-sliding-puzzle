package ass1.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Utils {
	public static String stringifyMap(Map<String, Integer> map, int count) {
		String output = "";
		int i = 0;
		for (Entry<String, Integer> entry : map.entrySet()) {
			output += entry.getKey() + " -> " + entry.getValue() + "\n";
			i += 1;
			if (i >= count) {
				break;
			}
		}
		return output;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
		
//		return map.keySet().stream().sorted().limit(10).collect(Collectors.toMap(Function.identity(), map::get));
    }

	public static List<File> searchPDFFilesInDirectory(String directory) {
		List<File> files;
		try {
			files = Files.list(Paths.get(directory))
			        .map(Path::toFile)
			        .filter(file -> file.isFile() && file.getName().endsWith(".pdf"))
			        .collect(Collectors.toList());
			return files;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static List<String> loadStopwords(String filepath) {
		try {
			return Arrays.asList(new String(Files.readAllBytes(
					Paths.get(filepath))).split("\n"));
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	public static Map<String, Integer> getWordsCount(String text, List<String> stopwords) {
		Map<String, Integer> dictionary = new HashMap<>();
		// Process text (split words, remove symbols)
		List<String> words = processText(text);
		// Count words (except stopwords)
		for (String word : words) {
			dictionary.putIfAbsent(word, 0);
			dictionary.put(word, dictionary.get(word)+1);
		}
		for (String word : stopwords) {
			if (dictionary.containsKey(word))
				dictionary.remove(word);
		}
		return dictionary;
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
}
