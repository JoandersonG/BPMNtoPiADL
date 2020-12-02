public class Connector {
    private String name;
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

    public void setTo(Component to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Id:" + id + "    From: " + fromId + "    To: " + toId;
    }
}
