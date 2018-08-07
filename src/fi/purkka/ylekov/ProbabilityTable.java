package fi.purkka.ylekov;

import java.util.*;

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
	
	public String generate(HeadlineList list) {
		int counter = 0;
		
		generate:
		while(counter++ < 10000) {
			String generated = tryGenerate();
			int words = generated.split("\\s+").length;
			
			if(words >= 5 && generated.length() < 130) {
				String headline = fixQuotes(generated);
				
				// Reject headlines with too many em dashes
				long emDashes = headline.chars().filter(c -> c == '–').count();
				if(emDashes > 1) {
					continue generate;
				}
				
				// Reject headlines that are substrings of stored ones
				for(String stored : list.all()) {
					if(stored.contains(headline)) {
						continue generate;
					}
				}
				
				return headline;
			}
		}
		
		throw new AssertionError("Couldn't generate in 10000 tries");
	}
	
	private static String fixQuotes(String headline) {
		while(count(headline, '\"') % 2 != 0) {
			if(headline.endsWith("\"")) {
				if(headline.contains(": ")) {
					headline = headline.replaceFirst(": ", ": \"");
				} else if(headline.contains("– ")) {
					headline = headline.replaceFirst("– ", "– \"");
				} else {
					headline = "\"" + headline;
				}
				
			} else if(headline.startsWith("\"")) {
				if(headline.contains(" –")) {
					headline = headline.replaceFirst(" –", "\" –");
				} else {
					headline = headline + "\"";
				}
				
			} else if(headline.contains(": \"") || headline.contains("– \"")
					|| headline.contains(" \"")) {
				headline = headline + "\"";
				
			} else if(headline.contains("\" –")) {
				headline = "\"" + headline;
				
			} else if(headline.contains("\",")) {
				if(headline.contains(": ") && isBefore(headline, ": ", "\", ")) {
					headline = headline.replaceFirst(": ", ": \"");
				} else if(headline.contains("– ") && isBefore(headline, "– ", "\", ")) {
					headline = headline.replace("– ", "– \"");
				} else {
					headline = "\"" + headline;
				}
			} else {
				System.err.println("Unable to fix quotes for [" + headline + "]");
				return headline;
			}
			
		}
		return headline;
	}
	
	private static boolean isBefore(String string, String first, String second) {
		return string.indexOf(first) < string.indexOf(second);
	}
	
	private static int count(String str, char c) {
		int count = 0;
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == c) count++;
		}
		return count;
	}
	
	private String tryGenerate() {
		StringBuilder sb = new StringBuilder();
		
		String word = randomKeyWeighted(table.get("["));
		while(true) {
			sb.append(word + " ");
			word = randomKeyWeighted(table.get(word));
			if(word.equals("]")) break;
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