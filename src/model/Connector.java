package model;

public class Connector {
    private String name;
    private String instanceName;
    private String id;
    private Component from;
    private String fromId;
    private Component to;
    private String toId;

    public Connector(String name, String id, Component from, Component to) {
        this.name = name;
        this.id = id;
        this.from = from;
        this.to = to;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Component getFrom() {
        return from;
    }

    public void setFrom(Component from) {
        this.from = from;
    }

    public Component getTo() {
        return to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toPiADL() {
        StringBuilder piADLcode = new StringBuilder();
        piADLcode.append("connector ").append(name).append(" is abstraction (){\n");
        piADLcode.append("\tconnection ").append("de").append((from != null ? from.getName() : "Sem id")).append(" is in (Integer)\n");
        piADLcode.append("\tconnection ").append("para").append((to != null ? to.getName() : "Sem id")).append(" is out (Integer)\n");
        piADLcode.append("\tprotocol is {\n")
                .append("\t\t(via ").append("de").append((from != null ? from.getName() : "Sem id")).append(" receive Integer |")
                .append(" via ").append("para").append((to != null ? to.getName() : "Sem id")).append(" send Integer)*\n")
                .append("\t}\n")
                .append("\tbehavior is {\n")
                .append("\t\tvia ").append("de").append((from != null ? from.getName() : "Sem id")
        ).append(" receive x : Integer\n")
                .append("\t\tvia ").append("para").append((to != null ? to.getName() : "Sem id")).append(" send x\n")
                .append("\t\tbehavior()\n")
                .append("\t}\n")
                .append("}\n");
        return piADLcode.toString();
    }

    public void setTo(Component to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Id:" + id + "    From: " + (from != null ? from.getName() : "Sem nome") +
                "    To: " + (to != null ? to.getName() : "Sem nome") ;
    }
}
