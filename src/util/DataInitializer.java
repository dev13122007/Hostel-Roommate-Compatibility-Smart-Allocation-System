package util;

import java.util.Arrays;
import java.util.List;

/**
 * Initializes baseline student records, preference profiles, rooms, allocations,
 * and requests ONLY when the storage files are empty on first startup.
 */
public class DataInitializer {

    public static void initializeIfEmpty() {
        initStudentsAndPreferences();
        initRooms();
        initAllocations();
        initRequests();
    }

    private static void initStudentsAndPreferences() {
        if (!FileManager.hasData("students.txt")) {
            List<String> studentLines = Arrays.asList(
                    "ST101|Aarav Sharma|aarav.sharma@campus.edu|pass123|B.Tech CSE|Year 2|Block A|true|A-101|1",
                    "ST102|Riya Verma|riya.verma@campus.edu|pass123|B.Tech CSE (AI & ML)|Year 2|Block A|true|A-101|2",
                    "ST103|Aditya Patel|aditya.patel@campus.edu|pass123|B.Tech ECE|Year 1|Block A|true|A-102|1",
                    "ST104|Ananya Singh|ananya.singh@campus.edu|pass123|B.Tech CSE|Year 2|Block B|false|None|0",
                    "ST105|Rahul Mehta|rahul.mehta@campus.edu|pass123|B.Tech CSE|Year 3|Block B|true|B-101|1",
                    "ST106|Sneha Gupta|sneha.gupta@campus.edu|pass123|B.Tech AI & ML|Year 2|Block A|false|None|0",
                    "ST107|Arjun Nair|arjun.nair@campus.edu|pass123|B.Tech Mechanical|Year 2|Block B|true|B-101|2",
                    "ST108|Priya Iyer|priya.iyer@campus.edu|pass123|B.Tech CSE|Year 1|Block C|false|None|0",
                    "ST109|Karan Malhotra|karan.malhotra@campus.edu|pass123|B.Tech ECE|Year 3|Block B|false|None|0",
                    "ST110|Neha Joshi|neha.joshi@campus.edu|pass123|B.Tech CSE (AI & ML)|Year 2|Block C|true|C-101|1",
                    "ST111|Yash Tiwari|yash.tiwari@campus.edu|pass123|B.Tech Civil|Year 1|Block A|false|None|0",
                    "ST112|Kavya Reddy|kavya.reddy@campus.edu|pass123|B.Tech CSE|Year 2|Block C|false|None|0",
                    "ST113|Rohan Deshmukh|rohan.deshmukh@campus.edu|pass123|B.Tech ECE|Year 2|Block B|false|None|0",
                    "ST114|Simran Kaur|simran.kaur@campus.edu|pass123|B.Tech AI & ML|Year 1|Block C|false|None|0",
                    "ST115|Abhishek Mishra|abhishek.mishra@campus.edu|pass123|B.Tech CSE|Year 3|Block A|false|None|0"
            );
            FileManager.writeLines("students.txt", studentLines);
        }

        if (!FileManager.hasData("preferences.txt")) {
            List<String> prefLines = Arrays.asList(
                    // studentId|sleep|study|clean|noise|social|food|temp|guest|env|weekend
                    "ST101|EARLY|HIGH|HIGH|QUIET|BALANCED|VEG|COOL|RARE|SILENT|STUDY",
                    "ST102|EARLY|HIGH|HIGH|QUIET|BALANCED|VEG|COOL|RARE|SILENT|STUDY",
                    "ST103|NORMAL|MEDIUM|HIGH|MODERATE|SOCIAL|ANY|MODERATE|OCCASIONAL|MUSIC|RELAX",
                    "ST104|LATE|HIGH|HIGH|QUIET|INTROVERTED|VEG|COOL|RARE|SILENT|STUDY",
                    "ST105|LATE|MEDIUM|MEDIUM|MODERATE|SOCIAL|NON_VEG|WARM|FREQUENT|GROUP|OUTINGS",
                    "ST106|LATE|HIGH|HIGH|QUIET|BALANCED|VEG|COOL|OCCASIONAL|SILENT|STUDY",
                    "ST107|LATE|MEDIUM|MEDIUM|MODERATE|SOCIAL|NON_VEG|WARM|FREQUENT|GROUP|OUTINGS",
                    "ST108|EARLY|HIGH|MEDIUM|QUIET|INTROVERTED|VEG|MODERATE|RARE|SILENT|RELAX",
                    "ST109|NORMAL|LOW|LOW|LOUD|SOCIAL|NON_VEG|WARM|FREQUENT|MUSIC|OUTINGS",
                    "ST110|NORMAL|HIGH|HIGH|QUIET|BALANCED|ANY|MODERATE|OCCASIONAL|SILENT|STUDY",
                    "ST111|LATE|LOW|LOW|LOUD|SOCIAL|NON_VEG|COOL|FREQUENT|GROUP|OUTINGS",
                    "ST112|EARLY|HIGH|HIGH|QUIET|INTROVERTED|VEG|COOL|RARE|SILENT|STUDY",
                    "ST113|NORMAL|MEDIUM|MEDIUM|MODERATE|BALANCED|ANY|MODERATE|OCCASIONAL|MUSIC|RELAX",
                    "ST114|LATE|HIGH|HIGH|QUIET|BALANCED|VEG|COOL|RARE|SILENT|STUDY",
                    "ST115|NORMAL|MEDIUM|HIGH|QUIET|INTROVERTED|VEG|MODERATE|RARE|SILENT|RELAX"
            );
            FileManager.writeLines("preferences.txt", prefLines);
        }
    }

    private static void initRooms() {
        if (!FileManager.hasData("rooms.txt")) {
            List<String> roomLines = Arrays.asList(
                    "A-101|A|1|2|ST101,ST102",
                    "A-102|A|1|2|ST103",
                    "A-103|A|1|2|NONE",
                    "A-201|A|2|2|NONE",
                    "A-202|A|2|2|NONE",
                    "B-101|B|1|2|ST105,ST107",
                    "B-102|B|1|2|NONE",
                    "B-203|B|2|2|NONE",
                    "B-204|B|2|2|NONE",
                    "C-101|C|1|2|ST110",
                    "C-201|C|2|2|NONE",
                    "C-304|C|3|2|NONE"
            );
            FileManager.writeLines("rooms.txt", roomLines);
        }
    }

    private static void initAllocations() {
        if (!FileManager.hasData("allocations.txt")) {
            List<String> allocLines = Arrays.asList(
                    "ALC-1001|ST101|A-101|1|2026-08-01 10:00:00",
                    "ALC-1002|ST102|A-101|2|2026-08-01 10:30:00",
                    "ALC-1003|ST103|A-102|1|2026-08-02 09:15:00",
                    "ALC-1004|ST105|B-101|1|2026-08-02 11:00:00",
                    "ALC-1005|ST107|B-101|2|2026-08-02 11:45:00",
                    "ALC-1006|ST110|C-101|1|2026-08-03 14:20:00"
            );
            FileManager.writeLines("allocations.txt", allocLines);
        }
    }

    private static void initRequests() {
        if (!FileManager.hasData("requests.txt")) {
            List<String> reqLines = Arrays.asList(
                    "REQ-1001|ST104|ST106|PENDING|2026-08-15 15:30:00",
                    "REQ-1002|ST108|ST112|PENDING|2026-08-16 11:10:00",
                    "REQ-1003|ST114|ST106|PENDING|2026-08-17 17:45:00"
            );
            FileManager.writeLines("requests.txt", reqLines);
        }
    }
}
