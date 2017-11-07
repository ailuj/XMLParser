import javax.management.modelmbean.XMLParseException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Entry implements Comparable<Entry>{
	public String key;
	public int value;
	public Entry(String key, int value){
		this.key=key;
		this.value=value;
	}

	@Override
	public int compareTo(Entry other){
		return new Integer(this.value).compareTo(new Integer(other.value));
	}
}

public class XmlParser extends Thread {

	int threadNumber;
	String corpusFile;

	public XmlParser(String corpusFile, int threadNumber){
		this.corpusFile=corpusFile;
		this.threadNumber=threadNumber;
	}

	ReentrantLock intLock =new ReentrantLock();
	static int                                   noOfDocs       =0;
	static int                                   threadFinished =0;
	static TreeMap<String,Integer>               topicCounts    = new TreeMap<>();
	static TreeMap<String,Integer>               placeCounts    = new TreeMap<>();
	static TreeMap<String,Integer>               peopleCounts   = new TreeMap<>();

	public static void main(String [] args){
		//read arguments
		String firstArg="";
		try{
			firstArg = args[0];
		}
		catch(Exception e){
			System.out.println("Please specify the folder containing the corpus files!");
			System.exit(1);
		}

		//read in corpus file handles
		List<String> corpusFileList=new ArrayList<String>();
		try (Stream<Path> paths = Files.walk(Paths.get("./"+firstArg))) {
			paths
					.filter((Files::isRegularFile))
					.forEach(x->corpusFileList.add(x.toString()));
		}
		catch(IOException e) {
			System.out.println("Argument is not a directory!");
			System.exit(1);
		}
		//initialize and start threads
		int threadCount=corpusFileList.size();
		XmlParser[] threadList=new XmlParser[threadCount];
		for (int i=0;i<threadCount;i++){
			XmlParser parser=new XmlParser(corpusFileList.get(i),i);
			threadList[i]=parser;
			parser.start();
		}
		for (int i=0;i<threadCount;i++){
			try{
				threadList[i].join();
			}
			catch (Exception e){
			}
		}
		//print all values
		System.out.println("Anzahl Dokumente: "+noOfDocs);

		int noOfTotalTopics=0;
		if(!topicCounts.isEmpty()) {
			noOfTotalTopics = topicCounts.values().stream().mapToInt(Integer::intValue).sum();
		}
		System.out.println("Anzahl Topics: "+noOfTotalTopics+" ("+topicCounts.size()+" distinct)");

		int noOfTotalPlaces=0;
		if(!placeCounts.isEmpty()) {
		noOfTotalPlaces = placeCounts.values().stream().mapToInt(Integer::intValue).sum();
	}
		System.out.println("Anzahl Places: "+noOfTotalPlaces+" ("+placeCounts.size()+" distinct)");

		int noOfTotalPeople=0;
		if(!peopleCounts.isEmpty()) {
			noOfTotalPeople = peopleCounts.values().stream().mapToInt(Integer::intValue).sum();
		}
		System.out.println("Anzahl People: "+noOfTotalPeople+" ("+peopleCounts.size()+" distinct)");
}



	public void run() {
		System.out.println("Thread "+this.threadNumber+" started. Reading in file "+this.corpusFile);
		try(BufferedReader br = new BufferedReader(new FileReader(corpusFile))) {
			String line = br.readLine();
			Pattern documentStart = Pattern.compile("<REUTERS");
			Pattern topicLine = Pattern.compile("<TOPICS>(.*)</TOPICS>");
			Pattern dTags = Pattern.compile("<D>(.+?)</D>");
			Pattern peopleLine = Pattern.compile("<PEOPLE>(.*)</PEOPLE>");
			Pattern placesLine = Pattern.compile("<PLACES>(.*)</PLACES>");

			Pattern title = Pattern.compile("<TITLE>(.*)</TITLE>");

			boolean insideBody=false;
			String text="";
			while (line != null) {
				//if new document
				Matcher m = documentStart.matcher(line);
				if (m.find()) {
					System.out.println("Document");
					intLock.lock();
					noOfDocs++;
					intLock.unlock();
				}

				//if topicLine
				m = topicLine.matcher(line);
				if (m.find()) {
					m = dTags.matcher(line);
					while (m.find()) {
						System.out.println("Topic "+m.group(1));
						String topic = m.group(1);
						intLock.lock();
						if (topicCounts.containsKey(topic)) {
							Integer value = topicCounts.get(topic);
							topicCounts.put(topic, ++value);
						}
						else {
							topicCounts.put(topic,1);
						}
						intLock.unlock();

					}
				}
				//if personLine
				m = peopleLine.matcher(line);
				if (m.find()) {
					m = dTags.matcher(line);
					while (m.find()) {
						System.out.println("People "+m.group(1));
						String topic = m.group(1);
						intLock.lock();
						if (peopleCounts.containsKey(topic)) {
							Integer value = peopleCounts.get(topic);
							peopleCounts.put(topic, ++value);
						}
						else {
							peopleCounts.put(topic,1);
						}
						intLock.unlock();
					}
				}
				//if placeLine
				m = placesLine.matcher(line);
				if (m.find()) {
					m = dTags.matcher(line);
					while (m.find()) {
						System.out.println("Place "+m.group(1));
						String place = m.group(1);
						intLock.lock();
						if (placeCounts.containsKey(place)) {
							Integer value = placeCounts.get(place);
							placeCounts.put(place, ++value);
						}
						else {
							placeCounts.put(place,1);
						}
						intLock.unlock();
					}
				}

				//if title
				m = title.matcher(line);
				if (m.find()) {
					System.out.println("Title "+m.group(1));
					text=text+" "+m.group(1);
				}

				line = br.readLine();
			}
		}
		catch(Exception e){
			System.out.println(e);
			intLock.lock();
			threadFinished++;
			intLock.unlock();
		}
		intLock.lock();
		threadFinished++;
		intLock.unlock();
	}
}
