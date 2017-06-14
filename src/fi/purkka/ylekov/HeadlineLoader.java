package fi.purkka.ylekov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HeadlineLoader {
	
	private final static String URL_IS = "http://www.is.fi/rss/tuoreimmat.xml";
	private final static String URL_YLE = "http://feeds.yle.fi/uutiset/v1/recent.rss?publisherIds=YLE_UUTISET";
	private final static String URL_IL = "http://www.iltalehti.fi/rss.xml";
	
	private final static Pattern PATTERN_IS = Pattern.compile("\\<title\\>\\<!\\[CDATA\\[(.+)\\]\\]");
	private final static Pattern PATTERN_YLE_IL = Pattern.compile("\\<item\\>\\n\\s*\\<title\\>(.*)\\<\\/title\\>");
	
	public static List<String> loadHeadlines() {
		List<String> all = new ArrayList<>();
		all.addAll(loadHeadlines(URL_IS, PATTERN_IS));
		all.addAll(loadHeadlines(URL_YLE, PATTERN_YLE_IL));
		all.addAll(loadHeadlines(URL_IL, PATTERN_YLE_IL));
		return all;
	}
	
	private static List<String> loadHeadlines(String loadUrl, Pattern pattern) {
		try {
			URL url = new URL(loadUrl);
			
			String content = new BufferedReader(new InputStreamReader(url.openStream())).lines()
					.collect(Collectors.joining("\n"));
			

			Matcher matcher = pattern.matcher(content);
			List<String> headlines = new ArrayList<>();
			
			while(matcher.find()) {
				headlines.add("[ " + matcher.group(1)
						.replace("&quot;", "\"")
						.replace("&#246;", "ö")
						.replace("&#228;", "ä")
						.replace("&#229;", "Å")
						.replace("&#214;", "Ö")
						.replace("&#196;", "Ä")
						.replace("&#197;", "Å")
						.replace("”", "\"")
						.replace(" - ", " – ")
						.replaceAll("[\\s]+", " ")
						.trim() + " ]");
			}
			
			System.out.println("Loaded " + headlines.size() + " from " + loadUrl);
			return headlines;
		} catch (IOException e) {
			System.out.println("Failed to load " + loadUrl);
		}
		
		return Collections.EMPTY_LIST;
	}
}