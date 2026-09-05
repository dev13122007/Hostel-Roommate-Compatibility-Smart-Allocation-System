package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a roommate pairing invitation between two students.
 */
public class RoommateRequest {
    private String requestId;
    private String senderId;
    private String receiverId;
    private String status; // "PENDING", "ACCEPTED", "REJECTED", "CANCELLED"
    private String timestamp;

    public RoommateRequest(String requestId, String senderId, String receiverId) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = "PENDING";
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public RoommateRequest(String requestId, String senderId, String receiverId, String status, String timestamp) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    public String toFileString() {
        return requestId + "|" + senderId + "|" + receiverId + "|" + status + "|" + timestamp;
    }

    public static RoommateRequest fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 5) {
            return new RoommateRequest(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
}
