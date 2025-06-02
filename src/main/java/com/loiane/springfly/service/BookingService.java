package com.loiane.springfly.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.loiane.springfly.model.Booking;
import com.loiane.springfly.model.BookingDetails;
import com.loiane.springfly.model.BookingStatus;
import com.loiane.springfly.model.SpringFlyDB;

@Service
public class BookingService {
  
  private final SpringFlyDB springFlyDB;

  public BookingService(SpringFlyDB springFlyDB) {
    this.springFlyDB = springFlyDB;
  }

  public List<BookingDetails> getBookings() {
		return springFlyDB.getBookings().stream().map(BookingDetails::new).toList();
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

  public BookingDetails getBookingDetails(String bookingNumber, String firstName, String lastName) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		return toBookingDetails(booking);
	}

  public void changeBooking(String bookingNumber, String firstName, String lastName, String newDate, String from,
			String to) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(1))) {
			throw new IllegalArgumentException("Booking cannot be changed within 24 hours of the start date.");
		}
		booking.setDate(LocalDate.parse(newDate));
		booking.setFrom(from);
		booking.setTo(to);
	}

  public void cancelBooking(String bookingNumber, String firstName, String lastName) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(2))) {
			throw new IllegalArgumentException("Booking cannot be cancelled within 48 hours of the start date.");
		}
		booking.setBookingStatus(BookingStatus.CANCELLED);
	}

  public BookingDetails toBookingDetails(Booking booking) {
    return new BookingDetails(booking);
  }

  public void changeSeat(String bookingNumber, String firstName, String lastName, String seatNumber) {
		var booking = findBooking(bookingNumber, firstName, lastName);
		booking.setSeatNumber(seatNumber);
	}
}
