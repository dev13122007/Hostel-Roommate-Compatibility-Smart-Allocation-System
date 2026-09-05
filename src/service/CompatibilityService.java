package service;

import interfaces.CompatibilityCalculator;
import model.CompatibilityResult;
import model.Preference;
import model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the CompatibilityCalculator interface to calculate a 10-category
 * weighted compatibility score and generate explainable match rationales.
 */
public class CompatibilityService implements CompatibilityCalculator {

    @Override
    public CompatibilityResult calculate(Student s1, Student s2) {
        Preference p1 = s1.getPreference();
        Preference p2 = s2.getPreference();

        Map<String, Double> scores = new LinkedHashMap<>();
        Map<String, Double> maxWeights = new LinkedHashMap<>();
        List<String> positiveReasons = new ArrayList<>();
        List<String> differenceReasons = new ArrayList<>();

        if (p1 == null || p2 == null) {
            return new CompatibilityResult(s1, s2, 0.0, "INCOMPLETE PREFERENCES",
                    scores, maxWeights, "Cannot calculate compatibility because one or both students have not set their preferences.",
                    positiveReasons, differenceReasons);
        }

        // 1. Sleep Schedule (15%)
        maxWeights.put("Sleep Schedule", 15.0);
        double sleepScore = scoreOrdinalCategory(p1.getSleepSchedule(), p2.getSleepSchedule(), 15.0,
                "EARLY", "NORMAL", "LATE");
        scores.put("Sleep Schedule", sleepScore);
        if (sleepScore == 15.0) {
            positiveReasons.add("Identical sleep schedules (" + p1.getSleepSchedule() + ")");
        } else if (sleepScore > 0) {
            differenceReasons.add("Slight variation in sleep timing (" + p1.getSleepSchedule() + " vs " + p2.getSleepSchedule() + ")");
        } else {
            differenceReasons.add("Conflicting sleep schedule: Early sleeper vs Late sleeper");
        }

        // 2. Study Habit (15%)
        maxWeights.put("Study Habit", 15.0);
        double studyScore = scoreOrdinalCategory(p1.getStudyHabit(), p2.getStudyHabit(), 15.0,
                "LOW", "MEDIUM", "HIGH");
        scores.put("Study Habit", studyScore);
        if (studyScore == 15.0) {
            positiveReasons.add("Matching study intensity and habits (" + p1.getStudyHabit() + ")");
        } else if (studyScore > 0) {
            differenceReasons.add("Moderate difference in study duration expectations (" + p1.getStudyHabit() + " vs " + p2.getStudyHabit() + ")");
        } else {
            differenceReasons.add("Large gap in study habits (" + p1.getStudyHabit() + " vs " + p2.getStudyHabit() + ")");
        }

        // 3. Cleanliness (15%)
        maxWeights.put("Cleanliness", 15.0);
        double cleanScore = scoreOrdinalCategory(p1.getCleanliness(), p2.getCleanliness(), 15.0,
                "LOW", "MEDIUM", "HIGH");
        scores.put("Cleanliness", cleanScore);
        if (cleanScore == 15.0) {
            positiveReasons.add("Similar standards for room cleanliness and tidiness (" + p1.getCleanliness() + ")");
        } else if (cleanScore > 0) {
            differenceReasons.add("Minor variance in cleanliness expectations (" + p1.getCleanliness() + " vs " + p2.getCleanliness() + ")");
        } else {
            differenceReasons.add("High vs Low cleanliness standard differences may cause friction");
        }

        // 4. Noise Preference (10%)
        maxWeights.put("Noise Preference", 10.0);
        double noiseScore = scoreOrdinalCategory(p1.getNoisePreference(), p2.getNoisePreference(), 10.0,
                "QUIET", "MODERATE", "LOUD");
        scores.put("Noise Preference", noiseScore);
        if (noiseScore == 10.0) {
            positiveReasons.add("Compatible noise tolerance and quiet hours preferences (" + p1.getNoisePreference() + ")");
        } else if (noiseScore > 0) {
            differenceReasons.add("Noise preferences slightly differ (" + p1.getNoisePreference() + " vs " + p2.getNoisePreference() + ")");
        } else {
            differenceReasons.add("Quiet room preference conflicts with loud environment tolerance");
        }

        // 5. Social Preference (10%)
        maxWeights.put("Social Preference", 10.0);
        double socialScore = scoreOrdinalCategory(p1.getSocialPreference(), p2.getSocialPreference(), 10.0,
                "INTROVERTED", "BALANCED", "SOCIAL");
        scores.put("Social Preference", socialScore);
        if (socialScore == 10.0) {
            positiveReasons.add("Harmonious social styles and personal space requirements (" + p1.getSocialPreference() + ")");
        } else if (socialScore > 0) {
            differenceReasons.add("Slight divergence in social energy levels (" + p1.getSocialPreference() + " vs " + p2.getSocialPreference() + ")");
        } else {
            differenceReasons.add("Introverted vs Highly Social lifestyle differences");
        }

        // 6. Food Preference (10%)
        maxWeights.put("Food Preference", 10.0);
        double foodScore = scoreFoodPreference(p1.getFoodPreference(), p2.getFoodPreference(), 10.0);
        scores.put("Food Preference", foodScore);
        if (foodScore == 10.0) {
            positiveReasons.add("Compatible dietary and meal habits (" + p1.getFoodPreference() + " & " + p2.getFoodPreference() + ")");
        } else {
            differenceReasons.add("Dietary preferences differ: Vegetarian vs Non-Vegetarian in shared space");
        }

        // 7. Room Temperature (10%)
        maxWeights.put("Room Temperature", 10.0);
        double tempScore = scoreOrdinalCategory(p1.getRoomTemperature(), p2.getRoomTemperature(), 10.0,
                "COOL", "MODERATE", "WARM");
        scores.put("Room Temperature", tempScore);
        if (tempScore == 10.0) {
            positiveReasons.add("Agreed temperature / AC preference (" + p1.getRoomTemperature() + ")");
        } else if (tempScore > 0) {
            differenceReasons.add("Mild difference in AC/fan temperature settings (" + p1.getRoomTemperature() + " vs " + p2.getRoomTemperature() + ")");
        } else {
            differenceReasons.add("Opposite temperature preferences (Cool AC vs Warm environment)");
        }

        // 8. Guest Frequency (5%)
        maxWeights.put("Guest Frequency", 5.0);
        double guestScore = scoreOrdinalCategory(p1.getGuestFrequency(), p2.getGuestFrequency(), 5.0,
                "RARE", "OCCASIONAL", "FREQUENT");
        scores.put("Guest Frequency", guestScore);
        if (guestScore == 5.0) {
            positiveReasons.add("Aligned expectations regarding visitors and guests in room (" + p1.getGuestFrequency() + ")");
        } else if (guestScore > 0) {
            differenceReasons.add("Slight difference in visitor frequency expectations");
        } else {
            differenceReasons.add("Frequent host vs Minimal visitor preference conflict");
        }

        // 9. Study Environment (5%)
        maxWeights.put("Study Environment", 5.0);
        double envScore = scoreEnvironmentCategory(p1.getStudyEnvironment(), p2.getStudyEnvironment(), 5.0);
        scores.put("Study Environment", envScore);
        if (envScore == 5.0) {
            positiveReasons.add("Matching study atmosphere (" + p1.getStudyEnvironment() + ")");
        } else if (envScore > 0) {
            differenceReasons.add("Minor divergence in study atmosphere preference (" + p1.getStudyEnvironment() + " vs " + p2.getStudyEnvironment() + ")");
        } else {
            differenceReasons.add("Silent study focus vs Group study preference in room");
        }

        // 10. Weekend Routine (5%)
        maxWeights.put("Weekend Routine", 5.0);
        double weekendScore = scoreWeekendCategory(p1.getWeekendRoutine(), p2.getWeekendRoutine(), 5.0);
        scores.put("Weekend Routine", weekendScore);
        if (weekendScore == 5.0) {
            positiveReasons.add("Matching weekend schedule and lifestyle (" + p1.getWeekendRoutine() + ")");
        } else if (weekendScore > 0) {
            differenceReasons.add("Slight difference in weekend activities");
        } else {
            differenceReasons.add("Different weekend routine (Studying vs Frequent Outings)");
        }

        // Total calculation
        double total = 0.0;
        for (double s : scores.values()) {
            total += s;
        }

        // Level categorization
        String level;
        if (total >= 90.0) {
            level = "EXCELLENT MATCH";
        } else if (total >= 80.0) {
            level = "VERY GOOD MATCH";
        } else if (total >= 70.0) {
            level = "GOOD MATCH";
        } else if (total >= 60.0) {
            level = "AVERAGE MATCH";
        } else {
            level = "LOW MATCH";
        }

        // Synthesize recommendation summary
        String recommendation = buildRecommendation(total, level, positiveReasons, differenceReasons);

        return new CompatibilityResult(s1, s2, total, level, scores, maxWeights,
                recommendation, positiveReasons, differenceReasons);
    }

