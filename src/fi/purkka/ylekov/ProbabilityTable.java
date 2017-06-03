package fi.purkka.ylekov;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ProbabilityTable {
	
	private Map<String, Map<String, Integer>> table = new HashMap<>();
	
	private final static Random rand = new Random();
	
	public void update(String sentence) {
		String[] words = sentence.split("\\s+");
		for(int i = 1; i < words.length; i++) {
			String previous = words[i-1];
			String current = words[i];
			
			Map<String, Integer> nextTable = table.getOrDefault(previous, new HashMap<>());
			nextTable.put(current, nextTable.getOrDefault(current, 0)+1);
			table.put(previous, nextTable);
		}
	}
	
	public String generate() {
		int counter = 0;
		
		while(counter++ < 10000) {
			String generated = tryGenerate();
			int words = generated.split("\\s+").length;
			if(words >= 5 && words <= 10) return generated;
		}
		
		throw new AssertionError("Couldn't generate in 10000 tries");
	}
	
	private String tryGenerate() {
		StringBuilder sb = new StringBuilder();
		
		String word = randomKeyWeighted(table.get("!"));
		while(true) {
			sb.append(word + " ");
			word = randomKeyWeighted(table.get(word));
			if(word.equals(".")) break;
		}
		
		return sb.toString().trim();
	}
	
	private static <T> T randomKeyWeighted(Map<T, Integer> map) {
		Set<T> keys = map.keySet();
		int weightSum = map.values().stream().mapToInt(i->i).sum();
		int value = rand.nextInt(weightSum);
		
		for(T t : keys) {
			value -= map.get(t);
			if(value < 0) return t;
		}
		
		throw new AssertionError("impossible");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String word : table.keySet()) {
			sb.append(word + ":\n");
			
			Map<String, Integer> nextTable = table.getOrDefault(word, Collections.EMPTY_MAP);
			for(String subword : nextTable.keySet()) {
				sb.append("\t" + subword + ": " + nextTable.get(subword) + "\n");
			}
		}
		return sb.toString();
	}
}