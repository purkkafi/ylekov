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
	
	private final static String URL_SEISKA = "https://www.seiska.fi/all.rss";
	private final static String URL_HS = "https://www.hs.fi/rss/tuoreimmat.xml";
	private final static String URL_IS = "https://www.is.fi/rss/tuoreimmat.xml";
	private final static String URL_YLE = "https://feeds.yle.fi/uutiset/v1/recent.rss?publisherIds=YLE_UUTISET";
	private final static String URL_IL = "http://www.iltalehti.fi/rss.xml";
	
	private final static Pattern PATTERN_TITLE_CDATA = Pattern.compile("\\<title\\>\\<!\\[CDATA\\[(.+)\\]\\]");
	private final static Pattern PATTERN_ITEM_TITLE = Pattern.compile("\\<item\\>\\n\\s*\\<title\\>(.*)\\<\\/title\\>");
	
	public static List<String> loadHeadlines() {
		List<String> all = new ArrayList<>();
		all.addAll(loadHeadlines(URL_SEISKA, PATTERN_ITEM_TITLE));
		all.addAll(loadHeadlines(URL_HS, PATTERN_TITLE_CDATA));
		all.addAll(loadHeadlines(URL_IS, PATTERN_TITLE_CDATA));
		all.addAll(loadHeadlines(URL_YLE, PATTERN_ITEM_TITLE));
		all.addAll(loadHeadlines(URL_IL, PATTERN_ITEM_TITLE));
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
						.replace("&#8221;", "\"")
						.replace("”", "\"")
						.replace("''", "\"")
						.replace(" - ", " – ")
						.replaceAll("[\\s]+", " ")
						.trim() + " ]");
			}
			
			System.out.println("Loaded " + headlines.size() + " from " + loadUrl);
			return headlines;
		} catch (IOException e) {
			System.out.println("Failed to load " + loadUrl + ": " + e.toString());
		}
		
		return Collections.EMPTY_LIST;
	}
}