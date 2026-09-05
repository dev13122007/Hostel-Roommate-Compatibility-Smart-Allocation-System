import exception.*;
import model.*;
import service.*;
import thread.ConcurrentAllocationDemo;
import util.DataInitializer;
import util.InputValidator;

import java.util.*;

/**
 * Main application entry point providing the interactive command-line interface (CLI)
 * for Students and Administrators.
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);

    private static PreferenceService preferenceService;
    private static StudentService studentService;
    private static CompatibilityService compatibilityService;
    private static RoommateRequestService requestService;
    private static RoomAllocationService allocationService;
    private static AdminService adminService;

    public static void main(String[] args) {
        // Initialize persistent baseline data files if empty
        DataInitializer.initializeIfEmpty();

        // Instantiate core service layer
        preferenceService = new PreferenceService();
        studentService = new StudentService(preferenceService);
        compatibilityService = new CompatibilityService();
        requestService = new RoommateRequestService(studentService);
        allocationService = new RoomAllocationService(studentService, preferenceService, compatibilityService, requestService);
        adminService = new AdminService(studentService, preferenceService, allocationService, requestService, compatibilityService);

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = readIntChoice("Enter choice: ");
            switch (choice) {
                case 1:
                    handleStudentLogin();
                    break;
                case 2:
                    handleStudentRegistration();
                    break;
                case 3:
                    handleAdminLogin();
                    break;
                case 4:
                    ConcurrentAllocationDemo.runDemo(studentService, allocationService);
                    break;
                case 5:
                    System.out.println("\nThank you for using the Hostel Roommate Compatibility System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select an option between 1 and 5.");
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n========================================");
        System.out.println(" HOSTEL ROOMMATE COMPATIBILITY SYSTEM   ");
        System.out.println("========================================");
        System.out.println("1. Student Login");
        System.out.println("2. Student Registration");
        System.out.println("3. Admin Login");
        System.out.println("4. Run Concurrency Allocation Demo");
        System.out.println("5. Exit");
        System.out.println("========================================");
    }

    // ==========================================
    // STUDENT AUTH & REGISTRATION
    // ==========================================

    private static void handleStudentLogin() {
        System.out.println("\n--- Student Login ---");
        System.out.print("Enter Student ID or Email: ");
        String idOrEmail = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            Student loggedInStudent = studentService.login(idOrEmail, password);
            System.out.println("\nLogin successful! Welcome, " + loggedInStudent.getName() + " (" + loggedInStudent.getStudentId() + ").");
            runStudentDashboard(loggedInStudent);
        } catch (InvalidLoginException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during login: " + e.getMessage());
        }
    }

    private static void handleStudentRegistration() {
        System.out.println("\n--- Student Registration ---");
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Institutional Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password (min 4 characters): ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter Academic Course (e.g. B.Tech CSE / AI & ML): ");
        String course = scanner.nextLine().trim();
        System.out.print("Enter Academic Year (e.g. Year 1 / Year 2): ");
        String year = scanner.nextLine().trim();
        System.out.print("Enter Hostel Block (e.g. Block A / Block B / Block C): ");
        String hostel = scanner.nextLine().trim();

        try {
            Student newStudent = studentService.registerStudent(name, email, password, course, year, hostel);
            System.out.println("\nRegistration Successful!");
            System.out.println("Your assigned Student ID is: " + newStudent.getStudentId());
            System.out.println("Please configure your lifestyle preferences to unlock roommate matching.");

            System.out.print("\nWould you like to set your lifestyle preferences now? (yes/no): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                handleUpdatePreferences(newStudent);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Registration Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    // ==========================================
    // STUDENT DASHBOARD
    // ==========================================

    private static void runStudentDashboard(Student student) {
        boolean inSession = true;
        while (inSession) {
            System.out.println("\n========================================");
            System.out.println("          STUDENT DASHBOARD             ");
            System.out.println("========================================");
            System.out.println("Logged in as: " + student.getName() + " [" + student.getStudentId() + "]");
            System.out.println("----------------------------------------");
            System.out.println("1.  View Profile");
            System.out.println("2.  Update Profile");
            System.out.println("3.  View Preferences");
            System.out.println("4.  Update Preferences");
            System.out.println("5.  Find Best Roommate Matches");
            System.out.println("6.  View Compatibility Explanation");
            System.out.println("7.  Send Roommate Request");
            System.out.println("8.  View Sent Requests");
            System.out.println("9.  View Received Requests");
            System.out.println("10. Accept Request");
            System.out.println("11. Reject Request");
            System.out.println("12. View Room Allocation");
            System.out.println("13. Logout");
            System.out.println("========================================");

            int choice = readIntChoice("Enter choice: ");
            switch (choice) {
                case 1:
                    student.displayDetails();
                    break;
                case 2:
                    handleUpdateProfile(student);
                    break;
                case 3:
                    handleViewPreferences(student);
                    break;
                case 4:
                    handleUpdatePreferences(student);
                    break;
                case 5:
                    handleFindBestMatches(student);
                    break;
                case 6:
                    handleCompatibilityExplanation(student);
                    break;
                case 7:
                    handleSendRequest(student);
                    break;
                case 8:
                    handleViewSentRequests(student);
                    break;
                case 9:
                    handleViewReceivedRequests(student);
                    break;
                case 10:
                    handleAcceptRequest(student);
                    break;
                case 11:
                    handleRejectRequest(student);
                    break;
                case 12:
                    handleViewRoomAllocation(student);
                    break;
                case 13:
                    System.out.println("Logged out successfully.");
                    inSession = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select an option between 1 and 13.");
            }
        }
    }

    private static void handleUpdateProfile(Student student) {
        System.out.println("\n--- Update Profile --- (Leave blank to keep existing value)");
        System.out.print("Name [" + student.getName() + "]: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email [" + student.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        System.out.print("Course [" + student.getCourse() + "]: ");
        String course = scanner.nextLine().trim();
        System.out.print("Year [" + student.getYear() + "]: ");
        String year = scanner.nextLine().trim();
        System.out.print("Hostel [" + student.getHostel() + "]: ");
        String hostel = scanner.nextLine().trim();

        try {
            studentService.updateStudentProfile(student.getStudentId(), name, email, course, year, hostel);
            System.out.println("Profile updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    private static void handleViewPreferences(Student student) {
        Preference p = preferenceService.getPreference(student.getStudentId());
        if (p == null) {
            System.out.println("\nYou have not set your preferences yet. Please select option 4 to configure them.");
        } else {
            p.displayPreferences();
        }
    }

    private static void handleUpdatePreferences(Student student) {
        System.out.println("\n========================================");
        System.out.println("    CONFIGURE LIFESTYLE PREFERENCES     ");
        System.out.println("========================================");

        String sleep = promptChoice("1. Sleep Schedule", InputValidator.SLEEP_SCHEDULES);
        String study = promptChoice("2. Study Habit", InputValidator.STUDY_HABITS);
        String clean = promptChoice("3. Cleanliness Standard", InputValidator.CLEANLINESS_LEVELS);
        String noise = promptChoice("4. Noise Tolerance", InputValidator.NOISE_PREFERENCES);
        String social = promptChoice("5. Social Style", InputValidator.SOCIAL_PREFERENCES);
        String food = promptChoice("6. Food Preference", InputValidator.FOOD_PREFERENCES);
        String temp = promptChoice("7. Room Temperature", InputValidator.ROOM_TEMPERATURES);
        String guest = promptChoice("8. Guest Frequency", InputValidator.GUEST_FREQUENCIES);
        String env = promptChoice("9. Study Environment", InputValidator.STUDY_ENVIRONMENTS);
        String weekend = promptChoice("10. Weekend Routine", InputValidator.WEEKEND_ROUTINES);

        Preference newPref = new Preference(student.getStudentId(), sleep, study, clean, noise, social, food, temp, guest, env, weekend);

        try {
            preferenceService.saveOrUpdatePreference(newPref);
            student.setPreference(newPref);
            System.out.println("\nPreferences saved successfully! Your roommate compatibility model is updated.");
        } catch (InvalidPreferenceException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    private static String promptChoice(String categoryLabel, List<String> options) {
        System.out.println("\n" + categoryLabel + ":");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + options.get(i));
        }
        while (true) {
            System.out.print("Select choice (1-" + options.size() + "): ");
            String input = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 1 && idx <= options.size()) {
                    return options.get(idx - 1);
                }
            } catch (NumberFormatException ignored) {}
            // Also allow typing the name directly
            for (String opt : options) {
                if (opt.equalsIgnoreCase(input)) return opt;
            }
            System.out.println("Invalid selection. Please enter a valid number or option name.");
        }
    }

    private static void handleFindBestMatches(Student student) {
        Preference myPref = preferenceService.getPreference(student.getStudentId());
        if (myPref == null) {
            System.out.println("\nYou must configure your preferences first (Option 4) before finding matches.");
            return;
        }

        List<Student> allStudents = studentService.getAllStudents();
        List<CompatibilityResult> results = new ArrayList<>();

        for (Student other : allStudents) {
            // Exclude self and students without preferences
            if (other.getStudentId().equals(student.getStudentId())) continue;
            if (preferenceService.getPreference(other.getStudentId()) == null) continue;

            CompatibilityResult res = compatibilityService.calculate(student, other);
            results.add(res);
        }

        if (results.isEmpty()) {
            System.out.println("\nNo other students with completed preferences are currently available for matching.");
            return;
        }

        // Sort descending by total score using Comparator
        results.sort(new Comparator<CompatibilityResult>() {
            @Override
            public int compare(CompatibilityResult r1, CompatibilityResult r2) {
                return Double.compare(r2.getTotalScore(), r1.getTotalScore());
            }
        });

        System.out.println("\n==========================================================================");
        System.out.println("                         BEST ROOMMATE MATCHES                            ");
        System.out.println("==========================================================================");
        int topLimit = Math.min(5, results.size());
        for (int i = 0; i < topLimit; i++) {
            CompatibilityResult res = results.get(i);
            Student candidate = res.getStudentB();
            String allocInfo = candidate.isAllocated() ? "[Allocated: Room " + candidate.getAllocatedRoomNo() + "]" : "[Unallocated]";
            System.out.printf("%d. %-18s (%s) - %s | %s%n",
                    (i + 1), candidate.getName(), candidate.getStudentId(), candidate.getCourse(), allocInfo);
            System.out.printf("   Compatibility : %.1f%% | Level: %s%n", res.getTotalScore(), res.getCompatibilityLevel());
            System.out.printf("   Key Strength  : %s%n",
                    res.getPositiveReasons().isEmpty() ? "General compatibility" : res.getPositiveReasons().get(0));
            System.out.println("--------------------------------------------------------------------------");
        }
        System.out.println("Tip: Use Option 6 to view full compatibility breakdown with any student ID.");
    }

    private static void handleCompatibilityExplanation(Student student) {
        Preference myPref = preferenceService.getPreference(student.getStudentId());
        if (myPref == null) {
            System.out.println("\nPlease configure your preferences first (Option 4).");
            return;
        }

        System.out.print("\nEnter Student ID to compare with (e.g. ST102, ST104): ");
        String targetId = scanner.nextLine().trim();

        try {
            Student target = studentService.getStudentById(targetId);
            if (target.getStudentId().equals(student.getStudentId())) {
                System.out.println("Cannot calculate compatibility with yourself.");
                return;
            }
            if (preferenceService.getPreference(target.getStudentId()) == null) {
                System.out.println("Student " + target.getName() + " has not configured their preferences yet.");
                return;
            }

            CompatibilityResult result = compatibilityService.calculate(student, target);
            result.displayDetailedBreakdown();

        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleSendRequest(Student student) {
        if (student.isAllocated()) {
            System.out.println("\nYou are already allocated to Room " + student.getAllocatedRoomNo() + ". You cannot send roommate requests.");
            return;
        }

        System.out.print("\nEnter Student ID to send request to (e.g. ST106): ");
        String receiverId = scanner.nextLine().trim();

        try {
            RoommateRequest req = requestService.sendRequest(student.getStudentId(), receiverId);
            Student receiver = studentService.getStudentById(receiverId);
            System.out.println("\nRoommate request (" + req.getRequestId() + ") successfully sent to " + receiver.getName() + "!");
        } catch (StudentNotFoundException | AlreadyAllocatedException | DuplicateRequestException | IllegalArgumentException e) {
            System.out.println("Request Failed: " + e.getMessage());
        }
    }

    private static void handleViewSentRequests(Student student) {
        List<RoommateRequest> sent = requestService.getSentRequests(student.getStudentId());
        System.out.println("\n==========================================================================");
        System.out.println("                            SENT ROOMMATE REQUESTS                        ");
        System.out.println("==========================================================================");
        if (sent.isEmpty()) {
            System.out.println("  You have not sent any roommate requests.");
        } else {
            System.out.printf("%-12s | %-24s | %-12s | %-20s%n", "Request ID", "Sent To", "Status", "Sent Date");
            System.out.println("--------------------------------------------------------------------------");
            for (RoommateRequest r : sent) {
                String toName = r.getReceiverId();
                try {
                    toName += " (" + studentService.getStudentById(r.getReceiverId()).getName() + ")";
                } catch (StudentNotFoundException ignored) {}
                System.out.printf("%-12s | %-24s | %-12s | %-20s%n", r.getRequestId(), toName, r.getStatus(), r.getTimestamp());
            }
        }
        System.out.println("==========================================================================");
    }

    private static void handleViewReceivedRequests(Student student) {
        List<RoommateRequest> received = requestService.getReceivedRequests(student.getStudentId());
        System.out.println("\n==========================================================================");
        System.out.println("                         RECEIVED ROOMMATE REQUESTS                       ");
        System.out.println("==========================================================================");
        if (received.isEmpty()) {
            System.out.println("  You have no received roommate requests.");
        } else {
            System.out.printf("%-12s | %-24s | %-12s | %-20s%n", "Request ID", "From", "Status", "Received Date");
            System.out.println("--------------------------------------------------------------------------");
            for (RoommateRequest r : received) {
                String fromName = r.getSenderId();
                try {
                    fromName += " (" + studentService.getStudentById(r.getSenderId()).getName() + ")";
                } catch (StudentNotFoundException ignored) {}
                System.out.printf("%-12s | %-24s | %-12s | %-20s%n", r.getRequestId(), fromName, r.getStatus(), r.getTimestamp());
            }
        }
        System.out.println("==========================================================================");
    }

    private static void handleAcceptRequest(Student student) {
        System.out.print("\nEnter Request ID to accept (e.g. REQ-1001): ");
        String reqId = scanner.nextLine().trim();

        try {
            RoommateRequest req = requestService.acceptRequest(reqId, student.getStudentId());
            Student sender = studentService.getStudentById(req.getSenderId());
            System.out.println("\nRequest " + req.getRequestId() + " ACCEPTED!");
            System.out.println("You and " + sender.getName() + " are now paired as preferred roommates.");
            System.out.println("When you proceed to Room Allocation (Option 12), the system will prioritize assigning you together.");
        } catch (RequestNotFoundException | AlreadyAllocatedException | IllegalArgumentException | StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleRejectRequest(Student student) {
        System.out.print("\nEnter Request ID to reject: ");
        String reqId = scanner.nextLine().trim();

        try {
            RoommateRequest req = requestService.rejectRequest(reqId, student.getStudentId());
            System.out.println("Request " + req.getRequestId() + " was rejected.");
        } catch (RequestNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleViewRoomAllocation(Student student) {
        System.out.println("\n========================================");
        System.out.println("         ROOM ALLOCATION STATUS         ");
        System.out.println("========================================");

        if (student.isAllocated()) {
            System.out.println("Status          : ALLOCATED");
            System.out.println("Assigned Room   : " + student.getAllocatedRoomNo());
            System.out.println("Assigned Bed    : Bed " + student.getAllocatedBedNo());

            try {
                Room r = allocationService.getRoomByNumber(student.getAllocatedRoomNo());
                System.out.println("Block & Floor   : Block " + r.getBlock() + ", Floor " + r.getFloor());
                System.out.println("Room Capacity   : " + r.getCapacity() + " Students");
                System.out.println("\nCurrent Occupants in Room " + r.getRoomNumber() + ":");
                int bed = 1;
                for (String occId : r.getOccupants()) {
                    String info = occId;
                    try {
                        Student occ = studentService.getStudentById(occId);
                        info = occ.getName() + " (" + occ.getCourse() + ")";
                        if (occId.equals(student.getStudentId())) {
                            info += " [YOU]";
                        }
                    } catch (StudentNotFoundException ignored) {}
                    System.out.println("  Bed " + bed++ + ": " + info);
                }
            } catch (RoomNotFoundException e) {
                System.out.println("Room details: " + e.getMessage());
            }
            System.out.println("========================================");
        } else {
            System.out.println("Status          : NOT ALLOCATED");
            System.out.println("Available Beds  : " + allocationService.getAvailableBedsCount() + " beds in hostel.");
            System.out.println("----------------------------------------");
            System.out.print("Would you like to trigger Smart Room Allocation now? (yes/no): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                try {
                    Allocation alloc = allocationService.smartAllocateStudent(student.getStudentId());
                    System.out.println("\nRoom allocation successful!");
                    System.out.println("Student       : " + student.getName());
                    System.out.println("Assigned Room : " + alloc.getRoomNumber());
                    System.out.println("Assigned Bed  : Bed " + alloc.getBedNumber());
                    System.out.println("Allocation ID : " + alloc.getAllocationId());

                    Room r = allocationService.getRoomByNumber(alloc.getRoomNumber());
                    System.out.println("\nCurrent Occupants:");
                    int bedIdx = 1;
                    for (String occId : r.getOccupants()) {
                        String name = occId;
                        try {
                            name = studentService.getStudentById(occId).getName();
                        } catch (StudentNotFoundException ignored) {}
                        System.out.println("  " + bedIdx++ + ". " + name + (occId.equals(student.getStudentId()) ? " (You)" : ""));
                    }
                } catch (StudentNotFoundException | AlreadyAllocatedException | RoomFullException | RoomNotFoundException e) {
                    System.out.println("Allocation Failed: " + e.getMessage());
                }
            }
        }
    }

    // ==========================================
    // ADMIN DASHBOARD
    // ==========================================

    private static void handleAdminLogin() {
        System.out.println("\n--- Administrator Login ---");
        System.out.print("Enter Admin ID or Email: ");
        String idOrEmail = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        try {
            Admin admin = adminService.login(idOrEmail, password);
            System.out.println("\nAuthentication Successful! Welcome, " + admin.getName() + ".");
            runAdminDashboard(admin);
        } catch (InvalidLoginException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void runAdminDashboard(Admin admin) {
        boolean inSession = true;
        while (inSession) {
            System.out.println("\n========================================");
            System.out.println("            ADMIN DASHBOARD             ");
            System.out.println("========================================");
            System.out.println("1. View All Students");
            System.out.println("2. View Student Details");
            System.out.println("3. View All Rooms");
            System.out.println("4. Add Room");
            System.out.println("5. Remove Room");
            System.out.println("6. View Allocations");
            System.out.println("7. View Roommate Requests");
            System.out.println("8. View System Statistics");
            System.out.println("9. Logout");
            System.out.println("========================================");

            int choice = readIntChoice("Enter choice: ");
            switch (choice) {
                case 1:
                    adminService.displayAllStudents();
                    break;
                case 2:
                    System.out.print("\nEnter Student ID to inspect: ");
                    String sId = scanner.nextLine().trim();
                    try {
                        adminService.displayStudentDetails(sId);
                    } catch (StudentNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3:
                    adminService.displayAllRooms();
                    break;
                case 4:
                    handleAddRoom();
                    break;
                case 5:
                    handleRemoveRoom();
                    break;
                case 6:
                    adminService.displayAllAllocations();
                    break;
                case 7:
                    adminService.displayAllRequests();
                    break;
                case 8:
                    adminService.displaySystemStatistics();
                    break;
                case 9:
                    System.out.println("Administrator session ended.");
                    inSession = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select an option between 1 and 9.");
            }
        }
    }

    private static void handleAddRoom() {
        System.out.println("\n--- Add New Hostel Room ---");
        System.out.print("Enter Room Number (e.g. D-101, B-301): ");
        String roomNum = scanner.nextLine().trim();
        System.out.print("Enter Block (e.g. A, B, C, D): ");
        String block = scanner.nextLine().trim();
        int floor = readIntChoice("Enter Floor Number (1-10): ");
        int capacity = readIntChoice("Enter Room Bed Capacity (1-4): ");

        try {
            adminService.addRoom(roomNum, block, floor, capacity);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to Add Room: " + e.getMessage());
        }
    }

    private static void handleRemoveRoom() {
        System.out.println("\n--- Remove Empty Hostel Room ---");
        System.out.print("Enter Room Number to remove: ");
        String roomNum = scanner.nextLine().trim();

        try {
            adminService.removeRoom(roomNum);
        } catch (RoomNotFoundException | IllegalArgumentException e) {
            System.out.println("Failed to Remove Room: " + e.getMessage());
        }
    }

    // ==========================================
    // HELPER INPUT METHODS
    // ==========================================

    private static int readIntChoice(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numerical choice.");
            }
        }
    }
}
