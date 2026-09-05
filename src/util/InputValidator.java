package util;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to validate terminal input and prevent malformed data or crashes.
 */
public class InputValidator {

    // Valid sets for 10 preference categories
    public static final List<String> SLEEP_SCHEDULES = Arrays.asList("EARLY", "NORMAL", "LATE");
    public static final List<String> STUDY_HABITS = Arrays.asList("LOW", "MEDIUM", "HIGH");
    public static final List<String> CLEANLINESS_LEVELS = Arrays.asList("LOW", "MEDIUM", "HIGH");
    public static final List<String> NOISE_PREFERENCES = Arrays.asList("QUIET", "MODERATE", "LOUD");
    public static final List<String> SOCIAL_PREFERENCES = Arrays.asList("INTROVERTED", "BALANCED", "SOCIAL");
    public static final List<String> FOOD_PREFERENCES = Arrays.asList("VEG", "NON_VEG", "ANY");
    public static final List<String> ROOM_TEMPERATURES = Arrays.asList("COOL", "MODERATE", "WARM");
    public static final List<String> GUEST_FREQUENCIES = Arrays.asList("RARE", "OCCASIONAL", "FREQUENT");
    public static final List<String> STUDY_ENVIRONMENTS = Arrays.asList("SILENT", "MUSIC", "GROUP");
    public static final List<String> WEEKEND_ROUTINES = Arrays.asList("RELAX", "STUDY", "OUTINGS");

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return trimmed.contains("@") && trimmed.contains(".") && trimmed.length() >= 5;
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.trim().length() >= 4;
    }

    public static boolean isValidOption(int choice, int min, int max) {
        return choice >= min && choice <= max;
    }

    public static boolean isValidPreference(String category, String value) {
        if (value == null) return false;
        String val = value.trim().toUpperCase();
        switch (category.toLowerCase()) {
            case "sleep":
                return SLEEP_SCHEDULES.contains(val);
            case "study":
                return STUDY_HABITS.contains(val);
            case "cleanliness":
                return CLEANLINESS_LEVELS.contains(val);
            case "noise":
                return NOISE_PREFERENCES.contains(val);
            case "social":
                return SOCIAL_PREFERENCES.contains(val);
            case "food":
                return FOOD_PREFERENCES.contains(val);
            case "temperature":
                return ROOM_TEMPERATURES.contains(val);
            case "guest":
                return GUEST_FREQUENCIES.contains(val);
            case "environment":
                return STUDY_ENVIRONMENTS.contains(val);
            case "weekend":
                return WEEKEND_ROUTINES.contains(val);
            default:
                return false;
        }
    }

    public static boolean isValidRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) return false;
        // Format: [Block]-[Floor/Number] e.g., A-101, B-203, C-304
        return roomNumber.trim().matches("^[A-Za-z0-9]+-[0-9]{3,4}$");
    }
}
