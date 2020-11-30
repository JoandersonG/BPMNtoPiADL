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
import static java.lang.System.exit;

public class YaoqiangXMLParser {

    ArrayList<Participant> participants;
    ArrayList<Message> messages;
    ArrayList<MessageFlow> messageFlows;
    ArrayList<Connector> connectors;
    ArrayList<ChoreographyTask> tasks;
    ArrayList<StartEvent> startEvents;
    ArrayList<EndEvent> endEvents;
    DocumentBuilder builder;

    YaoqiangXMLParser() throws ParserConfigurationException {
        participants = new ArrayList<>();
        messages = new ArrayList<>();
        messageFlows = new ArrayList<>();
        connectors = new ArrayList<>();
        tasks = new ArrayList<>();
        startEvents = new ArrayList<>();
        endEvents = new ArrayList<>();
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    //This method parses Yaoqiang's BPMN 2.0's coreography diagram
    public void parseBPMN(String filePath) throws IOException, SAXException {
        Document doc = builder.parse(filePath);
        parseParticipants(doc);
        parseMessage(doc);
        parseMessageAssociation(doc);
        parseMessageFlows(doc);
        parseConnectors(doc);
        parseCoreographyTasks(doc);
        parseStartEvents(doc);
        parseEndEvents(doc);
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

    private void parseMessageAssociation(Document doc) {
        NodeList associationNodes = doc.getElementsByTagName("association");
        for (int i = 0; i < associationNodes.getLength(); i++) {
            Node node = associationNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element association = (Element) node;
                String messageId = association.getAttribute("sourceRef");
                String participantId = association.getAttribute("targetRef");
                addParticipantToMessage(messageId,participantId);
            }
        }
    }

    private void parseMessageFlows(Document doc) {
        NodeList messageFlowNodes = doc.getElementsByTagName("messageFlow");
        for (int i = 0; i < messageFlowNodes.getLength(); i++) {
            Node node = messageFlowNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element messageFlow = (Element) node;
                String id = messageFlow.getAttribute("id");
                String sendId = messageFlow.getAttribute("sourceRef");
                String receiveId = messageFlow.getAttribute("targetRef");
                try {
                    messageFlows.add(new MessageFlow(id,getParticipant(sendId),getParticipant(receiveId)));
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro: um dos participantes envolvidos em  Message flow não foi encontrado");
                }
            }
        }
    }

