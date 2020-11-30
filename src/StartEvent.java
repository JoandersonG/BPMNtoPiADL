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

    public String toPiADL() {
        StringBuilder piADLcode = new StringBuilder();
        piADLcode.append("component ").append(name).append(" is abstraction (){\n");
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
                .append("}");
        return piADLcode.toString();
    }
}
