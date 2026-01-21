package com.loiane.springfly.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.loiane.springfly.model.Booking;
import com.loiane.springfly.model.BookingDetails;
import com.loiane.springfly.model.BookingSnapshot;
import com.loiane.springfly.model.BookingStatus;
import com.loiane.springfly.model.SpringFlyDB;
import com.loiane.springfly.model.ValidationResult;

/**
 * Tools for agent self-reflection and error recovery.
 * Allows the agent to validate its actions and rollback if needed.
 */
@Service
public class ValidationTools {

    private static final Logger log = LoggerFactory.getLogger(ValidationTools.class);

    private final SpringFlyDB springFlyDB;
    private final BookingService bookingService;

    // Store snapshots for potential rollback (keyed by booking number)
    private final Map<String, BookingSnapshot> snapshots = new ConcurrentHashMap<>();

    public ValidationTools(SpringFlyDB springFlyDB, BookingService bookingService) {
        this.springFlyDB = springFlyDB;
        this.bookingService = bookingService;
    }

    @Tool(description = """
        Verify that the last action taken was successful and matches the customer's intent. 
        Use this after performing any booking modification to confirm the result.
        Parameters:
        - bookingNumber: The booking reference number
        - firstName: Customer's first name
        - lastName: Customer's last name  
        - actionTaken: Description of what action was performed (e.g., 'changed flight date to 2026-02-15')
        - customerIntent: What the customer originally requested (e.g., 'change flight to February 15th')
        Returns a ValidationResult indicating if the action was successful and matches intent.
        """)
    public ValidationResult validateAction(String bookingNumber, String firstName, String lastName,
            String actionTaken, String customerIntent) {
        log.info("Validating action '{}' against customer intent '{}' for booking {}", 
            actionTaken, customerIntent, bookingNumber);

        try {
            BookingDetails currentBooking = bookingService.getBookingDetails(bookingNumber, firstName, lastName);

            // Check if booking exists and is valid
            if (currentBooking.date() == null) {
                return ValidationResult.failure(
                    actionTaken,
                    customerIntent,
                    "Booking not found or invalid",
                    "Verify the booking details with the customer and try again."
                );
            }

            // Check if booking was cancelled when it shouldn't be
            if (currentBooking.bookingStatus() == BookingStatus.CANCELLED 
                    && !customerIntent.toLowerCase().contains("cancel")) {
                return ValidationResult.failure(
                    actionTaken,
                    customerIntent,
                    "Booking is cancelled but cancellation was not requested",
                    "This may be an error. Consider using rollback if the cancellation was unintended."
                );
            }

            // Basic validation passed - action appears successful
            String actualOutcome = String.format("Booking %s: %s -> %s on %s, Status: %s",
                currentBooking.bookingNumber(),
                currentBooking.from(),
                currentBooking.to(),
                currentBooking.date(),
                currentBooking.bookingStatus()
            );

            log.info("Validation successful for booking {}", bookingNumber);
            return ValidationResult.success(actionTaken, actualOutcome);

        } catch (Exception e) {
            log.error("Validation failed for booking {}: {}", bookingNumber, e.getMessage());
            return ValidationResult.failure(
                actionTaken,
                customerIntent,
                "Error: " + e.getMessage(),
                "An error occurred during validation. Please verify the booking status manually."
            );
        }
    }

    @Tool(description = """
        Create a snapshot of the current booking state before making changes.
        Call this BEFORE performing any modification to enable rollback capability.
        Parameters:
        - bookingNumber: The booking reference number
        - firstName: Customer's first name
        - lastName: Customer's last name
        Returns confirmation that the snapshot was created.
        """)
    public String createSnapshot(String bookingNumber, String firstName, String lastName) {
        log.info("Creating snapshot for booking {}", bookingNumber);

        try {
            Booking booking = findBooking(bookingNumber, firstName, lastName);
            BookingSnapshot snapshot = BookingSnapshot.from(booking);
            snapshots.put(bookingNumber, snapshot);

            return String.format("Snapshot created for booking %s. Current state: %s -> %s on %s, Status: %s. " +
                    "You can now safely make changes and rollback if needed.",
                bookingNumber, booking.getFrom(), booking.getTo(), booking.getDate(), booking.getBookingStatus());

        } catch (Exception e) {
            log.error("Failed to create snapshot for booking {}: {}", bookingNumber, e.getMessage());
            return "Failed to create snapshot: " + e.getMessage();
        }
    }

    @Tool(description = """
        Rollback the last changes made to a booking, restoring it to the previously saved snapshot.
        Use this if a modification was made in error or doesn't match customer intent.
        Parameters:
        - bookingNumber: The booking reference number
        - reason: Why the rollback is being performed
        Returns confirmation of the rollback or an error if no snapshot exists.
        """)
    public String rollbackBooking(String bookingNumber, String firstName, String lastName, String reason) {
        log.info("Rolling back booking {} due to: {}", bookingNumber, reason);

        BookingSnapshot snapshot = snapshots.get(bookingNumber);
        if (snapshot == null) {
            return String.format("Cannot rollback booking %s: No snapshot was created before the modification. " +
                    "Please manually verify and correct the booking with the customer.", bookingNumber);
        }

        try {
            Booking booking = findBooking(bookingNumber, firstName, lastName);

            // Restore previous state
            booking.setDate(snapshot.date());
            booking.setFrom(snapshot.from());
            booking.setTo(snapshot.to());
            booking.setBookingStatus(snapshot.bookingStatus());

            // Remove the used snapshot
            snapshots.remove(bookingNumber);

            log.info("Successfully rolled back booking {} to state from {}", bookingNumber, snapshot.capturedAt());
            return String.format("Booking %s has been rolled back to its previous state: %s -> %s on %s, Status: %s. " +
                    "Reason for rollback: %s",
                bookingNumber, snapshot.from(), snapshot.to(), snapshot.date(), snapshot.bookingStatus(), reason);

        } catch (Exception e) {
            log.error("Rollback failed for booking {}: {}", bookingNumber, e.getMessage());
            return "Rollback failed: " + e.getMessage() + ". Please assist the customer manually.";
        }
    }

    private Booking findBooking(String bookingNumber, String firstName, String lastName) {
        return springFlyDB.getBookings()
            .stream()
            .filter(b -> b.getBookingNumber().equalsIgnoreCase(bookingNumber))
            .filter(b -> b.getPassenger().firstName().equalsIgnoreCase(firstName))
            .filter(b -> b.getPassenger().lastName().equalsIgnoreCase(lastName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}
