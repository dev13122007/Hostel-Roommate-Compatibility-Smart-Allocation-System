package interfaces;

import model.CompatibilityResult;
import model.Student;

/**
 * Interface defining the contract for roommate compatibility evaluation.
 * Demonstrates abstraction and polymorphism.
 */
public interface CompatibilityCalculator {
    /**
     * Evaluates compatibility between two students based on their preferences.
     *
     * @param s1 The reference student
     * @param s2 The candidate student
     * @return CompatibilityResult containing score, level, and breakdown
     */
    CompatibilityResult calculate(Student s1, Student s2);
}
