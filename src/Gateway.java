import java.util.ArrayList;

public class Gateway extends Component {
    public enum Type { EXCLUSIVE_GATEWAY, PARALLEL_GATEWAY }

    private ArrayList<Connector> incomings;
    private ArrayList<Connector> outgoings;
    private Type type;

    public Gateway(String name, String id, ArrayList<Connector> incomings, ArrayList<Connector> outgoings, Type type) {
        super(name, id);
        this.incomings = incomings;
        this.outgoings = outgoings;
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("    Name: '").append(getName()).append("'    Type: ").append(type).append("    id: ").append(getId());
        s.append("\n\t    Incoming: ");
        for (Connector inc : incomings) {
            s.append(inc.getId()).append("\t");
        }
        s.append("\n\t    Outgoing: ");
        for (Connector out : outgoings) {
            s.append(out.getId()).append("\t");
        }
        s.append("\n");
        return s.toString();
    }
}
