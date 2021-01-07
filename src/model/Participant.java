package model;

public class Participant {
    private String id;
    private String name;
    private String newName;

    public Participant(String id, String name, String newName) {
        this.id = id;
        this.name = name;
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
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

    @Override
    public String toString() {
        return "Id: " + id + "  Name: " + name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((Participant)obj).getId().equals(id);
    }
}
