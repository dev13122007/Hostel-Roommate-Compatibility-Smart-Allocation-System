package thread;

import exception.AlreadyAllocatedException;
import exception.RoomFullException;
import model.Allocation;
import model.Room;
import model.Student;
import service.RoomAllocationService;
import service.StudentService;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates thread synchronization to prevent race conditions and overbooking
 * when multiple threads simultaneously attempt to claim the final bed in a shared room.
 */
public class ConcurrentAllocationDemo {

    /**
     * Runnable task simulating a student attempting room allocation concurrently.
     */
    static class AllocationWorker implements Runnable {
        private String threadName;
        private Student student;
        private Room targetRoom;
        private RoomAllocationService allocationService;
        private List<String> resultsLog;

        public AllocationWorker(String threadName, Student student, Room targetRoom,
                                RoomAllocationService allocationService, List<String> resultsLog) {
            this.threadName = threadName;
            this.student = student;
            this.targetRoom = targetRoom;
            this.allocationService = allocationService;
            this.resultsLog = resultsLog;
        }

        @Override
        public void run() {
            System.out.println("[" + threadName + "] Started allocation attempt for " +
                    student.getName() + " -> Target Room: " + targetRoom.getRoomNumber() +
                    " (Beds available prior to lock: " + targetRoom.getAvailableBeds() + ")");

            try {
                // Simulate minor network/processing jitter
                Thread.sleep((long) (Math.random() * 50));

                /*
                 * SYNCHRONIZATION POINT:
                 * We synchronize on the target room instance inside executeDirectAllocation().
                 * Without synchronization, both threads could check 'room.isAvailable()' at the
                 * same instant, see 1 bed available, and both assign occupants - exceeding capacity.
                 */
                Allocation allocation = allocationService.executeDirectAllocation(student, targetRoom);

                String msg = " SUCCESS: [" + threadName + "] " + student.getName() +
                        " successfully booked Room " + targetRoom.getRoomNumber() +
                        " (Assigned Bed: " + allocation.getBedNumber() + ")";
                System.out.println(msg);
                synchronized (resultsLog) {
                    resultsLog.add(msg);
                }

            } catch (RoomFullException e) {
                String msg = " BLOCKED (Room Full): [" + threadName + "] " + student.getName() +
                        " could not be allocated. " + e.getMessage();
                System.out.println(msg);
                synchronized (resultsLog) {
                    resultsLog.add(msg);
                }
            } catch (AlreadyAllocatedException e) {
                String msg = " FAILED: [" + threadName + "] " + student.getName() + " already allocated.";
                System.out.println(msg);
                synchronized (resultsLog) {
                    resultsLog.add(msg);
                }
            } catch (InterruptedException e) {
                System.err.println("[" + threadName + "] Thread interrupted: " + e.getMessage());
            }
        }
    }

    /**
     * Runs the concurrent allocation simulation.
     */
    public static void runDemo(StudentService studentService, RoomAllocationService allocationService) {
        System.out.println("==========================================================================");
        System.out.println("       CONCURRENT ROOM ALLOCATION SIMULATION (THREAD SYNCHRONIZATION)     ");
        System.out.println("==========================================================================");
        System.out.println("Scenario:");
        System.out.println("  Two concurrent student threads simultaneously attempt to book the LAST");
        System.out.println("  remaining bed in Room B-203 (Capacity: 2, Current Occupants: 1).");
        System.out.println("  Without synchronization -> Race condition causes double-booking / overcapacity.");
        System.out.println("  With synchronization   -> Exactly 1 thread succeeds; 2nd thread is safely blocked.");
        System.out.println("--------------------------------------------------------------------------");

        try {
            // Find or configure room B-203 with 1 bed occupied, 1 bed free
            Room testRoom;
            try {
                testRoom = allocationService.getRoomByNumber("B-203");
            } catch (Exception e) {
                testRoom = new Room("B-203", "B", 2, 2);
            }

            // Reset test room occupancy for clean demo
            testRoom.getOccupants().clear();
            testRoom.addOccupant("ST101"); // Occupied by Aarav Sharma

            // Create 2 candidate students for concurrency test
            Student candidate1 = new Student("ST-DEMO1", "Rahul Mehta", "rahul.demo@campus.edu", "demo123",
                    "B.Tech CSE", "Year 2", "Block B", false, "None", 0);
            Student candidate2 = new Student("ST-DEMO2", "Karan Malhotra", "karan.demo@campus.edu", "demo123",
                    "B.Tech AI & ML", "Year 2", "Block B", false, "None", 0);

            List<String> resultsLog = new ArrayList<>();

            Thread t1 = new Thread(new AllocationWorker("Thread-Rahul", candidate1, testRoom, allocationService, resultsLog));
            Thread t2 = new Thread(new AllocationWorker("Thread-Karan", candidate2, testRoom, allocationService, resultsLog));

            System.out.println("Launching concurrent threads t1 and t2 simultaneously...\n");
            t1.start();
            t2.start();

            // Wait for both threads to finish
            t1.join();
            t2.join();

            System.out.println("\n--------------------------------------------------------------------------");
            System.out.println("SIMULATION SUMMARY & THREAD AUDIT:");
            for (String log : resultsLog) {
                System.out.println(log);
            }
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("Final State of Room B-203:");
            System.out.println("  Occupants count: " + testRoom.getOccupiedCount() + " / " + testRoom.getCapacity());
            System.out.println("  Occupant IDs   : " + testRoom.getOccupants());
            System.out.println("  Room Status    : " + testRoom.getStatus());
            System.out.println("  Available Beds : " + testRoom.getAvailableBeds());
            System.out.println("Integrity Check: " + (testRoom.getOccupiedCount() <= testRoom.getCapacity() ?
                    " PASSED (No Overbooking)" : " FAILED (Race Condition Detected)"));
            System.out.println("==========================================================================\n");

        } catch (Exception e) {
            System.err.println("Error during concurrency demo: " + e.getMessage());
        }
    }
}
