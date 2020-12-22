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
    ArrayList<Gateway> gateways;
    DocumentBuilder builder;

    YaoqiangXMLParser() throws ParserConfigurationException {
        participants = new ArrayList<>();
        messages = new ArrayList<>();
        messageFlows = new ArrayList<>();
        connectors = new ArrayList<>();
        tasks = new ArrayList<>();
        startEvents = new ArrayList<>();
        endEvents = new ArrayList<>();
        gateways = new ArrayList<>();
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
        parseGateways(doc);
        System.out.println();
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

    private Component getComponent(String id) {
        for (StartEvent s : startEvents) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        for (EndEvent e : endEvents) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        for (ChoreographyTask ct : tasks) {
            if (ct.getId().equals(id)) {
                return ct;
            }
        }
        return null;
    }

    private void parseConnectors(Document doc) {
        NodeList connectorNodes = doc.getElementsByTagName("sequenceFlow");
        for (int i = 0; i < connectorNodes.getLength(); i++) {
            Node node = connectorNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element connector = (Element) node;
                String id = connector.getAttribute("id");
                String name = "Fluxo_" + (i+1);
                String fromId = connector.getAttribute("sourceRef");
                String toId = connector.getAttribute("targetRef");
                connectors.add(new Connector(name, id, getComponent(fromId), getComponent(toId)));
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
                id = getValidId(name, id);
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
                Connector in = getConnector(incoming);
                Connector out = getConnector(outgoing);
                ChoreographyTask ct = new ChoreographyTask(
                        id,
                        name,
                        in,
                        out,
                        initiating,
                        choreoParticipantIds,
                        messageFlowIds
                        );
                tasks.add(ct);
                if (in != null) {
                    in.setTo(ct);
                }
                if (out != null) {
                    out.setFrom(ct);
                }
            }
        }
    }

    private void parseGateways(Document doc) {
        parseGatewayByType(doc, "exclusiveGateway");
        parseGatewayByType(doc, "parallelGateway");
    }
    private void parseGatewayByType(Document doc, String gatewayType) {
        NodeList gatewayList = doc.getElementsByTagName(gatewayType);
        for (int i = 0; i < gatewayList.getLength(); i++) {
            Node node = gatewayList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element gateway = (Element) node;
                String id = gateway.getAttribute("id");
                String name = gateway.getAttribute("name");
                id = getValidId(name != null ? !name.equals("") ? name : gatewayType : gatewayType, id);
                ArrayList<Connector> incoming = new ArrayList<>();
                ArrayList<Connector> outgoing = new ArrayList<>();
                NodeList insideInfoNode = gateway.getChildNodes();
                for (int j = 0; j < insideInfoNode.getLength(); j++) {
                    Node itemNode = insideInfoNode.item(j);
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element item = (Element) itemNode;
                        String itemName = item.getTagName();
                        switch (itemName) {
                            case "incoming":
                                Connector inc = getConnector(item.getTextContent());
                                if (inc != null){
                                    incoming.add(inc);
                                }

                                break;
                            case "outgoing":
                                outgoing.add(getConnector(item.getTextContent()));
                                break;
                        }
                    }
                }
                Gateway eg = new Gateway(
                        name,
                        id,
                        incoming,
                        outgoing,
                        gatewayType.equals("exclusiveGateway") ? Gateway.Type.EXCLUSIVE_GATEWAY : Gateway.Type.PARALLEL_GATEWAY
                );
                gateways.add(eg);
                for (Connector in : incoming) {
                    in.setTo(eg);
                }
                for (Connector out : outgoing) {
                    out.setFrom(eg);
                }
            }
        }
    }

    /*
    * Method for creating a valid task name given a name possibly with blank spaces and special characters
    */
    private String getValidId(String name, String id) {
        String[] splitName = name.split("[^a-zA-Z0-9_]");
        StringBuilder sb = new StringBuilder();
        if (splitName[0].matches("[0-9].*")) {
            sb.append("c");
        }
        for (String piece : splitName) {
            if (piece.equals("e")){
                continue;
            }
            if (!piece.matches("[0-9].*") && !piece.equals("") && piece.charAt(0) >= 97) { // turning first letter of each word capital
                piece = piece.replaceFirst("[a-zA-Z]", String.valueOf((char) (piece.charAt(0) - 32)));
            }
            sb.append(piece);
        }
        sb.append("_").append(id);
        return sb.toString();
    }

    private void parseStartEvents(Document doc) {

        NodeList nodes = doc.getElementsByTagName("startEvent");
        if (nodes.getLength() < 1) {
          System.out.println("Erro: quantidade de eventos de começo inválida. Forneça ao menos um startEvent.");
          exit(1);
        }
        for (int j = 0; j < nodes.getLength(); j++) {

            Element start = (Element) nodes.item(j);
            String name = start.getAttribute("name");
            if (name == null || name.isEmpty()) {
                name = "Start_" + (j+1);
            }
            String startId = getValidId(name, String.valueOf(j+1));
            NodeList outgoingNodeList = start.getElementsByTagName("outgoing");
            ArrayList<String> outgoingIds = new ArrayList<>();
            for (int i = 0; i < outgoingNodeList.getLength(); i++) {
                Node outgoingNode = outgoingNodeList.item(i);
                if (outgoingNode.getNodeType() == Node.ELEMENT_NODE) {
                    outgoingIds.add(outgoingNode.getTextContent());
                }
            }
            StartEvent newStart = new StartEvent(name, startId, outgoingIds);
            startEvents.add(newStart);
            for (String cId : outgoingIds) {
                Connector cnn = getConnector(cId);
                if (cnn != null) {
                cnn.setFrom(newStart);
                }
            }
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
            String name = end.getAttribute("name");
            if (name == null || name.isEmpty()) {
                name = "End_" + (j+1);
            }
            String endId = getValidId(name, String.valueOf(j+1));
            NodeList incomingNodeList = end.getElementsByTagName("incoming");
            ArrayList<String> incomingIds = new ArrayList<>();
            for (int i = 0; i < incomingNodeList.getLength(); i++) {
                Node incomingNode = incomingNodeList.item(i);
                if (incomingNode.getNodeType() == Node.ELEMENT_NODE) {
                    incomingIds.add(incomingNode.getTextContent());
                }
            }
            EndEvent newEnd = new EndEvent(name, endId, incomingIds);
            endEvents.add(newEnd);
            for (String cId : incomingIds) {
                Connector cnn = getConnector(cId);
                if (cnn != null) {
                    cnn.setTo(newEnd);
                }
            }
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
        s.append("Gateways: \n");
        for (Gateway eg : gateways) {
            s.append("\t");
            s.append(eg.toString());
            s.append("\n");
        }
        return s.toString();
    }

    public String generatePiADL() {
        StringBuilder piADLcode = new StringBuilder();
        for (StartEvent s : startEvents) {
            piADLcode.append(s.toPiADL());
        }
        for (Connector c : connectors) {
            piADLcode.append(c.toPIADL());
        }
        for (ChoreographyTask ct : tasks) {
            piADLcode.append(ct.toPiADL());
        }
        for (EndEvent e : endEvents) {
            piADLcode.append(e.toPiADL());
        }
        for (Gateway g : gateways) {
            piADLcode.append(g.toPiADL());
        }
        return piADLcode.toString();
    }
}
