import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserDemo{
    public static void main(String[] args){
        try{
            File inputFile = new File("/Users/juliadullin/Documents/Uni_Master/2.Semester/maschinelleSprachverarbeitung/Übung01/reuters-corpus/reut2-001.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            UserHandler userhandler = new UserHandler();
            saxParser.parse(inputFile, userhandler);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
