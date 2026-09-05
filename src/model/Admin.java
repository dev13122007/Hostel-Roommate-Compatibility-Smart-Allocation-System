package model;

/**
 * Represents an administrator in the system.
 * Demonstrates inheritance extending the User base class.
 */
public class Admin extends User {
    private String department;

    public Admin(String id, String name, String email, String password, String department) {
        super(id, name, email, password, "ADMIN");
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public void displayDetails() {
        System.out.println("========================================");
        System.out.println("            ADMINISTRATOR INFO          ");
        System.out.println("========================================");
        System.out.println("Admin ID    : " + getId());
        System.out.println("Name        : " + getName());
        System.out.println("Email       : " + getEmail());
        System.out.println("Department  : " + department);
        System.out.println("Role        : " + getRole());
        System.out.println("========================================");
    }
}
