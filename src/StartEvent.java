import java.util.ArrayList;

public class StartEvent extends Component{
    private ArrayList<String> outgoingIds;

    public StartEvent(String name, String id, ArrayList<String> outgoingIds) {
        super(name, id);
        this.outgoingIds = outgoingIds;
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
        return "    Name: '" + getName() + "'    id: " + getId() + "    Outgoing: " + s.toString();
    }

    public String toPiADL() {
        StringBuilder piADLcode = new StringBuilder();
        piADLcode.append("component ").append(getId()).append(" is abstraction (){\n");
        for (int i = 0; i < outgoingIds.size(); i++) {
            piADLcode.append("\tconnection ").append("saida_").append(i+1).append(" is out (Integer)\n");
        }
        piADLcode.append("\tprotocol is {\n")
        .append("\t\t(");
        for (int i = 0; i < outgoingIds.size(); i++) {
            piADLcode.append("via ").append("saida_").append(i+1).append(" send Integer");
            if (i == outgoingIds.size() - 1) {
                piADLcode.append(")*\n");
            } else {
                piADLcode.append(" |\n\t\t");
            }
        }
        piADLcode.append("\t}\n")
                .append("\tbehavior is {\n");
        for (int i = 0; i < outgoingIds.size(); i++) {
            piADLcode.append("\t\tvia ").append("saida_").append(i+1).append(" send 0\n");
        }
        piADLcode.append("\t\tbehavior()\n")
                .append("\t}\n")
                .append("}\n");
        return piADLcode.toString();
    }
}
