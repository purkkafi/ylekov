package fi.purkka.ylekov;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ylekov {
	
	public final static Path CACHE_FILE = Paths.get("cached_headlines");
	
	public static void main(String[] args) {
		if(args.length == 1 && args[0].equals("update")) {
			HeadlineList list;
			if(Files.exists(CACHE_FILE)) {
				list = HeadlineList.of(CACHE_FILE);
			} else {
				list = HeadlineList.of(Collections.EMPTY_LIST);
			}
			
			for(String headline : HeadlineLoader.loadHeadlines()) {
				list.append(headline);
			}
			
			list.write(CACHE_FILE);
			
			System.out.println("Updated headline cache");
			System.exit(0);
		}
		
		if(args[0].equals("generate")) {
			int n = args.length == 2 ? Integer.parseInt(args[1]) : 1;
			
			HeadlineList list = HeadlineList.of(CACHE_FILE);
			List<String> headlines = new ArrayList<>(list.all());
			ProbabilityTable table = new ProbabilityTable();
			
			for(String headline : headlines) {
				table.update(headline);
			}
			
			for(int i = 0; i < n; i++) {
				System.out.println(table.generate(list));
			}
			System.exit(0);
		}
		
		
		System.err.println("Usage: update | generate [n]");
	}
}