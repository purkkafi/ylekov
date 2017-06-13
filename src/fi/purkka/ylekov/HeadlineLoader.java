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
	
	private final static String URL_MOST_READ = "http://feeds.yle.fi/uutiset/v1/majorHeadlines/YLE_UUTISET.rss";
	private final static String URL_RECENT = "http://feeds.yle.fi/uutiset/v1/recent.rss?publisherIds=YLE_UUTISET";
	
	public static List<String> loadHeadlines() {
		List<String> all = new ArrayList<>();
		all.addAll(loadHeadlines(URL_MOST_READ));
		all.addAll(loadHeadlines(URL_RECENT));
		return all;
	}
	
	private static List<String> loadHeadlines(String loadUrl) {
		try {
			URL url = new URL(loadUrl);
			
			String content = new BufferedReader(new InputStreamReader(url.openStream())).lines()
					.collect(Collectors.joining("\n"));
			
			
			Pattern pattern = Pattern.compile("\\<item\\>\\n\\s*\\<title\\>(.*)\\<\\/title\\>");
			Matcher matcher = pattern.matcher(content);
			List<String> headlines = new ArrayList<>();
			
			while(matcher.find()) {
				headlines.add("[ " + matcher.group(1)
						.replaceAll("&quot;", "\"")
						.replaceAll("”", "\"")
						.replaceAll("[\\s]+", " ")
						.trim() + " ]");
			}
			
			return headlines;
		} catch (IOException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}
		
		return Collections.EMPTY_LIST;
	}
}