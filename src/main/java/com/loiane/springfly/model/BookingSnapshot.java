package com.loiane.springfly.model;

import java.time.LocalDate;

/**
 * Snapshot of a booking state before modifications.
 * Used for rollback functionality in error recovery.
 */
public record BookingSnapshot(
    String bookingNumber,
    LocalDate date,
    String from,
    String to,
    BookingStatus bookingStatus,
    LocalDate capturedAt
) {
    public static BookingSnapshot from(Booking booking) {
        return new BookingSnapshot(
            booking.getBookingNumber(),
            booking.getDate(),
            booking.getFrom(),
            booking.getTo(),
            booking.getBookingStatus(),
            LocalDate.now()
        );
    }
}
