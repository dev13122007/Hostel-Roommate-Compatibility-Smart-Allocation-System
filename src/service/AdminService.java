package service;

import exception.InvalidLoginException;
import exception.RoomNotFoundException;
import exception.StudentNotFoundException;
import model.Admin;
import model.Allocation;
import model.CompatibilityResult;
import model.Preference;
import model.Room;
import model.RoommateRequest;
import model.Student;

import java.util.List;

/**
 * Service managing administrative tasks, system diagnostics, and statistical analytics.
 */
public class AdminService {
    private Admin defaultAdmin;
    private StudentService studentService;
    private PreferenceService preferenceService;
    private RoomAllocationService allocationService;
    private RoommateRequestService requestService;
    private CompatibilityService compatibilityService;

    public AdminService(StudentService studentService, PreferenceService preferenceService,
                        RoomAllocationService allocationService, RoommateRequestService requestService,
                        CompatibilityService compatibilityService) {
        this.studentService = studentService;
        this.preferenceService = preferenceService;
        this.allocationService = allocationService;
        this.requestService = requestService;
        this.compatibilityService = compatibilityService;
        // Evaluation admin credential
        this.defaultAdmin = new Admin("ADM-101", "Chief Warden", "admin@hostel.edu", "admin123", "Hostel Affairs");
    }

    public Admin login(String emailOrId, String password) throws InvalidLoginException {
        if ((defaultAdmin.getEmail().equalsIgnoreCase(emailOrId.trim()) ||
             defaultAdmin.getId().equalsIgnoreCase(emailOrId.trim()) ||
             "admin".equalsIgnoreCase(emailOrId.trim())) &&
            defaultAdmin.verifyPassword(password)) {
            return defaultAdmin;
        }
        throw new InvalidLoginException("Invalid Administrator credentials.");
    }

