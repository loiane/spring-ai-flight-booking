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
		} catch (Exception e) {
			return new BookingDetails(bookingNumber, firstName, lastName, null, null,
					null, null, null, "ERROR: " + e.getMessage());
		}
	}

  @Tool(description = "Change ONLY the flight date for an existing booking. Use this when the customer wants to fly on a different date but keep the same route. The customer should be informed of the change fee based on their booking class before calling this tool. Returns confirmation with the updated booking details.")
  public String changeFlightDate(String bookingNumber, String firstName, String lastName, String newDate) {
    try {
      bookingService.changeFlightDate(bookingNumber, firstName, lastName, newDate);
      BookingDetails updated = bookingService.getBookingDetails(bookingNumber, firstName, lastName);
      return String.format("SUCCESS: Flight date changed to %s. Updated booking: %s -> %s on %s, Status: %s",
          newDate, updated.from(), updated.to(), updated.date(), updated.bookingStatus());
    } catch (Exception e) {
      return "FAILED: Could not change flight date. Reason: " + e.getMessage();
    }
  }

  @Tool(description = "Change ONLY the flight route (origin and/or destination) for an existing booking. Use this when the customer wants to change where they are flying from or to, but keep the same date. The customer should be informed of the change fee based on their booking class before calling this tool. Returns confirmation with the updated booking details.")
  public String changeFlightRoute(String bookingNumber, String firstName, String lastName, String from, String to) {
    try {
      bookingService.changeFlightRoute(bookingNumber, firstName, lastName, from, to);
      BookingDetails updated = bookingService.getBookingDetails(bookingNumber, firstName, lastName);
      return String.format("SUCCESS: Flight route changed to %s -> %s. Updated booking: %s on %s, Status: %s",
          from, to, updated.from() + " -> " + updated.to(), updated.date(), updated.bookingStatus());
    } catch (Exception e) {
      return "FAILED: Could not change flight route. Reason: " + e.getMessage();
    }
  }

  @Tool(description = "Change both the flight date AND route for an existing booking. Use this ONLY when the customer explicitly wants to change both the date and the route. The customer should be informed of the change fee based on their booking class before calling this tool. Returns confirmation with the updated booking details.")
	public String changeBooking(String bookingNumber, String firstName, String lastName, String newDate, String from,
			String to) {
    try {
		  bookingService.changeBooking(bookingNumber, firstName, lastName, newDate, from, to);
      BookingDetails updated = bookingService.getBookingDetails(bookingNumber, firstName, lastName);
      return String.format("SUCCESS: Booking changed. New flight: %s -> %s on %s, Status: %s",
          updated.from(), updated.to(), updated.date(), updated.bookingStatus());
    } catch (Exception e) {
      return "FAILED: Could not change booking. Reason: " + e.getMessage();
    }
	}

  @Tool(description = "Cancel an existing booking. The customer should be informed of the cancellation fee based on their booking class and must confirm before calling this tool. Cancellations must be made at least 48 hours before departure. Returns confirmation of the cancellation.")
  public String cancelBooking(String bookingNumber, String firstName, String lastName) {
    try {
      bookingService.cancelBooking(bookingNumber, firstName, lastName);
      return String.format("SUCCESS: Booking %s has been cancelled.", bookingNumber);
    } catch (Exception e) {
      return "FAILED: Could not cancel booking. Reason: " + e.getMessage();
    }
  }
}
