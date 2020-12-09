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

    public String toPiADL() {
        StringBuilder piADLcode = new StringBuilder();
        piADLcode.append("component ").append(getId()).append(" is abstraction (){\n");
        for (int i = 0; i < incomingIds.size(); i++) {
            piADLcode.append("\tconnection ").append("entrada_").append(i+1).append(" is in (Integer)\n");
        }
        piADLcode.append("\tprotocol is {\n")
                .append("\t\t(");
        for (int i = 0; i < incomingIds.size(); i++) {
            piADLcode.append("via ").append("entrada_").append(i+1).append(" receive Integer");
            if (i == incomingIds.size() - 1) {
                piADLcode.append(")*\n");
            } else {
                piADLcode.append(" |\n\t\t");
            }
        }
        piADLcode.append("\t}\n")
                .append("\tbehavior is {\n");
        for (int i = 0; i < incomingIds.size(); i++) {
            piADLcode.append("\t\tvia ").append("entrada_").append(i+1).append(" receive x_").append(i).append(" : Integer\n");
        }
        piADLcode.append("\t\tbehavior()\n")
                .append("\t}\n")
                .append("}\n");
        return piADLcode.toString();
    }
}
