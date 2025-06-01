package com.loiane.springfly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.loiane.springfly.model.BookingDetails;
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
}
