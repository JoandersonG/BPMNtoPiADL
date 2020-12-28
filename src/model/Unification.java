package model;

public class Unification {
    private Component fromComp;
    private Component toComp;
    private String fromPort;
    private String toPort;
    private Connector connector;

    public Unification(Component fromComp, Component toComp, Connector connector) {
        this.fromComp = fromComp;
        this.toComp = toComp;
        this.connector = connector;
    }

    public Component getFromComp() {
        return fromComp;
    }

    public Component getToComp() {
        return toComp;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    @Override
    public String toString() {
        return (fromComp == null ? "<desconhecido>" : fromComp.getInstanceName()) +
                "::" + fromPort + " unifies " +
                (connector == null ? "<desconhecido>::desconhecido" : connector.getInstanceName() +
                "::" + "de" + connector.getFrom().getId()) +
                "\n\t\t\t" +
                (connector == null ? "<desconhecido>::desconhecido" : connector.getInstanceName() +
                "::" + "para" + connector.getTo().getId()) + " unifies " +
                (toComp == null ? "<desconhecido>": toComp.getInstanceName()) +
                "::" + toPort;
    }
}