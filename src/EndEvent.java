import java.util.ArrayList;

public class EndEvent extends Component {
    private String id;
    private ArrayList<String> incomingIds;

    public EndEvent(String name, String id, ArrayList<String> incomingIds) {
        super(name);
        this.id = id;
        this.incomingIds = incomingIds;
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
        return "    Name: '" + getName() + "'    id: " + id + "    Incoming: " + s.toString();
    }
}
