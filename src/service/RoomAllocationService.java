package service;

import exception.AlreadyAllocatedException;
import exception.RoomFullException;
import exception.RoomNotFoundException;
import exception.StudentNotFoundException;
import model.Allocation;
import model.Room;
import model.RoommateRequest;
import model.Student;
import util.FileManager;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service handling room management, smart compatibility-aware room assignment,
 * and safe synchronized allocation preventing race conditions.
 */
public class RoomAllocationService {
    private static final String ROOMS_FILE = "rooms.txt";
    private static final String ALLOCATIONS_FILE = "allocations.txt";

    // Map of roomNumber -> Room object
    private Map<String, Room> roomMap;
    private List<Room> roomList;
    private List<Allocation> allocationList;

    private StudentService studentService;
    private PreferenceService preferenceService;
    private CompatibilityService compatibilityService;
    private RoommateRequestService requestService;

    public RoomAllocationService(StudentService studentService, PreferenceService preferenceService,
                                 CompatibilityService compatibilityService, RoommateRequestService requestService) {
        this.studentService = studentService;
        this.preferenceService = preferenceService;
        this.compatibilityService = compatibilityService;
        this.requestService = requestService;
        this.roomMap = new HashMap<>();
        this.roomList = new ArrayList<>();
        this.allocationList = new ArrayList<>();

        loadRooms();
        loadAllocations();
    }

    public void loadRooms() {
        roomMap.clear();
        roomList.clear();
        List<String> lines = FileManager.readLines(ROOMS_FILE);
        for (String line : lines) {
            Room r = Room.fromFileString(line);
            if (r != null) {
                roomMap.put(r.getRoomNumber().toUpperCase(), r);
                roomList.add(r);
            }
        }
    }

