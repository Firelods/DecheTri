package etu.seinksansdoozebank.dechetri.model.user;

public enum Role {
    USER,
    EMPLOYEE,
    MANAGER,
    ADMIN;

    public static Role fromString(String role) {
        switch (role) {
            case "Employee":
                return EMPLOYEE;
            case "Manager":
                return MANAGER;
            case "Administrateur":
                return ADMIN;
            default:
                return USER;
        }
    }

    public boolean canSeeStats() {
        return this == MANAGER || this == ADMIN;
    }
}
