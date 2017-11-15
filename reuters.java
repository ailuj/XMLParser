import java.io.*;
import java.util.*;

public class reuters {
	public static int countText(String s, int textCount, HashMap<String,Integer> text) {
		String[] parts = s.toLowerCase().split("\\s+");
		for (int i=0; i<parts.length; i++) {
			if (!parts[i].equals("")) {
				if (text.containsKey(parts[i])) {
					text.put(parts[i],text.get(parts[i])+1);
					textCount++;
				} else {
					text.put(parts[i],1);
					textCount++;
				}
			}
		}
		return textCount;
	}

	public static int count(String caseString, String s, int count, HashSet<String> tokenSet) {
		s = s.substring(caseString.length(),s.length()-(caseString.length()+1));
		int pos = 0;
		String tmp = null;
		while (s.length() > pos) {
			tmp = s.substring(s.indexOf("<D>", pos)+3, s.indexOf("</D>", pos)).toLowerCase();
			if (tokenSet.contains(tmp)) {
				count++;
			} else {
				count++;
				tokenSet.add(tmp);
			}
			pos = s.indexOf("</D>", pos) + 4;
		}
		return count;
	}

	public static <K, V extends Comparable<? super V>> LinkedList<Map.Entry<K, V>> mostFrequent(Map<K, V> map) {
        LinkedList<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        for (int i=0; i<30; i++) {
        	Map.Entry<K, V> entry = list.get(i);
        	System.out.println(entry.getKey() + ": " + ((Integer) entry.getValue()).intValue());
        }
        return list;
    }

	public static void main(String[] args) {
		BufferedReader br = null;
		FileReader fr = null;
		int docCount = 0;
		int placeCount = 0;
		int peopleCount = 0;
		int topicCount = 0;
		int textCount = 0;
		boolean body = false;
		boolean title = false;
		HashSet<String> places = new HashSet<String>();
		HashSet<String> people = new HashSet<String>();
		HashSet<String> topics = new HashSet<String>();
		HashMap<String,Integer> text = new HashMap<String,Integer>();
		File folder = new File(args[0]);
		//Files lesen
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".xml")) {
				try {
					br = new BufferedReader(new FileReader(file));
					String s = null;
					String tmp = null;
					while ((s = br.readLine()) != null) {
						if (title) {
							if (s.contains("</TITLE>")) {
								title = false;
								s = s.substring(0,s.indexOf("</TITLE>"));
							}
							textCount = countText(s, textCount, text);
							continue;
						}
						if (body) {
							if (s.contains("</BODY>")) {
								body = false;
								s = s.substring(0,s.indexOf("</BODY>"));
							}
							textCount = countText(s, textCount, text);
							continue;
						}
						if (s.contains("<REUTERS")) {
							docCount++;
						} else if (s.contains("<TITLE>")) {
							if (s.contains("</TITLE>")) {
								s = s.substring(s.indexOf("<TITLE>")+7,s.indexOf("</TITLE>"));
							} else {
								s = s.substring(s.indexOf("<TITLE>")+7);
								title = true;
							}
							textCount = countText(s, textCount, text);
						} else if (s.contains("<BODY>")) {
							body = true;
							tmp = s.substring(s.indexOf("<BODY>") + 6);
							textCount = countText(tmp, textCount, text);
						} else if (s.contains("<PLACES>")) {
							placeCount = count("<PLACES>", s, placeCount, places);
						} else if (s.contains("<PEOPLE>")) {
							peopleCount = count("<PEOPLE>", s, peopleCount, people);
						} else if (s.contains("<TOPICS>")) {
							topicCount = count("<TOPICS>", s, topicCount, topics);
						}
					}
					if (br != null) {
						br.close();
					}
					if (fr != null) {
						fr.close();
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		//Ausgabe
		System.out.println("Anzahl Dokumente: " + docCount);
		System.out.println("Anzahl Wörter: " + textCount + " (" + text.size() + " distinct)");
		System.out.println("Anzahl Topics: " + topicCount + " (" + topics.size() + " distinct)");
		System.out.println("Anzahl Places: " + placeCount + " (" + places.size() + " distinct)");
		System.out.println("Anzahl People: " + peopleCount + " (" + people.size() + " distinct)");
		System.out.println("\nhäufigste Wörter:");
		mostFrequent(text);
	}
}