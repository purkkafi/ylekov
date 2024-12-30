package fi.purkka.ylekov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.unbescape.xml.XmlEscape;

public class HeadlineLoader {
	
	private final static String URL_HYMY = "https://hymy.fi/feed/";
	private final static String URL_MTV = "https://api.mtvuutiset.fi/mtvuutiset/api/feed/rss/uutiset_uusimmat";
	private final static String URL_SEISKA = "https://www.seiska.fi/";
	private final static String URL_HS = "https://www.hs.fi/rss/tuoreimmat.xml";
	private final static String URL_IS = "https://www.is.fi/rss/tuoreimmat.xml";
	private final static String URL_YLE = "https://feeds.yle.fi/uutiset/v1/recent.rss?publisherIds=YLE_UUTISET";
	private final static String URL_IL = "https://www.iltalehti.fi/rss.xml";
	
	private final static Pattern PATTERN_TITLE_CDATA =
			Pattern.compile("\\<title\\>\\<!\\[CDATA\\[(.+?)\\]\\]");
	
	private final static Pattern PATTERN_ITEM_TITLE =
			Pattern.compile("\\<item\\>\\n\\s*\\<title\\>(.*)\\<\\/title\\>");
	
	private final static Pattern PATTERN_SEISKA =
			Pattern.compile("\\<h2.+\\>(.+)");
	
	public static List<String> loadHeadlines() {
		List<String> all = new ArrayList<>();
		all.addAll(loadHeadlines(URL_HYMY, PATTERN_ITEM_TITLE));
		all.addAll(loadHeadlines(URL_MTV, PATTERN_TITLE_CDATA));
		all.addAll(loadHeadlines(URL_SEISKA, PATTERN_SEISKA));
		all.addAll(loadHeadlines(URL_HS, PATTERN_TITLE_CDATA));
		all.addAll(loadHeadlines(URL_IS, PATTERN_TITLE_CDATA));
		all.addAll(loadHeadlines(URL_YLE, PATTERN_ITEM_TITLE));
		all.addAll(loadHeadlines(URL_IL, PATTERN_ITEM_TITLE));
		return all;
	}
	
	private static List<String> loadHeadlines(String loadUrl, Pattern pattern) {
		try(InputStream is = new URL(loadUrl).openStream()) {
			String content = new BufferedReader(new InputStreamReader(is)).lines()
					.collect(Collectors.joining("\n"));

			Matcher matcher = pattern.matcher(content);
			List<String> headlines = new ArrayList<>();
			
			while(matcher.find()) {
				headlines.add("[ " + formatHeadline(matcher.group(1)) + " ]");
			}
			
			System.out.println("Loaded " + headlines.size() + " from " + loadUrl);
			return headlines;
		} catch (IOException e) {
			System.err.println("Failed to load " + loadUrl);
			e.printStackTrace();
		}
		
		return Collections.EMPTY_LIST;
	}
	
	public static String formatHeadline(String headline) {
		if(headline.startsWith("<p>") && headline.endsWith("</p>")) {
			headline = headline.substring(3, headline.length()-4);
		}
		return XmlEscape.unescapeXml(headline)
				.replace("\u00AD", "")
				.replace("\u00A0", "")
				.replace("”", "\"")
				.replace("“", "\"")
				.replace("''", "\"")
				.replace(" - ", " – ")
				.replace("\"–", "\" –")
				.replace("–", "–")
				.replaceAll("[\\s]+", " ")
				.trim();
	}
}
