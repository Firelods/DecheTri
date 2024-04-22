package etu.seinksansdoozebank.dechetri.model.user;

public class User {

    private String id;
    private String name;
    private Role role;
    private static int idCounter = 1;

    public User(String name, Role role) {
        this.id = String.valueOf(idCounter++);
        this.name = name;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(int idCounter) {
        User.idCounter = idCounter;
    }
}
