package ass1.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Dictionary {

	private Map<String, Integer> dictionary;
	
	public Dictionary() {
		dictionary = new HashMap<>();
	}
	
	synchronized public void update(Map<String, Integer> partialDictionary) {
		for (Entry<String, Integer> elem : partialDictionary.entrySet()) {
			String key = elem.getKey();
			if (!dictionary.containsKey(key))
				dictionary.put(key, elem.getValue());
			else
				dictionary.put(key, dictionary.get(key) + elem.getValue());
		}
	}
	
	synchronized public Map<String, Integer> get() {
		return Collections.unmodifiableMap(dictionary);
	}
}
