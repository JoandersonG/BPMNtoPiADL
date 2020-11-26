import java.util.ArrayList;

public class StartEvent {
    private String name;
    private String id;
    private ArrayList<String> outgoingIds;

    public StartEvent(String name, String id, ArrayList<String> outgoingIds) {
        this.name = name;
        this.id = id;
        this.outgoingIds = outgoingIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getOutgoingIds() {
        return outgoingIds;
    }

    public void setOutgoingIds(ArrayList<String> outgoingIds) {
        this.outgoingIds = outgoingIds;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String outgoingId : outgoingIds) {
            s.append(outgoingId).append("\t");
        }
        s.append("\n");
        return "    Name: '" + name + "'    id: " + id + "    Outgoing: " + s.toString();
    }
}
