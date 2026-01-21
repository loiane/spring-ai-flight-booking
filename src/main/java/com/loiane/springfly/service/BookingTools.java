package com.loiane.springfly.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.loiane.springfly.model.BookingDetails;

@Service
public class BookingTools {
  
	private final BookingService bookingService;

  public BookingTools(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @Tool(description = "Retrieve booking details for a customer. Returns flight information including date, origin, destination, seat number, booking class, and status. Use this tool first before any booking modifications.")
  public BookingDetails getBookingDetails(String bookingNumber, String firstName, String lastName) {
		try {
			return bookingService.getBookingDetails(bookingNumber, firstName, lastName);
		} catch (Exception _) {
			return new BookingDetails(bookingNumber, firstName, lastName, null, null,
					null, null, null, null);
		}
	}

  @Tool(description = "Change an existing booking's flight date and/or route. The customer should be informed of the change fee based on their booking class before calling this tool. Parameters: bookingNumber, firstName, lastName, newDate (format: YYYY-MM-DD), from (departure airport), to (arrival airport).")
	public void changeBooking(String bookingNumber, String firstName, String lastName, String newDate, String from,
			String to) {
		bookingService.changeBooking(bookingNumber, firstName, lastName, newDate, from, to);
	}

  @Tool(description = "Cancel an existing booking. The customer should be informed of the cancellation fee based on their booking class and must confirm before calling this tool. Cancellations must be made at least 48 hours before departure.")
  public void cancelBooking(String bookingNumber, String firstName, String lastName) {
    bookingService.cancelBooking(bookingNumber, firstName, lastName);
  }
}
