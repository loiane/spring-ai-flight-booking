package com.loiane.springfly.model;

import java.time.LocalDate;

public record Booking(
    String bookingNumber,
    LocalDate date,
    LocalDate bookingTo,
    Passenger passenger,
    String from,
    String to,
    BookingStatus bookingStatus,
    String seatNumber,
    BookingClass bookingClass
) {
}
