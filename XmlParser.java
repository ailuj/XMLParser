import javax.management.modelmbean.XMLParseException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlParser extends Thread {

	int threadNumber;
	String corpusFile;

	public XmlParser(String corpusFile, int threadNumber){
		this.corpusFile=corpusFile;
		this.threadNumber=threadNumber;
	}

	Semaphore intLock=new Semaphore(1,true);
	int noOfDocs=0;
	int noOfTotalWords=0;
	int threadFinished=0;
	PriorityBlockingQueue<String> wordCounts=new PriorityBlockingQueue<String>();
	PriorityBlockingQueue<String> topicCounts=new PriorityBlockingQueue<String>();
	PriorityBlockingQueue<String> placeCounts=new PriorityBlockingQueue<String>();
	PriorityBlockingQueue<String> peopleCounts=new PriorityBlockingQueue<String>();

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
		for (int i=0;i<threadCount;i++){
			new XmlParser(corpusFileList.get(i),i).start();

		}
	}

	public void run() {
		System.out.println("Thread "+this.threadNumber+" started. Reading in file "+this.corpusFile);
		try(BufferedReader br = new BufferedReader(new FileReader(corpusFile))) {
			String line = br.readLine();
			Pattern p=Pattern.compile("<TOPICS>");
			while(line!=null){
				Matcher m=p.matcher(line);
				m.matches();
				if(m.find()) {
					System.out.println(this.threadNumber+", "+line);
				}
				line=br.readLine();
			}
			return;
		}
		catch(Exception e){
			System.out.println(e);
			intLock.tryAcquire();
			threadFinished++;
			intLock.release();
		}
		// analyzes one corpus file
	}
}
