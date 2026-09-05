package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hostel room containing a maximum capacity and currently assigned occupants.
 */
public class Room {
    private String roomNumber;
    private String block;
    private int floor;
    private int capacity;
    private List<String> occupants; // Student IDs
    private String status; // "AVAILABLE", "PARTIALLY_FILLED", "FULL"

    public Room(String roomNumber, String block, int floor, int capacity) {
        this.roomNumber = roomNumber;
        this.block = block;
        this.floor = floor;
        this.capacity = capacity;
        this.occupants = new ArrayList<>();
        updateStatus();
    }

    public Room(String roomNumber, String block, int floor, int capacity, List<String> occupants) {
        this.roomNumber = roomNumber;
        this.block = block;
        this.floor = floor;
        this.capacity = capacity;
        this.occupants = occupants != null ? occupants : new ArrayList<>();
        updateStatus();
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        updateStatus();
    }

    public List<String> getOccupants() {
        return occupants;
    }

    public void setOccupants(List<String> occupants) {
        this.occupants = occupants != null ? occupants : new ArrayList<>();
        updateStatus();
    }

    public String getStatus() {
        return status;
    }

    public int getOccupiedCount() {
        return occupants.size();
    }

    public int getAvailableBeds() {
        return capacity - occupants.size();
    }

    public boolean isAvailable() {
        return occupants.size() < capacity;
    }

    public void updateStatus() {
        if (occupants.isEmpty()) {
            this.status = "AVAILABLE";
        } else if (occupants.size() < capacity) {
            this.status = "PARTIALLY_FILLED";
        } else {
            this.status = "FULL";
        }
    }

    /**
     * Adds an occupant to the room if space is available.
     * Returns the assigned bed number (1-based index).
     */
    public synchronized int addOccupant(String studentId) {
        if (occupants.size() < capacity && !occupants.contains(studentId)) {
            occupants.add(studentId);
            updateStatus();
            return occupants.size(); // Bed 1 or Bed 2, etc.
        }
        return -1; // Allocation failed
    }

    /**
     * Removes an occupant from the room.
     */
    public synchronized boolean removeOccupant(String studentId) {
        boolean removed = occupants.remove(studentId);
        if (removed) {
            updateStatus();
        }
        return removed;
    }

    public void displayRoomDetails() {
        System.out.printf("  Room %-7s | Block: %-2s | Floor: %-2d | Cap: %d | Occ: %d | Avail: %d | Status: %-16s%n",
                roomNumber, block, floor, capacity, occupants.size(), getAvailableBeds(), status);
    }

    /**
     * Serializes Room to text line: roomNumber|block|floor|capacity|occupant1,occupant2
     */
    public String toFileString() {
        String occStr = occupants.isEmpty() ? "NONE" : String.join(",", occupants);
        return roomNumber + "|" + block + "|" + floor + "|" + capacity + "|" + occStr;
    }

    /**
     * Parses Room from text line.
     */
    public static Room fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 4) {
            String roomNum = parts[0];
            String block = parts[1];
            int floor = Integer.parseInt(parts[2]);
            int cap = Integer.parseInt(parts[3]);
            List<String> occs = new ArrayList<>();
            if (parts.length >= 5 && !parts[4].equalsIgnoreCase("NONE") && !parts[4].trim().isEmpty()) {
                String[] occArray = parts[4].split(",");
                for (String o : occArray) {
                    if (!o.trim().isEmpty()) {
                        occs.add(o.trim());
                    }
                }
            }
            return new Room(roomNum, block, floor, cap, occs);
        }
        return null;
    }
}
