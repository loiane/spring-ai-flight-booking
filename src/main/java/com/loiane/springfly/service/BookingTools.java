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

  @Tool(description = "Get booking details")
  public BookingDetails getBookingDetails(String bookingNumber, String firstName, String lastName) {
		try {
			return bookingService.getBookingDetails(bookingNumber, firstName, lastName);
		} catch (Exception _) {
			return new BookingDetails(bookingNumber, firstName, lastName, null, null,
					null, null, null, null);
		}
	}

  @Tool(description = "Change booking dates")
	public void changeBooking(String bookingNumber, String firstName, String lastName, String newDate, String from,
			String to) {
		bookingService.changeBooking(bookingNumber, firstName, lastName, newDate, from, to);
	}

  @Tool(description = "Cancel booking")
  public void cancelBooking(String bookingNumber, String firstName, String lastName) {
    bookingService.cancelBooking(bookingNumber, firstName, lastName);
  }
}
