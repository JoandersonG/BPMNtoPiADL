import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            YaoqiangXMLParser parser = new YaoqiangXMLParser();
            parser.parseBPMN("diagram.bpmn");
            String piADLcode = parser.generatePiADL("Example");
            System.out.println(piADLcode);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
