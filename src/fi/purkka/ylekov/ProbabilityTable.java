package fi.purkka.ylekov;

import java.util.*;
import java.util.stream.Collectors;

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
				String headline = newFixQuotes(generated);
				
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
	
	private static String newFixQuotes(String headline) {
		if(!headline.contains("\"")) return headline;
		
		List<String> chunks = splitIntoChunks(headline);
		
		for(int i = 0; i < chunks.size()-1; i++) {
			String chunk = chunks.get(i);
			
			// When a quote is encountered, make sure it has a closing equivalent
			// and add one if it doesn't
			if(chunk.equals("\"")) {
				if(i+2 >= chunks.size() || !chunks.get(i+2).equals("\"")) {
					String tryAddQuote = chunks.get(i+1);
					
					if(!tryAddQuote.startsWith(",") && !tryAddQuote.equals(" ")) {
						chunks.set(i+1, addQuoteToEnd(tryAddQuote));
						
					} else {
						// Inserting a quote earlier is preferable
						chunks.set(i-1, addQuoteToStart(chunks.get(i-1)));
					}
				}
				i = i+2;
			}
		}
		
		
		// Special case: headline ends with quote
		if(chunks.get(chunks.size()-1).equals("\"")) {
			if(chunks.size() <= 2 || !chunks.get(chunks.size()-3).equals("\"")) {
				chunks.set(chunks.size()-2, addQuoteToStart(chunks.get(chunks.size()-2)));
			}
		}
		
		String fixed = chunks.stream().collect(Collectors.joining());
		
		// Special case: headlines like [aaaa "aaaaa" aaaa"]
		if(count(fixed, '"') % 2 != 0 && fixed.endsWith("\"")) {
			fixed = fixed.substring(0, fixed.length()-2);
		}
		
		return fixed;
	}
	
	private static String addQuoteToEnd(String chunk) {
		if(chunk.endsWith(" ")) return chunk.substring(0, chunk.length()-1) + "\" ";
		return chunk + "\"";
	}
	
	private static String addQuoteToStart(String chunk) {
		if(chunk.startsWith(" ")) return " \"" + chunk.substring(1);
		return "\"" + chunk;
	}
	
	private static Set<Character> SPLIT_CHARS = new HashSet<>(Arrays.asList('"', '–', '|', ':'));
	
	private static List<String> splitIntoChunks(String headline) {
		List<String> chunks = new ArrayList<>();
		int lastIndex = 0;
		int index = 0;
		
		while(index < headline.length()) {
			
			trySplit:
			if(SPLIT_CHARS.contains(headline.charAt(index))) {
				if(headline.charAt(index) == ':' && headline.charAt(index-1) != ' ' && headline.charAt(index+1) != ' ') {
					break trySplit;
				}
				
				chunks.add(headline.substring(lastIndex, index));
				chunks.add(headline.substring(index, index+1));
				lastIndex = index+1;
			}
			index++;
		}
		chunks.add(headline.substring(lastIndex));
		
		while(chunks.contains("")) chunks.remove("");
		
		return chunks;
	}
	
	/* static { // debug
		String[] tests = new String[] {
				"Kissa: \"Olen nälkäinen",
				"Kani söi ihmisen – maistui hyvältä\"",
				"Nyt.fi | Trump \"teki jotain",
				"\"Kaksi kiloa – jotain",
				"\"Aika hienoa\", Pete Buttigieg sanoi ja \"söi koiran",
				"Tämä on outo erityistapaus\"",
				"Tätä kaunotarta ei tarvitse pelätä\", sanoo 18 vuotta vankeutta",
				"Testataan joo\" – tämäkin outo tapaus",
				"Nyt.fi-kolumni | Tanskalainen lastenanimaatio \"penismies\" Johnista hämmentää, huvittaa ja joimme samppanjaa\""
		};
		
		System.out.println("-----------");
		for(String test : tests) {
			System.out.println(test + " ---->");
			System.out.println(newFixQuotes(test));
		}
		System.out.println("-----------");
	}*/
	
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
