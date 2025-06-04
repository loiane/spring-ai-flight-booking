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
      booking.getBookingNumber(),
      booking.getPassenger().firstName(),
      booking.getPassenger().lastName(),
      booking.getBookingTo(),
      booking.getBookingStatus(),
      booking.getFrom(),
      booking.getTo(),
      booking.getSeatNumber(),
      booking.getBookingClass().name()
    );
  }
}