    public void loadAllocations() {
        allocationList.clear();
        List<String> lines = FileManager.readLines(ALLOCATIONS_FILE);
        int maxId = 1000;
        for (String line : lines) {
            Allocation a = Allocation.fromFileString(line);
            if (a != null) {
                allocationList.add(a);
                try {
                    String numStr = a.getAllocationId().replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        int num = Integer.parseInt(numStr);
                        if (num > maxId) maxId = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        IdGenerator.initCounters(100, 1000, maxId);
    }

    public void saveAllRooms() {
        List<String> lines = new ArrayList<>();
        for (Room r : roomList) {
            lines.add(r.toFileString());
        }
        FileManager.writeLines(ROOMS_FILE, lines);
    }

    public void saveAllAllocations() {
        List<String> lines = new ArrayList<>();
        for (Allocation a : allocationList) {
            lines.add(a.toFileString());
        }
        FileManager.writeLines(ALLOCATIONS_FILE, lines);
    }

    public Room getRoomByNumber(String roomNumber) throws RoomNotFoundException {
        if (roomNumber == null) {
            throw new RoomNotFoundException("Room number cannot be null.");
        }
        Room room = roomMap.get(roomNumber.trim().toUpperCase());
        if (room == null) {
            throw new RoomNotFoundException("Room '" + roomNumber + "' does not exist.");
        }
        return room;
    }

    public boolean roomExists(String roomNumber) {
        return roomNumber != null && roomMap.containsKey(roomNumber.trim().toUpperCase());
    }

    public void addRoom(String roomNumber, String block, int floor, int capacity) throws IllegalArgumentException {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty.");
        }
        String cleanRoom = roomNumber.trim().toUpperCase();
        if (roomMap.containsKey(cleanRoom)) {
            throw new IllegalArgumentException("Room " + cleanRoom + " already exists.");
        }
        if (capacity <= 0 || capacity > 6) {
            throw new IllegalArgumentException("Capacity must be between 1 and 6 beds.");
        }
        Room newRoom = new Room(cleanRoom, block.trim().toUpperCase(), floor, capacity);
        roomMap.put(cleanRoom, newRoom);
        roomList.add(newRoom);
        saveAllRooms();
    }

    public void removeRoom(String roomNumber) throws RoomNotFoundException, IllegalArgumentException {
        Room room = getRoomByNumber(roomNumber);
        if (room.getOccupiedCount() > 0) {
            throw new IllegalArgumentException("Cannot remove Room " + room.getRoomNumber() + " because it currently has active occupants.");
        }
        roomMap.remove(room.getRoomNumber().toUpperCase());
        roomList.remove(room);
        saveAllRooms();
    }

    /**
     * Smart Room Allocation:
     * 1. Check if student is already allocated.
     * 2. If student has an ACCEPTED roommate pair, prioritize placing them in the same room.
     * 3. Check partially filled rooms and evaluate compatibility with existing occupants.
     * 4. Otherwise, pick the best available empty room.
     */
    public synchronized Allocation smartAllocateStudent(String studentId)
            throws StudentNotFoundException, AlreadyAllocatedException, RoomFullException {

        Student student = studentService.getStudentById(studentId);
        if (student.isAllocated()) {
            throw new AlreadyAllocatedException("Student " + student.getName() + " is already allocated to Room " + student.getAllocatedRoomNo() + " (Bed " + student.getAllocatedBedNo() + ").");
        }

        // Check if student has an ACCEPTED roommate request
        String preferredPartnerId = findAcceptedRoommatePartner(studentId);
        if (preferredPartnerId != null) {
            try {
                Student partner = studentService.getStudentById(preferredPartnerId);
                // If partner is already allocated, attempt to place student in partner's room
                if (partner.isAllocated() && partner.getAllocatedRoomNo() != null && !partner.getAllocatedRoomNo().equals("None")) {
                    Room partnerRoom = roomMap.get(partner.getAllocatedRoomNo().toUpperCase());
                    if (partnerRoom != null && partnerRoom.isAvailable()) {
                        return executeDirectAllocation(student, partnerRoom);
                    }
                }
            } catch (StudentNotFoundException ignored) {}
        }

        // Search partially filled rooms to find highest compatibility match
        Room bestPartialRoom = null;
        double highestCompatibility = -1.0;

        for (Room r : roomList) {
            if (r.isAvailable() && r.getOccupiedCount() > 0) {
                // If student preferences exist, test compatibility with existing occupant
                if (student.getPreference() != null) {
                    for (String occId : r.getOccupants()) {
                        try {
                            Student occStudent = studentService.getStudentById(occId);
                            if (occStudent.getPreference() != null) {
                                double score = compatibilityService.calculate(student, occStudent).getTotalScore();
                                if (score > highestCompatibility) {
                                    highestCompatibility = score;
                                    bestPartialRoom = r;
                                }
                            }
                        } catch (StudentNotFoundException ignored) {}
                    }
                } else if (bestPartialRoom == null) {
                    bestPartialRoom = r;
                }
            }
        }

        // If a good partially filled room is found (score >= 60%), use it
        if (bestPartialRoom != null && (highestCompatibility >= 60.0 || student.getPreference() == null)) {
            return executeDirectAllocation(student, bestPartialRoom);
        }

        // Otherwise find first completely empty room
        for (Room r : roomList) {
            if (r.getOccupiedCount() == 0) {
                return executeDirectAllocation(student, r);
            }
        }

        // Fallback to any available room
        for (Room r : roomList) {
            if (r.isAvailable()) {
                return executeDirectAllocation(student, r);
            }
        }

        throw new RoomFullException("All hostel rooms are currently at full capacity. No beds available.");
    }

    /**
     * Executes thread-safe room allocation with synchronization on the target Room object.
     */
    public Allocation executeDirectAllocation(Student student, Room room) throws RoomFullException, AlreadyAllocatedException {
        if (student.isAllocated()) {
            throw new AlreadyAllocatedException("Student " + student.getName() + " is already allocated.");
        }

        // Synchronize on the target Room monitor to prevent concurrent overbooking
        synchronized (room) {
            if (!room.isAvailable()) {
                throw new RoomFullException("Room " + room.getRoomNumber() + " is full. Available beds: 0.");
            }

            int bedAssigned = room.addOccupant(student.getStudentId());
            if (bedAssigned == -1) {
                throw new RoomFullException("Failed to allocate bed in Room " + room.getRoomNumber() + ".");
            }

            // Update student entity
            student.setAllocated(true);
            student.setAllocatedRoomNo(room.getRoomNumber());
            student.setAllocatedBedNo(bedAssigned);

            // Create allocation record
            String allocId = IdGenerator.generateAllocationId();
            Allocation allocation = new Allocation(allocId, student.getStudentId(), room.getRoomNumber(), bedAssigned);
            allocationList.add(allocation);

            // Persist state updates
            saveAllRooms();
            saveAllAllocations();
            studentService.saveAllStudents();

            return allocation;
        }
    }

    private String findAcceptedRoommatePartner(String studentId) {
        if (requestService == null) return null;
        for (RoommateRequest req : requestService.getAllRequests()) {
            if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) {
                if (req.getSenderId().equalsIgnoreCase(studentId)) {
                    return req.getReceiverId();
                } else if (req.getReceiverId().equalsIgnoreCase(studentId)) {
                    return req.getSenderId();
                }
            }
        }
        return null;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(roomList);
    }

    public List<Allocation> getAllAllocations() {
        return new ArrayList<>(allocationList);
    }

    public int getTotalRoomsCount() {
        return roomList.size();
    }

    public int getAvailableBedsCount() {
        int count = 0;
        for (Room r : roomList) {
            count += r.getAvailableBeds();
        }
        return count;
    }

    public int getAllocatedStudentsCount() {
        int count = 0;
        for (Student s : studentService.getAllStudents()) {
            if (s.isAllocated()) count++;
        }
        return count;
    }
}
