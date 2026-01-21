package com.loiane.springfly.model;

/**
 * Result of validating an action taken by the agent.
 * Used for self-reflection to verify actions match customer intent.
 */
public record ValidationResult(
    boolean success,
    String actionTaken,
    String expectedOutcome,
    String actualOutcome,
    String suggestion
) {
    
    public static ValidationResult success(String actionTaken, String outcome) {
        return new ValidationResult(true, actionTaken, outcome, outcome, null);
    }

    public static ValidationResult failure(String actionTaken, String expected, String actual, String suggestion) {
        return new ValidationResult(false, actionTaken, expected, actual, suggestion);
    }

    public static ValidationResult mismatch(String actionTaken, String expected, String actual) {
        return new ValidationResult(false, actionTaken, expected, actual, 
            "The action result does not match the expected outcome. Consider verifying with the customer.");
    }
}
