import java.util.ArrayList;

public class ChoreographyTask {
    private String id;
    private String name;
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
        this.id = id;
        this.name = name;
        this.incoming = incoming;
        this.outgoing = outgoing;
        this.initiatingParticipantId = initiatingParticipantId;
        this.participantIds = participantIds;
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