    private void parseConnectors(Document doc) {
        NodeList connectorNodes = doc.getElementsByTagName("sequenceFlow");
        for (int i = 0; i < connectorNodes.getLength(); i++) {
            Node node = connectorNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element connector = (Element) node;
                String id = connector.getAttribute("id");
                String fromId = connector.getAttribute("sourceRef");
                String toId = connector.getAttribute("targetRef");
                connectors.add(new Connector(id, fromId, toId));
            }
        }
    }

    private void parseCoreographyTasks(Document doc) {
        NodeList tasksList = doc.getElementsByTagName("choreographyTask");
        for (int i = 0; i < tasksList.getLength(); i++) {
            Node node = tasksList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element task = (Element) node;
                String id = task.getAttribute("id");
                String initiating = task.getAttribute("initiatingParticipantRef");
                String name = task.getAttribute("name");
                String incoming = "";
                String outgoing = "";
                ArrayList<String> choreoParticipantIds = new ArrayList<>();
                ArrayList<String> messageFlowIds = new ArrayList<>();
                NodeList insideInfoNode = task.getChildNodes();
                for (int j = 0; j < insideInfoNode.getLength(); j++) {
                    Node itemNode = insideInfoNode.item(j);
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element item = (Element) itemNode;
                        String itemName = item.getTagName();
                        switch (itemName) {
                            case "incoming":
                                incoming = item.getTextContent();
                                break;
                            case "outgoing":
                                outgoing = item.getTextContent();
                                break;
                            case "participantRef":
                                choreoParticipantIds.add(item.getTextContent());
                                break;
                            case "messageFlowRef":
                                messageFlowIds.add(item.getTextContent());
                                break;
                        }
                    }
                }
                ChoreographyTask ct = new ChoreographyTask(
                        id,
                        name,
                        getConnector(incoming),
                        getConnector(outgoing),
                        initiating,
                        choreoParticipantIds,
                        messageFlowIds
                        );
                tasks.add(ct);
            }
        }
    }
    private void parseStartEvents(Document doc) {

        NodeList nodes = doc.getElementsByTagName("startEvent");
        if (nodes.getLength() < 1) {
          System.out.println("Erro: quantidade de eventos de começo inválida. Forneça ao menos um startEvent.");
          exit(1);
        }
        for (int j = 0; j < nodes.getLength(); j++) {

            Element start = (Element) nodes.item(j);
            String startId = start.getAttribute("id");
            String name = start.getAttribute("name");
            if (name == null || name.isEmpty()) {
                name = "Start_" + (j+1);
            }
            NodeList outgoingNodeList = start.getElementsByTagName("outgoing");
            ArrayList<String> outgoingIds = new ArrayList<>();
            for (int i = 0; i < outgoingNodeList.getLength(); i++) {
                Node outgoingNode = outgoingNodeList.item(i);
                if (outgoingNode.getNodeType() == Node.ELEMENT_NODE) {
                    outgoingIds.add(outgoingNode.getTextContent());
                }
            }
            startEvents.add(new StartEvent(name, startId, outgoingIds));
        }
    }

    private Participant getParticipant(String id) {
        for (Participant p : participants) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private void parseEndEvents(Document doc) {

        NodeList nodes = doc.getElementsByTagName("endEvent");
        if (nodes.getLength() < 1) {
            System.out.println("Erro: quantidade de eventos de fim inválida. Forneça ao menos um endEvent.");
            exit(1);
        }
        for (int j = 0; j < nodes.getLength(); j++) {

            Element end = (Element) nodes.item(j);
            String endId = end.getAttribute("id");
            String name = end.getAttribute("name");
            NodeList incomingNodeList = end.getElementsByTagName("incoming");
            ArrayList<String> incomingIds = new ArrayList<>();
            for (int i = 0; i < incomingNodeList.getLength(); i++) {
                Node incomingNode = incomingNodeList.item(i);
                if (incomingNode.getNodeType() == Node.ELEMENT_NODE) {
                    incomingIds.add(incomingNode.getTextContent());
                }
            }
            endEvents.add(new EndEvent(name, endId, incomingIds));
        }
    }

    private Connector getConnector(String id) {
        for (Connector c : connectors) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    //this method adds participantId into the message on messages array with messageId id
    private void addParticipantToMessage(String messageId, String participantId) {
        Participant participant = getParticipant(participantId);
        if (participant == null) {
            return;
        }
        for (Message m : messages) {
            if (m.getId().equals(messageId)) {
                m.setParticipant(participant);
                return;
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
        s.append("MessageFlows: \n");
        for (MessageFlow mf : messageFlows) {
            s.append("\t");
            s.append(mf.toString());
            s.append("\n");
        }
        s.append("Connectors: \n");
        for (Connector c : connectors) {
            s.append("\t");
            s.append(c.toString());
            s.append("\n");
        }
        s.append("ChoreographyTasks: \n");
        for (ChoreographyTask ct : tasks) {
            s.append("\t");
            s.append(ct.toString());
            s.append("\n");
        }
        s.append("StartEvents: \n");
        for (StartEvent se : startEvents) {
            s.append("\t");
            s.append(se.toString());
            s.append("\n");
        }
        s.append("EndEvents: \n");
        for (EndEvent ee : endEvents) {
            s.append("\t");
            s.append(ee.toString());
            s.append("\n");
        }
        return s.toString();
    }

    public String generatePiADL() {
        StringBuilder piADLcode = new StringBuilder();
        for (StartEvent s : startEvents) {
            piADLcode.append(s.toPiADL());
        }
        return piADLcode.toString();
    }
}
