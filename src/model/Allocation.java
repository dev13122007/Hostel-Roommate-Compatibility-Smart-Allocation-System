package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents the official assignment record of a student to a room and specific bed.
 */
public class Allocation {
    private String allocationId;
    private String studentId;
    private String roomNumber;
    private int bedNumber;
    private String allocationDate;

    public Allocation(String allocationId, String studentId, String roomNumber, int bedNumber) {
        this.allocationId = allocationId;
        this.studentId = studentId;
        this.roomNumber = roomNumber;
        this.bedNumber = bedNumber;
        this.allocationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Allocation(String allocationId, String studentId, String roomNumber, int bedNumber, String allocationDate) {
        this.allocationId = allocationId;
        this.studentId = studentId;
        this.roomNumber = roomNumber;
        this.bedNumber = bedNumber;
        this.allocationDate = allocationDate;
    }

    public String getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(String allocationId) {
        this.allocationId = allocationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(int bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(String allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String toFileString() {
        return allocationId + "|" + studentId + "|" + roomNumber + "|" + bedNumber + "|" + allocationDate;
    }

    public static Allocation fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 5) {
            int bed = Integer.parseInt(parts[3]);
            return new Allocation(parts[0], parts[1], parts[2], bed, parts[4]);
        }
        return null;
    }
}
