import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class UserHandler extends DefaultHandler {

    boolean rDate = false;
    boolean rTopics = false;
    boolean rPlaces = false;
    boolean rPeople = false;
    boolean rOrgs = false;
    boolean rText = false;
    boolean rTitle = false;
    boolean rDateline = false;
    boolean rBody = false;
    ArrayList<String> topics = new ArrayList<String>();
    ArrayList<String> places = new ArrayList<String>();
    ArrayList<String> people = new ArrayList<String>();
    ArrayList<String> text = new ArrayList<>();

    @Override
    public void startElement(
            String uri, String localName, String elName, Attributes attributes)
            throws SAXException{

            if(elName.equalsIgnoreCase("lewis")){

            } else if(elName.equalsIgnoreCase("reuters")){

            } else if(elName.equalsIgnoreCase("date")){
                rDate = true;
            } else if(elName.equalsIgnoreCase("topics")){
                rTopics = true;
            } else if(elName.equalsIgnoreCase("places")){
                rPlaces = true;
            } else if(elName.equalsIgnoreCase("people")){
                rPeople = true;
            } else if(elName.equalsIgnoreCase("orgs")){
                rOrgs = true;
            } else if(elName.equalsIgnoreCase("text")){
                rText = true;
            } else if(elName.equalsIgnoreCase("title")){
                rTitle = true;
            } else if(elName.equalsIgnoreCase("dateline")){
                rDateline = true;
            } else if(elName.equalsIgnoreCase("body")){
                rBody = true;
            }
    }

    @Override
    public void endElement(
            String uri, String localName, String elName) throws SAXException{
        if(elName.equalsIgnoreCase("lewis")) {
            Set<String> topicSet = new HashSet<String>(topics);
            Set<String> placesSet = new HashSet<String>(places);
            Set<String> peopleSet = new HashSet<String>(people);
            Set<String> textSet = new HashSet<String>(text);

            Map<String, Long> counts =
                    text.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

            System.out.println("results: ");
            System.out.println("topics total: " + topics.size() + " topics distinct: " + topicSet.size());
            System.out.println("places total: " + places.size() + " places distinct: " + placesSet.size());
            System.out.println("people total: " + people.size() + " people distinct: " + peopleSet.size());
            System.out.println("text total in body and title: " + text.size() + " text distinct in body and title: " + textSet.size());
            System.out.println("frequencies in text: " + counts);
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException{

        String string = new String(ch, start, length);
        String[] array = string.split("\\s+");
        System.out.print(Arrays.toString(array));

        if(rTopics){
            //fügt nur Strings zu den Topics, die nicht leer sind - vielleicht kann man das schöner lösen?
            for(int i = 0; i < array.length; i++) {
                if (array[i] != "") {
                    topics.add(array[i]);
                }
            }
            rTopics = false;
        } else if(rPlaces){
            for(int i = 0; i < array.length; i++) {
                if (array[i] != "") {
                    places.add(array[i]);
                }
            }
            rPlaces = false;

        } else if(rPeople){
            for(int i = 0; i < array.length; i++) {
                if (array[i] != "") {
                    people.add(array[i]);
                }
            }
            rPeople = false;
        } else if(rTitle || rBody){
            for(int i = 0; i < array.length; i++) {
                if (array[i] != "") {
                    text.add(array[i]);
                }
            }
            if(rTitle) rTitle = false;
            if(rBody) rBody = false;
        }

    }

}

/**
 * get:
 * number of documents
 * number of words in title and body, distinct and total
 * don't separate title and body
 * 30 most frequent words and their frequency
 * number of topics, places and people (total and distinct)
 */
