package service;

import exception.InvalidPreferenceException;
import model.Preference;
import util.FileManager;
import util.InputValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for storing, retrieving, and validating student preferences.
 */
public class PreferenceService {
    private static final String FILE_NAME = "preferences.txt";
    // Maps student ID to their lifestyle Preference
    private Map<String, Preference> preferenceMap;

    public PreferenceService() {
        this.preferenceMap = new HashMap<>();
        loadPreferences();
    }

    public void loadPreferences() {
        preferenceMap.clear();
        List<String> lines = FileManager.readLines(FILE_NAME);
        for (String line : lines) {
            Preference p = Preference.fromFileString(line);
            if (p != null) {
                preferenceMap.put(p.getStudentId(), p);
            }
        }
    }

    public void saveAllPreferences() {
        List<String> lines = new java.util.ArrayList<>();
        for (Preference p : preferenceMap.values()) {
            lines.add(p.toFileString());
        }
        FileManager.writeLines(FILE_NAME, lines);
    }

    public Preference getPreference(String studentId) {
        return preferenceMap.get(studentId);
    }

    public boolean hasPreference(String studentId) {
        return preferenceMap.containsKey(studentId);
    }

    public void saveOrUpdatePreference(Preference preference) throws InvalidPreferenceException {
        validatePreference(preference);
        preferenceMap.put(preference.getStudentId(), preference);
        saveAllPreferences();
    }

    private void validatePreference(Preference p) throws InvalidPreferenceException {
        if (p == null) {
            throw new InvalidPreferenceException("Preference object cannot be null.");
        }
        if (!InputValidator.isValidPreference("sleep", p.getSleepSchedule())) {
            throw new InvalidPreferenceException("Invalid Sleep Schedule: " + p.getSleepSchedule() + ". Allowed: EARLY, NORMAL, LATE");
        }
        if (!InputValidator.isValidPreference("study", p.getStudyHabit())) {
            throw new InvalidPreferenceException("Invalid Study Habit: " + p.getStudyHabit() + ". Allowed: LOW, MEDIUM, HIGH");
        }
        if (!InputValidator.isValidPreference("cleanliness", p.getCleanliness())) {
            throw new InvalidPreferenceException("Invalid Cleanliness: " + p.getCleanliness() + ". Allowed: LOW, MEDIUM, HIGH");
        }
        if (!InputValidator.isValidPreference("noise", p.getNoisePreference())) {
            throw new InvalidPreferenceException("Invalid Noise Preference: " + p.getNoisePreference() + ". Allowed: QUIET, MODERATE, LOUD");
        }
        if (!InputValidator.isValidPreference("social", p.getSocialPreference())) {
            throw new InvalidPreferenceException("Invalid Social Preference: " + p.getSocialPreference() + ". Allowed: INTROVERTED, BALANCED, SOCIAL");
        }
        if (!InputValidator.isValidPreference("food", p.getFoodPreference())) {
            throw new InvalidPreferenceException("Invalid Food Preference: " + p.getFoodPreference() + ". Allowed: VEG, NON_VEG, ANY");
        }
        if (!InputValidator.isValidPreference("temperature", p.getRoomTemperature())) {
            throw new InvalidPreferenceException("Invalid Room Temperature: " + p.getRoomTemperature() + ". Allowed: COOL, MODERATE, WARM");
        }
        if (!InputValidator.isValidPreference("guest", p.getGuestFrequency())) {
            throw new InvalidPreferenceException("Invalid Guest Frequency: " + p.getGuestFrequency() + ". Allowed: RARE, OCCASIONAL, FREQUENT");
        }
        if (!InputValidator.isValidPreference("environment", p.getStudyEnvironment())) {
            throw new InvalidPreferenceException("Invalid Study Environment: " + p.getStudyEnvironment() + ". Allowed: SILENT, MUSIC, GROUP");
        }
        if (!InputValidator.isValidPreference("weekend", p.getWeekendRoutine())) {
            throw new InvalidPreferenceException("Invalid Weekend Routine: " + p.getWeekendRoutine() + ". Allowed: RELAX, STUDY, OUTINGS");
        }
    }

    public Map<String, Preference> getAllPreferences() {
        return preferenceMap;
    }
}
