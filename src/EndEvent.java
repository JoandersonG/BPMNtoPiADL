import java.util.ArrayList;

public class EndEvent extends Component {
    private ArrayList<String> incomingIds;

    public EndEvent(String name, String id, ArrayList<String> incomingIds) {
        super(name, id);
        this.incomingIds = incomingIds;
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
        return "    Name: '" + getName() + "'    id: " + getId() + "    Incoming: " + s.toString();
    }
}