    public void displayAllStudents() {
        List<Student> students = studentService.getAllStudents();
        System.out.println("=========================================================================================================");
        System.out.println("                                      HOSTEL REGISTERED STUDENTS                                         ");
        System.out.println("=========================================================================================================");
        System.out.printf("%-8s | %-18s | %-24s | %-16s | %-8s | %-12s | %-10s%n",
                "ID", "Name", "Email", "Course", "Year", "Hostel", "Status");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Student s : students) {
            String status = s.isAllocated() ? "Room " + s.getAllocatedRoomNo() : "Unassigned";
            System.out.printf("%-8s | %-18s | %-24s | %-16s | %-8s | %-12s | %-10s%n",
                    s.getStudentId(), s.getName(), s.getEmail(), s.getCourse(), s.getYear(), s.getHostel(), status);
        }
        System.out.println("=========================================================================================================");
        System.out.println("Total Students Registered: " + students.size());
    }

    public void displayStudentDetails(String studentId) throws StudentNotFoundException {
        Student s = studentService.getStudentById(studentId);
        s.displayDetails();
        Preference p = preferenceService.getPreference(s.getStudentId());
        if (p != null) {
            p.displayPreferences();
        } else {
            System.out.println("  [Preferences have not yet been configured by this student.]");
        }
    }

    public void displayAllRooms() {
        List<Room> rooms = allocationService.getAllRooms();
        System.out.println("=========================================================================================================");
        System.out.println("                                           HOSTEL ROOM STATUS                                            ");
        System.out.println("=========================================================================================================");
        System.out.printf("%-10s | %-6s | %-6s | %-9s | %-9s | %-11s | %-16s | %-20s%n",
                "Room No", "Block", "Floor", "Capacity", "Occupied", "Avail Beds", "Status", "Occupants");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Room r : rooms) {
            String occStr = r.getOccupants().isEmpty() ? "Empty" : String.join(", ", r.getOccupants());
            System.out.printf("%-10s | %-6s | %-6s | %-9d | %-9d | %-11d | %-16s | %-20s%n",
                    r.getRoomNumber(), r.getBlock(), "F-" + r.getFloor(), r.getCapacity(),
                    r.getOccupiedCount(), r.getAvailableBeds(), r.getStatus(), occStr);
        }
        System.out.println("=========================================================================================================");
        System.out.println("Total Rooms: " + rooms.size() + " | Total Available Beds: " + allocationService.getAvailableBedsCount());
    }

    public void addRoom(String roomNumber, String block, int floor, int capacity) throws IllegalArgumentException {
        allocationService.addRoom(roomNumber, block, floor, capacity);
        System.out.println("Room " + roomNumber.toUpperCase() + " successfully created.");
    }

    public void removeRoom(String roomNumber) throws RoomNotFoundException, IllegalArgumentException {
        allocationService.removeRoom(roomNumber);
        System.out.println("Room " + roomNumber.toUpperCase() + " successfully removed.");
    }

    public void displayAllAllocations() {
        List<Allocation> allocations = allocationService.getAllAllocations();
        System.out.println("=========================================================================================================");
        System.out.println("                                         ACTIVE ROOM ALLOCATIONS                                         ");
        System.out.println("=========================================================================================================");
        System.out.printf("%-12s | %-10s | %-20s | %-10s | %-8s | %-20s%n",
                "Alloc ID", "Student ID", "Student Name", "Room No", "Bed No", "Allocated Date");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        if (allocations.isEmpty()) {
            System.out.println("  No room allocations currently recorded.");
        } else {
            for (Allocation a : allocations) {
                String name = "Unknown";
                try {
                    Student s = studentService.getStudentById(a.getStudentId());
                    name = s.getName();
                } catch (StudentNotFoundException ignored) {}
                System.out.printf("%-12s | %-10s | %-20s | %-10s | %-8d | %-20s%n",
                        a.getAllocationId(), a.getStudentId(), name, a.getRoomNumber(), a.getBedNumber(), a.getAllocationDate());
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Total Active Allocations: " + allocations.size());
    }

    public void displayAllRequests() {
        List<RoommateRequest> requests = requestService.getAllRequests();
        System.out.println("=========================================================================================================");
        System.out.println("                                         PEER ROOMMATE REQUESTS                                          ");
        System.out.println("=========================================================================================================");
        System.out.printf("%-12s | %-22s | %-22s | %-12s | %-20s%n",
                "Request ID", "Sender", "Receiver", "Status", "Timestamp");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        if (requests.isEmpty()) {
            System.out.println("  No roommate requests on record.");
        } else {
            for (RoommateRequest r : requests) {
                String senderInfo = r.getSenderId();
                String receiverInfo = r.getReceiverId();
                try {
                    senderInfo += " (" + studentService.getStudentById(r.getSenderId()).getName() + ")";
                    receiverInfo += " (" + studentService.getStudentById(r.getReceiverId()).getName() + ")";
                } catch (StudentNotFoundException ignored) {}

                System.out.printf("%-12s | %-22s | %-22s | %-12s | %-20s%n",
                        r.getRequestId(), senderInfo, receiverInfo, r.getStatus(), r.getTimestamp());
            }
        }
        System.out.println("=========================================================================================================");
        System.out.println("Total Requests: " + requests.size() + " | Pending: " + requestService.getPendingRequestCount());
    }

    public void displaySystemStatistics() {
        List<Student> students = studentService.getAllStudents();
        int totalStudents = students.size();
        int totalRooms = allocationService.getTotalRoomsCount();
        int allocatedCount = allocationService.getAllocatedStudentsCount();
        int availableBeds = allocationService.getAvailableBedsCount();
        int pendingReqs = requestService.getPendingRequestCount();

        // Calculate dynamic compatibility distribution across all student pairs
        int pairCount = 0;
        double sumScores = 0;
        int excellent = 0;
        int veryGood = 0;
        int good = 0;
        int average = 0;
        int low = 0;

        for (int i = 0; i < students.size(); i++) {
            Student s1 = students.get(i);
            if (s1.getPreference() == null) continue;
            for (int j = i + 1; j < students.size(); j++) {
                Student s2 = students.get(j);
                if (s2.getPreference() == null) continue;

                CompatibilityResult res = compatibilityService.calculate(s1, s2);
                pairCount++;
                sumScores += res.getTotalScore();
                double score = res.getTotalScore();

                if (score >= 90.0) excellent++;
                else if (score >= 80.0) veryGood++;
                else if (score >= 70.0) good++;
                else if (score >= 60.0) average++;
                else low++;
            }
        }

        double avgScore = pairCount > 0 ? (sumScores / pairCount) : 0.0;

        System.out.println("========================================");
        System.out.println("          SYSTEM STATISTICS             ");
        System.out.println("========================================");
        System.out.printf("  %-25s : %d%n", "Total Students", totalStudents);
        System.out.printf("  %-25s : %d%n", "Total Rooms", totalRooms);
        System.out.printf("  %-25s : %d%n", "Allocated Students", allocatedCount);
        System.out.printf("  %-25s : %d%n", "Unallocated Students", (totalStudents - allocatedCount));
        System.out.printf("  %-25s : %d%n", "Available Beds", availableBeds);
        System.out.printf("  %-25s : %d%n", "Pending Requests", pendingReqs);
        System.out.println("----------------------------------------");
        System.out.printf("  %-25s : %d pairs%n", "Evaluated Student Pairs", pairCount);
        System.out.printf("  %-25s : %.1f%%%n", "Average Compatibility", avgScore);
        System.out.println("----------------------------------------");
        System.out.println("  COMPATIBILITY DISTRIBUTION:");
        System.out.printf("  %-25s : %d%n", "Excellent Matches (90-100%)", excellent);
        System.out.printf("  %-25s : %d%n", "Very Good Matches (80-89%)", veryGood);
        System.out.printf("  %-25s : %d%n", "Good Matches (70-79%)", good);
        System.out.printf("  %-25s : %d%n", "Average Matches (60-69%)", average);
        System.out.printf("  %-25s : %d%n", "Low Matches (<60%)", low);
        System.out.println("========================================");
    }
}
