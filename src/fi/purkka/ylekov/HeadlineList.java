package fi.purkka.ylekov;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class HeadlineList {
	
	public final static int MAX_SIZE = 1000;
	
	private final LinkedHashSet<String> set = new LinkedHashSet<>();
	
	public void append(String string) {
		set.add(string);
		if(set.size() > MAX_SIZE) {
			Iterator<String> iter = set.iterator();
			iter.next();
			iter.remove();
		}
	}
	
	public ArrayList<String> all() {
		return new ArrayList<>(set);
	}
	
	public void write(Path path) {
		try {
			Files.write(path, all());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static HeadlineList of(Path path) {
		try {
			HeadlineList list = new HeadlineList();
			
			for(String line : Files.readAllLines(path)) {
				list.append(line);
			}
			
			return list;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static HeadlineList of(List<String> strings) {
		HeadlineList list = new HeadlineList();
		for(String string : strings) { list.append(string); }
		return list;
	}
	
	@Override
	public String toString() {
		return set.toString();
	}
}