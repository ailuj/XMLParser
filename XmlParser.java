import javax.management.modelmbean.XMLParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XmlParser extends Thread {

	public static void main(String [] args){
		String firstArg="";
		if (args.length > 0){
			firstArg = args[0];
		}
		else{
			System.out.println("Please specify the folder containing the corpus files!");
			System.exit(1);
			}
		//initialize output values
		int noOfDocs=0;
		int noOfTotalWords=0;
		int threadFinished=0;
		PriorityBlockingQueue<String> wordCounts=new PriorityBlockingQueue<String>();
		PriorityBlockingQueue<String> topicCounts=new PriorityBlockingQueue<String>();
		PriorityBlockingQueue<String> placeCounts=new PriorityBlockingQueue<String>();
		PriorityBlockingQueue<String> peopleCounts=new PriorityBlockingQueue<String>();
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
		int threadCount=corpusFileList.size();
		for (int i=0;i<threadCount;i++){
			XmlParser corpusThread=new XmlParser();
			corpusThread.run(corpusFileList.get(i));
		}

		//start threads
		XmlParser corpusThreadOne=new XmlParser();
		corpusThreadOne.run();
	}

	public void run(String corpusFile) {
		System.out.println("Thread started. Reading in file "+corpusFile);
		// analyzes one corpus file
	}
}
