import java.util.ArrayList;

public class ChoreographyTask extends Component{
    private String id;
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
        super(name);
        this.id = id;
        this.incoming = incoming;
        this.outgoing = outgoing;
        this.initiatingParticipantId = initiatingParticipantId;
        this.participantIds = participantIds;
        this.messageFlowIds = messageFlowIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        StringBuilder pIds = new StringBuilder();
        for (String pId : participantIds) {
            pIds.append("        Participant: ").append(pId).append("\n");
        }
        StringBuilder mIds = new StringBuilder();
        for (String mId : messageFlowIds) {
            mIds.append("        MessageFlow: ").append(mId).append("\n");
        }
        return "Task id: " + id +
                "\n    Name: " + name +
                "\n    Incoming: " + incoming +
                "\n    Outgoing: " + outgoing +
                "\n    InitiatingId: " + initiatingParticipantId +
                "\n    Participants:\n" + pIds.toString() +
                "    MessageFlows: \n" + mIds.toString();
    }
}
