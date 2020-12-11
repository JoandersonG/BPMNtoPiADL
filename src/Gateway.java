import java.util.ArrayList;

public class Gateway extends Component {
    private ArrayList<Connector> incomings;
    private ArrayList<Connector> outgoings;

    public Gateway(String name, String id, ArrayList<Connector> incomings, ArrayList<Connector> outgoings) {
        super(name, id);
        this.incomings = incomings;
        this.outgoings = outgoings;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("    Name: '").append(getName()).append("'    id: ").append(getId());
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
