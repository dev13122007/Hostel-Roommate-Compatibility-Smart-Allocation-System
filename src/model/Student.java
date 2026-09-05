package model;

/**
 * Represents a student residing in the hostel.
 * Demonstrates inheritance extending the User base class and aggregation of Preference.
 */
public class Student extends User {
    private String course;
    private String year;
    private String hostel;
    private boolean isAllocated;
    private String allocatedRoomNo;
    private int allocatedBedNo;
    private Preference preference;

    public Student(String studentId, String name, String email, String password,
                   String course, String year, String hostel) {
        super(studentId, name, email, password, "STUDENT");
        this.course = course;
        this.year = year;
        this.hostel = hostel;
        this.isAllocated = false;
        this.allocatedRoomNo = "None";
        this.allocatedBedNo = 0;
        this.preference = null;
    }

    public Student(String studentId, String name, String email, String password,
                   String course, String year, String hostel, boolean isAllocated,
                   String allocatedRoomNo, int allocatedBedNo) {
        super(studentId, name, email, password, "STUDENT");
        this.course = course;
        this.year = year;
        this.hostel = hostel;
        this.isAllocated = isAllocated;
        this.allocatedRoomNo = allocatedRoomNo;
        this.allocatedBedNo = allocatedBedNo;
        this.preference = null;
    }

    public String getStudentId() {
        return getId();
    }

    public void setStudentId(String studentId) {
        setId(studentId);
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public boolean isAllocated() {
        return isAllocated;
    }

    public void setAllocated(boolean allocated) {
        isAllocated = allocated;
    }

    public String getAllocatedRoomNo() {
        return allocatedRoomNo;
    }

    public void setAllocatedRoomNo(String allocatedRoomNo) {
        this.allocatedRoomNo = allocatedRoomNo;
    }

    public int getAllocatedBedNo() {
        return allocatedBedNo;
    }

    public void setAllocatedBedNo(int allocatedBedNo) {
        this.allocatedBedNo = allocatedBedNo;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    @Override
    public void displayDetails() {
        System.out.println("========================================");
        System.out.println("            STUDENT PROFILE             ");
        System.out.println("========================================");
        System.out.printf("  %-18s : %s%n", "Student ID", getId());
        System.out.printf("  %-18s : %s%n", "Name", getName());
        System.out.printf("  %-18s : %s%n", "Email", getEmail());
        System.out.printf("  %-18s : %s%n", "Course", course);
        System.out.printf("  %-18s : %s%n", "Year", year);
        System.out.printf("  %-18s : %s%n", "Hostel Block", hostel);
        System.out.printf("  %-18s : %s%n", "Allocation Status", (isAllocated ? "ALLOCATED" : "NOT ALLOCATED"));
        if (isAllocated) {
            System.out.printf("  %-18s : %s (Bed %d)%n", "Assigned Room", allocatedRoomNo, allocatedBedNo);
        }
        System.out.println("========================================");
    }

    /**
     * Converts to serialized pipe-delimited format for storage in students.txt.
     */
    public String toFileString() {
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" +
               course + "|" + year + "|" + hostel + "|" + isAllocated + "|" +
               allocatedRoomNo + "|" + allocatedBedNo;
    }

    /**
     * Parses from serialized line in students.txt.
     */
    public static Student fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 10) {
            boolean allocated = Boolean.parseBoolean(parts[7]);
            int bedNo = Integer.parseInt(parts[9]);
            return new Student(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], allocated, parts[8], bedNo);
        }
        return null;
    }
}
