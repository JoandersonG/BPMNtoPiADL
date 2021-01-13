package model;

public abstract class Component {
    private String name;
    private String instanceName;
    private String originalName;
    private String id;

    public Component(String name, String originalName, String id) {
        this.name = name;
        this.originalName = originalName;
        this.id = id;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String toPiADL();

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((Component) obj).getId().equals(id);
    }
}
