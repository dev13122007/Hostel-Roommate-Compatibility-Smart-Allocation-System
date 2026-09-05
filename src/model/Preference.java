package model;

/**
 * Represents a student's lifestyle and study preferences across 10 categories.
 */
public class Preference {
    private String studentId;
    private String sleepSchedule;   // EARLY, NORMAL, LATE
    private String studyHabit;       // LOW, MEDIUM, HIGH
    private String cleanliness;      // LOW, MEDIUM, HIGH
    private String noisePreference;  // QUIET, MODERATE, LOUD
    private String socialPreference; // INTROVERTED, BALANCED, SOCIAL
    private String foodPreference;   // VEG, NON_VEG, ANY
    private String roomTemperature;  // COOL, MODERATE, WARM
    private String guestFrequency;   // RARE, OCCASIONAL, FREQUENT
    private String studyEnvironment; // SILENT, MUSIC, GROUP
    private String weekendRoutine;   // RELAX, STUDY, OUTINGS

    public Preference(String studentId, String sleepSchedule, String studyHabit,
                      String cleanliness, String noisePreference, String socialPreference,
                      String foodPreference, String roomTemperature, String guestFrequency,
                      String studyEnvironment, String weekendRoutine) {
        this.studentId = studentId;
        this.sleepSchedule = sleepSchedule.toUpperCase();
        this.studyHabit = studyHabit.toUpperCase();
        this.cleanliness = cleanliness.toUpperCase();
        this.noisePreference = noisePreference.toUpperCase();
        this.socialPreference = socialPreference.toUpperCase();
        this.foodPreference = foodPreference.toUpperCase();
        this.roomTemperature = roomTemperature.toUpperCase();
        this.guestFrequency = guestFrequency.toUpperCase();
        this.studyEnvironment = studyEnvironment.toUpperCase();
        this.weekendRoutine = weekendRoutine.toUpperCase();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSleepSchedule() {
        return sleepSchedule;
    }

    public void setSleepSchedule(String sleepSchedule) {
        this.sleepSchedule = sleepSchedule.toUpperCase();
    }

    public String getStudyHabit() {
        return studyHabit;
    }

    public void setStudyHabit(String studyHabit) {
        this.studyHabit = studyHabit.toUpperCase();
    }

    public String getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(String cleanliness) {
        this.cleanliness = cleanliness.toUpperCase();
    }

    public String getNoisePreference() {
        return noisePreference;
    }

    public void setNoisePreference(String noisePreference) {
        this.noisePreference = noisePreference.toUpperCase();
    }

    public String getSocialPreference() {
        return socialPreference;
    }

    public void setSocialPreference(String socialPreference) {
        this.socialPreference = socialPreference.toUpperCase();
    }

    public String getFoodPreference() {
        return foodPreference;
    }

    public void setFoodPreference(String foodPreference) {
        this.foodPreference = foodPreference.toUpperCase();
    }

    public String getRoomTemperature() {
        return roomTemperature;
    }

    public void setRoomTemperature(String roomTemperature) {
        this.roomTemperature = roomTemperature.toUpperCase();
    }

    public String getGuestFrequency() {
        return guestFrequency;
    }

    public void setGuestFrequency(String guestFrequency) {
        this.guestFrequency = guestFrequency.toUpperCase();
    }

    public String getStudyEnvironment() {
        return studyEnvironment;
    }

    public void setStudyEnvironment(String studyEnvironment) {
        this.studyEnvironment = studyEnvironment.toUpperCase();
    }

    public String getWeekendRoutine() {
        return weekendRoutine;
    }

    public void setWeekendRoutine(String weekendRoutine) {
        this.weekendRoutine = weekendRoutine.toUpperCase();
    }

    public void displayPreferences() {
        System.out.println("----------------------------------------");
        System.out.println("  LIFESTYLE & STUDY PREFERENCES         ");
        System.out.println("----------------------------------------");
        System.out.printf("  %-20s : %s%n", "Sleep Schedule", sleepSchedule);
        System.out.printf("  %-20s : %s%n", "Study Habit", studyHabit);
        System.out.printf("  %-20s : %s%n", "Cleanliness", cleanliness);
        System.out.printf("  %-20s : %s%n", "Noise Preference", noisePreference);
        System.out.printf("  %-20s : %s%n", "Social Preference", socialPreference);
        System.out.printf("  %-20s : %s%n", "Food Preference", foodPreference);
        System.out.printf("  %-20s : %s%n", "Room Temperature", roomTemperature);
        System.out.printf("  %-20s : %s%n", "Guest Frequency", guestFrequency);
        System.out.printf("  %-20s : %s%n", "Study Environment", studyEnvironment);
        System.out.printf("  %-20s : %s%n", "Weekend Routine", weekendRoutine);
        System.out.println("----------------------------------------");
    }

    /**
     * Converts to serialized pipe-delimited format for storage.
     */
    public String toFileString() {
        return studentId + "|" + sleepSchedule + "|" + studyHabit + "|" + cleanliness + "|" +
               noisePreference + "|" + socialPreference + "|" + foodPreference + "|" +
               roomTemperature + "|" + guestFrequency + "|" + studyEnvironment + "|" + weekendRoutine;
    }

    /**
     * Parses from serialized line.
     */
    public static Preference fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 11) {
            return new Preference(parts[0], parts[1], parts[2], parts[3], parts[4],
                                  parts[5], parts[6], parts[7], parts[8], parts[9], parts[10]);
        }
        return null;
    }
}
