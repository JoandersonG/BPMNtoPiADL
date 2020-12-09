import java.util.ArrayList;

public class ChoreographyTask extends Component{
    private Connector incoming;
    private Connector outgoing;
    private String initiatingParticipantId;
    private ArrayList<String> participantIds;
    private ArrayList<String> messageFlowIds;

    public ChoreographyTask(
            String id,
            String name,
            Connector incoming,
            Connector outgoing,
            String initiatingParticipantId,
            ArrayList<String> participantIds,
            ArrayList<String> messageFlowIds
    ) {
        super(name, id);
        this.incoming = incoming;
        this.outgoing = outgoing;
        this.initiatingParticipantId = initiatingParticipantId;
        this.participantIds = participantIds;
        this.messageFlowIds = messageFlowIds;
    }

    public Connector getIncoming() {
        return incoming;
    }

    public void setIncoming(Connector incoming) {
        this.incoming = incoming;
    }

    public Connector getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Connector outgoing) {
        this.outgoing = outgoing;
    }

    public String getInitiatingParticipantId() {
        return initiatingParticipantId;
    }

    public void setInitiatingParticipantId(String initiatingParticipantId) {
        this.initiatingParticipantId = initiatingParticipantId;
    }

    public ArrayList<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(ArrayList<String> participantIds) {
        this.participantIds = participantIds;
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
                .append("\n    InitiatingId: ").append(initiatingParticipantId)
                .append("\n    Participants:\n");
        for (String pId : participantIds) {
            sb.append("        Participant: ").append(pId).append("\n");
        }
        sb.append("    MessageFlows: \n");
        for (String mId : messageFlowIds) {
            sb.append("        MessageFlow: ").append(mId).append("\n");
        }

        return sb.toString();
    }

    public String toPiADL() {
        StringBuilder piADLcode = new StringBuilder();
        piADLcode.append("component ").append(getId()).append(" is abstraction (){\n")
                .append("\tconnection ").append("entrada").append(" is in (Integer)\n")
                .append("\tconnection ").append("saida").append(" is out (Integer)\n")
                .append("\tprotocol is {\n")
                .append("\t\t(via ").append("entrada").append(" receive Integer |")
                .append(" via ").append("saida").append(" send Integer)*\n")
                .append("\t}\n")
                .append("\tbehavior is {\n")
                .append("\t\tvia ").append("entrada").append(" receive x : Integer\n")
                .append("\t\tvia ").append("saida")
                .append(" send x\n")
                .append("\t\tbehavior()\n")
                .append("\t}\n")
                .append("}\n");
        return piADLcode.toString();
    }
}
