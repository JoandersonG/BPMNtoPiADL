package model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
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

    public YaoqiangXMLParser() throws ParserConfigurationException {
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
        try {
            Document doc = builder.parse(filePath);
            parseParticipants(doc);
            parseMessage(doc);
            parseMessageAssociation(doc);
            parseMessageFlows(doc);
            parseConnectors(doc);
            parseCoreographyTasks(doc);
            parseSubChoreographyTasks(doc);
            parseStartEvents(doc);
            parseEndEvents(doc);
            parseGateways(doc);
            System.out.println();
        } catch (FileNotFoundException exception) {
            throw new FileNotFoundException();
        }
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
                    System.out.println("Erro: um dos participantes envolvidos em  model.Message flow não foi encontrado");
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
                Participant initiating = getParticipant(task.getAttribute("initiatingParticipantRef"));
                String name = task.getAttribute("name");
                String id = name.equals("") ? getValidId("Task", String.valueOf(i + 1)) : getValidId(name, String.valueOf(i + 1));
                String incoming = "";
                String outgoing = "";
                ArrayList<Participant> choreoParticipants = new ArrayList<>();
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
                                choreoParticipants.add(getParticipant(item.getTextContent()));
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
                        initiating,
                        choreoParticipants,
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

    private void parseSubChoreographyTasks(Document doc) {
        NodeList tasksList = doc.getElementsByTagName("subChoreography");
        for (int i = 0; i < tasksList.getLength(); i++) {
            Node node = tasksList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element task = (Element) node;
                Participant initiating = getParticipant(task.getAttribute("initiatingParticipantRef"));
                String name = task.getAttribute("name");
                String id = name.equals("") ? getValidId("SubTask", String.valueOf(i + 1)) : getValidId(name, String.valueOf(i + 1));
                String incoming = "";
                String outgoing = "";
                ArrayList<Participant> choreoParticipants = new ArrayList<>();
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
                                choreoParticipants.add(getParticipant(item.getTextContent()));
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
                        initiating,
                        choreoParticipants,
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
                String name = gateway.getAttribute("name");
                String id = getValidId(name.equals("")? "model.Gateway" : name, String.valueOf(i + 1));
                ArrayList<String> incomings = new ArrayList<>();
                ArrayList<String> outgoings = new ArrayList<>();
                NodeList insideInfoNode = gateway.getChildNodes();
                for (int j = 0; j < insideInfoNode.getLength(); j++) {
                    Node itemNode = insideInfoNode.item(j);
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element item = (Element) itemNode;
                        String itemName = item.getTagName();
                        switch (itemName) {
                            case "incoming":
                                    incomings.add(item.getTextContent());
                                break;
                            case "outgoing":
                                outgoings.add(item.getTextContent());
                                break;
                        }
                    }
                }
                Gateway eg = new Gateway(
                        name,
                        id,
                        incomings,
                        outgoings,
                        gatewayType.equals("exclusiveGateway") ? Gateway.Type.EXCLUSIVE_GATEWAY : Gateway.Type.PARALLEL_GATEWAY
                );
                gateways.add(eg);
                for (String in : incomings) {
                    Connector c = getConnector(in);
                    if (c!= null) {
                        c.setTo(eg);
                    }
                }
                for (String out : outgoings) {
                    Connector c = getConnector(out);
                    if (c!= null) {
                        c.setFrom(eg);
                    }
                }
            }
        }
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /*
    * Method for creating a valid task name given a name possibly with blank spaces and special characters
    */
    private String getValidId(String name, String id) {
        name = removerAcentos(name);
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
        if (thereIsSuchId(sb.toString())) {
            sb.append(id);
        }
        int i = 1;
        while (thereIsSuchId(sb.toString())) {
            sb.append(i);
            i++;
        }
        return sb.toString();
    }

    private boolean thereIsSuchId(String id) {
        for (ChoreographyTask t : tasks) {
            if (t.getId().equals(id)) {
                return true;
            }
        }
        for (StartEvent s : startEvents) {
            if (s.getId().equals(id)) {
                return true;
            }
        }
        for (EndEvent e : endEvents) {
            if (e.getId().equals(id)) {
                return true;
            }
        }
        for (Gateway g : gateways) {
            if (g.getId().equals(id)) {
                return true;
            }
        }
        return false;
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
                name = "Start";
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
                name = "End";
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

    public String generatePiADL(String archName) {
        StringBuilder piADLcode = new StringBuilder();
        //Gerar código navegando por elementos:
        ArrayList<Component> alreadyRead = new ArrayList<>();
        for (StartEvent s : startEvents) {
            piADLcode.append(toPiADL(alreadyRead, s));
        }
        piADLcode.append(generateArchitecture(archName));
        return piADLcode.toString();
    }

    private String toPiADL(ArrayList<Component> alreadyRead, Component component) {
        StringBuilder s = new StringBuilder();
        s.append(component.toPiADL());
        ArrayList<Connector> connectors = findConnectorFrom(component.getId());
        for (Connector conn : connectors) {
            s.append(conn.toPiADL());
            if (!alreadyRead.contains(conn.getTo())) {
                s.append(toPiADL(alreadyRead, conn.getTo()));
                alreadyRead.add(conn.getTo());
            }
        }
        return s.toString();
    }

    private ArrayList<Connector> findConnectorFrom(String cId) {
        ArrayList<Connector> conns = new ArrayList<>();
        for (Connector con : connectors) {
            if (con.getFrom().getId().equals(cId)) {
                conns.add(con);
            }
        }
        return conns;
    }

    private String generateArchitecture(String archName) {
        StringBuilder s = new StringBuilder();
        s.append("architecture ").append(archName).append(" is abstraction () {\n");
        s.append("\tbehavior is {\n")
         .append("\t\tcompose {\n");
        for (int i = 0; i < startEvents.size(); i++) {
            startEvents.get(i).setInstanceName(i == 0? "i": "i" + (i+1));
            s.append(i == 0? "\t\t\t" : "\t\t\tand ").append(startEvents.get(i).getInstanceName()).append(" is ").append(startEvents.get(i).getId()).append("()\n");
        }
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setInstanceName(i == 0? "t": "t" + (i+1));
            s.append("\t\t\tand ").append(tasks.get(i).getInstanceName()).append(" is ").append(tasks.get(i).getId()).append("()\n");
        }
        for (int i = 0; i < gateways.size(); i++) {
            gateways.get(i).setInstanceName(i == 0? "gw": "gw" + (i+1));
            s.append("\t\t\tand ").append(gateways.get(i).getInstanceName()).append(" is ").append(gateways.get(i).getId()).append("()\n");
        }
        for (int i = 0; i < endEvents.size(); i++) {
            endEvents.get(i).setInstanceName(i == 0? "f": "f" + (i+1));
            s.append("\t\t\tand ").append(endEvents.get(i).getInstanceName()).append(" is ").append(endEvents.get(i).getId()).append("()\n");
        }
        for (int i = 0; i < connectors.size(); i++) {
            connectors.get(i).setInstanceName(i == 0? "c": "c" + (i+1));
            s.append("\t\t\tand ").append(connectors.get(i).getInstanceName()).append(" is ").append(connectors.get(i).getName()).append("()\n");
        }
        s.append("\t\t} where {\n");

        ArrayList<Unification> unifications = new ArrayList<>();

        for (Connector c : connectors) {
            unifications.add(new Unification(c.getFrom(), c.getTo(), c));
        }

        for (StartEvent se : startEvents) {
            for (int i = 0; i < se.getOutgoings().size(); i++) {
                ArrayList<Unification> us = findUnificationByFrom(unifications, se);
                us.get(i).setFromPort(se.getOutgoings().get(i));
            }
        }
        for (ChoreographyTask ct : tasks) {
            //from
            ArrayList<Unification> us = findUnificationByFrom(unifications, ct);
            for (Unification u : us) {
                    u.setFromPort(ct.getOutgoing());
            }
            //to
            us = findUnificationByTo(unifications, ct);
            for (Unification u : us) {
                u.setToPort(ct.getIncoming());
            }
        }
        for (Gateway g : gateways) {
            for (int i = 0; i < g.getIncomings().size(); i++) {
                ArrayList<Unification> us = findUnificationByTo(unifications, g);
                //a quantidade de incomings é igual à quantidade de Unifications obtida
                us.get(i).setToPort(g.getIncomings().get(i));
            }
            for (int i = 0; i < g.getOutgoings().size(); i++) {
                ArrayList<Unification> us = findUnificationByFrom(unifications, g);
                us.get(i).setFromPort(g.getOutgoings().get(i));
            }
        }
        for (EndEvent ee : endEvents) {
            for (int i = 0; i < ee.getIncomings().size(); i++) {
                ArrayList<Unification> us = findUnificationByTo(unifications, ee);
                us.get(i).setToPort(ee.getIncomings().get(i));
            }
        }
        for (Unification u : unifications) {
            s.append("\t\t\t").append(u).append("\n");
        }
        s.append("\t\t}\n");
        s.append("\t}\n");
        s.append("}\n");
        s.append("behavior is {\n");
        s.append("\tbecome(").append(archName).append("())\n");
        s.append("}\n");

        return s.toString();
    }
    private ArrayList<Unification> findUnificationByFrom(ArrayList<Unification> unifications, Component comp) {
        ArrayList<Unification> unificationsMatch = new ArrayList<>();
        for (Unification u : unifications) {
            if (u.getFromComp() != null && u.getFromComp().getId().equals(comp.getId())
            ) {
                unificationsMatch.add(u);
            }
        }
        return unificationsMatch;
    }
    private ArrayList<Unification> findUnificationByTo(ArrayList<Unification> unifications, Component comp) {
        ArrayList<Unification> unificationsMatch = new ArrayList<>();
        for (Unification u : unifications) {
            if (u.getToComp() != null && u.getToComp().getId().equals(comp.getId())) {
                unificationsMatch.add(u);
            }
        }
        return unificationsMatch;
    }
}