    /**
     * Helper to score 3-tier ordinal categories (e.g. LOW-MEDIUM-HIGH, EARLY-NORMAL-LATE).
     * Exact match = full weight, 1 step apart = 50% weight, 2 steps apart = 0%.
     */
    private double scoreOrdinalCategory(String v1, String v2, double weight,
                                        String low, String mid, String high) {
        if (v1 == null || v2 == null) return 0.0;
        if (v1.equalsIgnoreCase(v2)) {
            return weight; // 100%
        }
        int rank1 = getOrdinalRank(v1, low, mid, high);
        int rank2 = getOrdinalRank(v2, low, mid, high);
        int diff = Math.abs(rank1 - rank2);

        if (diff == 1) {
            return weight * 0.5; // 50% compatible
        }
        return 0.0; // Conflicting (diff == 2)
    }

    private int getOrdinalRank(String val, String low, String mid, String high) {
        if (val.equalsIgnoreCase(low)) return 1;
        if (val.equalsIgnoreCase(mid)) return 2;
        if (val.equalsIgnoreCase(high)) return 3;
        return 2;
    }

    /**
     * Food scoring: VEG+VEG, NON_VEG+NON_VEG, or ANY+ANY = 100%
     * VEG+ANY or NON_VEG+ANY = 100% (compatible)
     * VEG+NON_VEG = 0% (conflict)
     */
    private double scoreFoodPreference(String f1, String f2, double weight) {
        if (f1 == null || f2 == null) return 0.0;
        if (f1.equalsIgnoreCase(f2)) {
            return weight;
        }
        if (f1.equalsIgnoreCase("ANY") || f2.equalsIgnoreCase("ANY")) {
            return weight; // Fully compatible with flexible diet
        }
        return 0.0; // VEG vs NON_VEG conflict
    }

