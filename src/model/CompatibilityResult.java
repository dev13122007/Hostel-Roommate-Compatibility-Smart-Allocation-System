package model;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates the quantitative compatibility score, qualitative level,
 * detailed category breakdowns, and explainable AI-style decision rationales.
 */
public class CompatibilityResult {
    private Student studentA;
    private Student studentB;
    private double totalScore;
    private String compatibilityLevel;
    private Map<String, Double> categoryScores;
    private Map<String, Double> categoryMaxWeights;
    private String recommendation;
    private List<String> positiveReasons;
    private List<String> differenceReasons;

    public CompatibilityResult(Student studentA, Student studentB, double totalScore,
                               String compatibilityLevel, Map<String, Double> categoryScores,
                               Map<String, Double> categoryMaxWeights, String recommendation,
                               List<String> positiveReasons, List<String> differenceReasons) {
        this.studentA = studentA;
        this.studentB = studentB;
        this.totalScore = totalScore;
        this.compatibilityLevel = compatibilityLevel;
        this.categoryScores = categoryScores;
        this.categoryMaxWeights = categoryMaxWeights;
        this.recommendation = recommendation;
        this.positiveReasons = positiveReasons;
        this.differenceReasons = differenceReasons;
    }

    public Student getStudentA() {
        return studentA;
    }

    public Student getStudentB() {
        return studentB;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public String getCompatibilityLevel() {
        return compatibilityLevel;
    }

    public Map<String, Double> getCategoryScores() {
        return categoryScores;
    }

    public Map<String, Double> getCategoryMaxWeights() {
        return categoryMaxWeights;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public List<String> getPositiveReasons() {
        return positiveReasons;
    }

    public List<String> getDifferenceReasons() {
        return differenceReasons;
    }

    /**
     * Displays a formatted breakdown of category-wise scores and explanatory notes.
     */
    public void displayDetailedBreakdown() {
        System.out.println("========================================");
        System.out.println("       COMPATIBILITY BREAKDOWN          ");
        System.out.println("========================================");
        System.out.println("Comparing: " + studentA.getName() + " with " + studentB.getName());
        System.out.println("Course   : " + studentB.getCourse() + " (" + studentB.getYear() + ")");
        System.out.println("Hostel   : " + studentB.getHostel());
        System.out.println("----------------------------------------");
        System.out.printf("  %-22s %10s%n", "Category", "Score");
        System.out.println("----------------------------------------");

        for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            double score = entry.getValue();
            double maxWeight = categoryMaxWeights.getOrDefault(category, 0.0);
            System.out.printf("  %-22s  %4.1f / %-4.1f%n", category, score, maxWeight);
        }

        System.out.println("----------------------------------------");
        System.out.printf("TOTAL SCORE: %4.1f / 100.0%n", totalScore);
        System.out.println("----------------------------------------");
        System.out.println("MATCH LEVEL: " + compatibilityLevel);
        System.out.println();
        System.out.println("Why this match works:");
        if (positiveReasons.isEmpty()) {
            System.out.println("  - Limited common lifestyle habits.");
        } else {
            for (String pos : positiveReasons) {
                System.out.println("  + " + pos);
            }
        }
        System.out.println();
        System.out.println("Possible differences to discuss:");
        if (differenceReasons.isEmpty()) {
            System.out.println("  - No significant lifestyle conflicts identified.");
        } else {
            for (String diff : differenceReasons) {
                System.out.println("  - " + diff);
            }
        }
        System.out.println();
        System.out.println("System Recommendation:");
        System.out.println("  " + recommendation);
        System.out.println("========================================");
    }
}
