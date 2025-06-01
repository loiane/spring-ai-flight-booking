package com.loiane.springfly.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpringFlyDB {
  
  private List<Passenger> passengers = new ArrayList<>();
  private List<Booking> bookings = new ArrayList<>();

  public List<Passenger> getPassengers() {
    return passengers;
  }
  public void setPassengers(List<Passenger> passengers) {
    this.passengers = passengers;
  }
  public List<Booking> getBookings() {
    return bookings;
  }
  public void setBookings(List<Booking> bookings) {
    this.bookings = bookings;
  }

}
