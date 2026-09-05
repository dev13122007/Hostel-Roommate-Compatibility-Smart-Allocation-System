package service;

import exception.InvalidLoginException;
import exception.StudentNotFoundException;
import model.Student;
import util.FileManager;
import util.IdGenerator;
import util.InputValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing student lifecycle, registration, authentication, profile updates, and lookups.
 */
public class StudentService {
    private static final String FILE_NAME = "students.txt";
    // Map of studentId -> Student for fast O(1) lookup
    private Map<String, Student> studentMap;
    // List for maintaining student ordering
    private List<Student> studentList;
    private PreferenceService preferenceService;

    public StudentService(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
        this.studentMap = new HashMap<>();
        this.studentList = new ArrayList<>();
        loadStudents();
    }

    public void loadStudents() {
        studentMap.clear();
        studentList.clear();
        List<String> lines = FileManager.readLines(FILE_NAME);
        int maxId = 100;
        for (String line : lines) {
            Student s = Student.fromFileString(line);
            if (s != null) {
                // Link preference if available
                if (preferenceService.hasPreference(s.getStudentId())) {
                    s.setPreference(preferenceService.getPreference(s.getStudentId()));
                }
                studentMap.put(s.getStudentId(), s);
                studentList.add(s);

                // Update IdGenerator tracking
                try {
                    String idNumStr = s.getStudentId().replaceAll("[^0-9]", "");
                    if (!idNumStr.isEmpty()) {
                        int num = Integer.parseInt(idNumStr);
                        if (num > maxId) maxId = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        IdGenerator.initCounters(maxId, 1000, 1000);
    }

    public void saveAllStudents() {
        List<String> lines = new ArrayList<>();
        for (Student s : studentList) {
            lines.add(s.toFileString());
        }
        FileManager.writeLines(FILE_NAME, lines);
    }

    public Student registerStudent(String name, String email, String password,
                                   String course, String year, String hostel) throws Exception {
        if (!InputValidator.isNotEmpty(name)) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (!InputValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        // Check duplicate email
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException("A student with email '" + email + "' is already registered.");
            }
        }

        String studentId = IdGenerator.generateStudentId();
        Student newStudent = new Student(studentId, name.trim(), email.trim(), password, course.trim(), year.trim(), hostel.trim());

        studentMap.put(studentId, newStudent);
        studentList.add(newStudent);
        saveAllStudents();
        return newStudent;
    }

    public Student login(String idOrEmail, String password) throws InvalidLoginException {
        if (!InputValidator.isNotEmpty(idOrEmail) || !InputValidator.isNotEmpty(password)) {
            throw new InvalidLoginException("Student ID/Email and password cannot be empty.");
        }

        Student student = null;
        if (studentMap.containsKey(idOrEmail.trim().toUpperCase())) {
            student = studentMap.get(idOrEmail.trim().toUpperCase());
        } else if (studentMap.containsKey(idOrEmail.trim())) {
            student = studentMap.get(idOrEmail.trim());
        } else {
            // Check by email
            for (Student s : studentList) {
                if (s.getEmail().equalsIgnoreCase(idOrEmail.trim())) {
                    student = s;
                    break;
                }
            }
        }

        if (student == null || !student.verifyPassword(password)) {
            throw new InvalidLoginException("Invalid Student ID/Email or Password. Please try again.");
        }

        // Refresh preference link
        if (preferenceService.hasPreference(student.getStudentId())) {
            student.setPreference(preferenceService.getPreference(student.getStudentId()));
        }

        return student;
    }

    public Student getStudentById(String studentId) throws StudentNotFoundException {
        if (studentId == null) {
            throw new StudentNotFoundException("Student ID cannot be null.");
        }
        Student s = studentMap.get(studentId.trim().toUpperCase());
        if (s == null) {
            s = studentMap.get(studentId.trim());
        }
        if (s == null) {
            throw new StudentNotFoundException("Student with ID '" + studentId + "' was not found.");
        }
        // Refresh preference link
        if (preferenceService.hasPreference(s.getStudentId())) {
            s.setPreference(preferenceService.getPreference(s.getStudentId()));
        }
        return s;
    }

    public boolean studentExists(String studentId) {
        return studentMap.containsKey(studentId.trim().toUpperCase()) || studentMap.containsKey(studentId.trim());
    }

    public void updateStudentProfile(String studentId, String name, String email, String course, String year, String hostel)
            throws StudentNotFoundException, IllegalArgumentException {
        Student student = getStudentById(studentId);

        if (InputValidator.isNotEmpty(name)) {
            student.setName(name.trim());
        }
        if (InputValidator.isNotEmpty(email)) {
            if (!InputValidator.isValidEmail(email)) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            // Check if another student has this email
            for (Student s : studentList) {
                if (!s.getStudentId().equals(studentId) && s.getEmail().equalsIgnoreCase(email.trim())) {
                    throw new IllegalArgumentException("Email '" + email + "' is already in use by another student.");
                }
            }
            student.setEmail(email.trim());
        }
        if (InputValidator.isNotEmpty(course)) {
            student.setCourse(course.trim());
        }
        if (InputValidator.isNotEmpty(year)) {
            student.setYear(year.trim());
        }
        if (InputValidator.isNotEmpty(hostel)) {
            student.setHostel(hostel.trim());
        }

        saveAllStudents();
    }

    public List<Student> getAllStudents() {
        // Ensure preferences are attached
        for (Student s : studentList) {
            if (preferenceService.hasPreference(s.getStudentId())) {
                s.setPreference(preferenceService.getPreference(s.getStudentId()));
            }
        }
        return new ArrayList<>(studentList);
    }

    public int getTotalStudentCount() {
        return studentList.size();
    }
}
