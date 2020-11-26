import java.util.ArrayList;

public class EndEvent {
    private String name;
    private String id;
    private ArrayList<String> incomingIds;

    public EndEvent(String name, String id, ArrayList<String> incomingIds) {
        this.name = name;
        this.id = id;
        this.incomingIds = incomingIds;
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

    public ArrayList<String> getIncomingIds() {
        return incomingIds;
    }

    public void setIncomingIds(ArrayList<String> incomingIds) {
        this.incomingIds = incomingIds;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String incomingId : incomingIds) {
            s.append(incomingId).append("\t");
        }
        s.append("\n");
        return "    Name: '" + name + "'    id: " + id + "    Incoming: " + s.toString();
    }
}
