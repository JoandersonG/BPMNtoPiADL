public class Connector {
    private String id;
    private String fromId;
    private String toId;

    public Connector(String id, String fromId, String toId) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
    }

    @Override
    public String toString() {
        return "Id:" + id + "    From: " + fromId + "    To: " + toId;
    }
}
