package model;

import java.util.ArrayList;

public class ChoreographyTask extends Component{
    private String incoming;
    private String outgoing;
    private Participant initParticipant;
    private ArrayList<Participant> participants;
    private ArrayList<String> messageFlowIds;

    public ChoreographyTask(
            String id,
            String name,
            Participant initParticipant,
            ArrayList<Participant> participants,
            ArrayList<String> messageFlowIds
    ) {
        super(name, id);
        this.initParticipant = initParticipant;
        this.participants = participants;
        this.messageFlowIds = messageFlowIds;
    }

    public String getIncoming() {
        return incoming;
    }

    public void setIncoming(String incoming) {
        this.incoming = incoming;
    }

    public void setOutgoing(String outgoing) {
        this.outgoing = outgoing;
    }

    public String getOutgoing() {
        return outgoing;
    }

    public Participant getInitParticipant() {
        return initParticipant;
    }

    public void setInitParticipant(Participant initParticipant) {
        this.initParticipant = initParticipant;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    public ArrayList<String> getMessageFlowIds() {
        return messageFlowIds;
    }

    public void setMessageFlowIds(ArrayList<String> messageFlowIds) {
        this.messageFlowIds = messageFlowIds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task id: ").append(getId()).append("\n    Name: ")
                .append(getName() != null ? getName() : "Sem Nome")
                .append("\n    Incoming: ").append(incoming)
                .append("\n    Outgoing: ").append(outgoing)
                .append("\n    InitiatingId: ").append(initParticipant)
                .append("\n    Participants:\n");
        for (Participant p : participants) {
            sb.append("        model.Participant: ").append(p.getId()).append("\n");
        }
        sb.append("    MessageFlows: \n");
        for (String mId : messageFlowIds) {
            sb.append("        model.MessageFlow: ").append(mId).append("\n");
        }

        return sb.toString();
    }

    public String toPiADL() {
        StringBuilder piADLcode = new StringBuilder();
        incoming = "entrada";
        outgoing = "saida";
        piADLcode.append("component ").append(getId()).append(" is abstraction (){\n")
                .append("\tconnection ").append(incoming).append(" is in (Integer)\n")
                .append("\tconnection ").append(outgoing).append(" is out (Integer)\n")
                .append("\tprotocol is {\n")
                .append("\t\t(via ").append(incoming).append(" receive Integer |")
                .append(" via ").append(outgoing).append(" send Integer)*\n")
                .append("\t}\n")
                .append("\tbehavior is {\n")
                .append("\t\tvia ").append(incoming).append(" receive x : Integer\n")
                .append("\t\tvia ").append(outgoing)
                .append(" send x\n")
                .append("\t\tbehavior()\n")
                .append("\t}\n")
                .append("}\n");
        return piADLcode.toString();
    }
}
