package com.loiane.springfly.model;

import java.time.LocalDate;

public record BookingDetails(
  String bookingNumber, 
	String firstName, 
	String lastName, 
	LocalDate date,
	BookingStatus bookingStatus, 
	String from, 
	String to, 
	String seatNumber, 
	String bookingClass
) { 

  public BookingDetails(Booking booking) {
    this(
      booking.bookingNumber(),
      booking.passenger().firstName(),
      booking.passenger().lastName(),
      booking.date(),
      booking.bookingStatus(),
      booking.from(),
      booking.to(),
      booking.seatNumber(),
      booking.bookingClass().name()
    );
  }
}
