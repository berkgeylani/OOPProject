package textprocess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatisticsCreator {

	private Map<String,Integer> triGramMap;

	private Map<String,Integer> wordMap;

	public StatisticsCreator(String content) {
		String[] splittedStr = content.split("[\\W]+");
		triGramMap=new HashMap<>();
		wordMap = new HashMap<>();

		for (int j = 0; j < splittedStr.length; j++) {
			addWordToMap(splittedStr[j].toLowerCase());
			String str = "_" + splittedStr[j].toLowerCase() + "_";
			int length = str.length();
			if (length > 3) {
				for (int i = 0; i < length - 2; i++) {
					// 0-1-2 , 1,2,3 , 2,3,4
					String substring = str.substring(i, i + 3);
					addTrigramToMap(substring);
				}
			}
		}
	}

	public Map<String, Integer> getTriGramList() {
		return triGramMap;
	}

	public Map<String,Integer> getWordList() {
		return wordMap;
	}
	
	private void addWordToMap(String str) {
		if (wordMap.containsKey(str)) {
			wordMap.put(str, wordMap.get(str)+1);
		}else {
			wordMap.put(str, 1);
		}
	}
	
	private void addTrigramToMap(String str) {
		if (triGramMap.containsKey(str)) {
			triGramMap.put(str, triGramMap.get(str)+1);
		}else {
			triGramMap.put(str, 1);
		}
	}
}
