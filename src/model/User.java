package model;

/**
 * Abstract base class representing a general system user.
 * Demonstrates abstraction, encapsulation, and common user properties.
 */
public abstract class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role; // "STUDENT" or "ADMIN"

    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Verifies if the provided password matches this user's password.
     */
    public boolean verifyPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    /**
     * Abstract method to display user-specific profile details.
     */
    public abstract void displayDetails();
}
