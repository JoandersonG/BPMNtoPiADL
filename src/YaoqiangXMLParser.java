import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class YaoqiangXMLParser {

    ArrayList<Participant> participants;
    ArrayList<Message> messages;
    DocumentBuilder builder;

    YaoqiangXMLParser() throws ParserConfigurationException {
        participants = new ArrayList<>();
        messages = new ArrayList<>();
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    //This method parses Yaoqiang's BPMN 2.0's coreography diagram
    public void parseBPMN(String filePath) throws IOException, SAXException {
        Document doc = builder.parse(filePath);
        parseParticipants(doc);
        parseMessage(doc);
    }

    private void parseParticipants(Document doc) {
        NodeList participantsNodes = doc.getElementsByTagName("participant");
        for (int i = 0; i < participantsNodes.getLength(); i++) {
            Node node = participantsNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element participant = (Element) node;
                String participantId = participant.getAttribute("id");
                String participantName = participant.getAttribute("name");
                participants.add(new Participant(participantId, participantName));
            }
        }
    }

    private void parseMessage(Document doc) {
        NodeList messageNodes = doc.getElementsByTagName("message");
        for (int i = 0; i < messageNodes.getLength(); i++) {
            Node node = messageNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element message = (Element) node;
                String id = message.getAttribute("id");
                String name = message.getAttribute("name");
                messages.add(new Message(id, name));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Participants: \n");
        for (Participant p : participants) {
            s.append("\t");
            s.append(p.toString());
            s.append("\n");
        }
        s.append("Messages: \n");
        for (Message m : messages) {
            s.append("\t");
            s.append(m.toString());
            s.append("\n");
        }
        return s.toString();
    }
}