    /**
     * Study environment: SILENT, MUSIC, GROUP
     */
    private double scoreEnvironmentCategory(String e1, String e2, double weight) {
        if (e1 == null || e2 == null) return 0.0;
        if (e1.equalsIgnoreCase(e2)) return weight;
        if ((e1.equalsIgnoreCase("SILENT") && e2.equalsIgnoreCase("MUSIC")) ||
            (e1.equalsIgnoreCase("MUSIC") && e2.equalsIgnoreCase("SILENT")) ||
            (e1.equalsIgnoreCase("MUSIC") && e2.equalsIgnoreCase("GROUP")) ||
            (e1.equalsIgnoreCase("GROUP") && e2.equalsIgnoreCase("MUSIC"))) {
            return weight * 0.5;
        }
        return 0.0; // SILENT vs GROUP conflict
    }

    /**
     * Weekend routine: RELAX, STUDY, OUTINGS
     */
    private double scoreWeekendCategory(String w1, String w2, double weight) {
        if (w1 == null || w2 == null) return 0.0;
        if (w1.equalsIgnoreCase(w2)) return weight;
        if ((w1.equalsIgnoreCase("RELAX") && w2.equalsIgnoreCase("STUDY")) ||
            (w1.equalsIgnoreCase("STUDY") && w2.equalsIgnoreCase("RELAX")) ||
            (w1.equalsIgnoreCase("RELAX") && w2.equalsIgnoreCase("OUTINGS")) ||
            (w1.equalsIgnoreCase("OUTINGS") && w2.equalsIgnoreCase("RELAX"))) {
            return weight * 0.5;
        }
        return 0.0; // STUDY vs OUTINGS conflict
    }

    private String buildRecommendation(double total, String level, List<String> pos, List<String> diff) {
        StringBuilder sb = new StringBuilder();
        if (total >= 90.0) {
            sb.append("Highly recommended pairing. Both students exhibit harmonious sleep, study, and cleanliness expectations with virtually no conflicts.");
        } else if (total >= 80.0) {
            sb.append("Strongly recommended. Both students share core lifestyle routines. Minor differences can easily be coordinated.");
        } else if (total >= 70.0) {
            sb.append("Good compatible match. General habits align well, though mutual agreement on quiet hours or AC usage is advised.");
        } else if (total >= 60.0) {
            sb.append("Moderate match. Noticeable lifestyle variances exist in study or sleep timing. May require compromise.");
        } else {
            sb.append("Not recommended as primary pairing due to divergent daily schedules, study habits, or environmental preferences.");
        }
        return sb.toString();
    }
}
