package com.loiane.springfly.controller;

import com.loiane.springfly.model.Booking;
import com.loiane.springfly.model.Passenger;
import com.loiane.springfly.model.SpringFlyDB;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final SpringFlyDB springFlyDB;

    public DataController(SpringFlyDB springFlyDB) {
        this.springFlyDB = springFlyDB;
    }

    /**
     * Get all passengers
     */
    @GetMapping("/passengers")
    public List<Passenger> getAllPassengers() {
        return springFlyDB.getPassengers();
    }

    /**
     * Get all bookings
     */
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return springFlyDB.getBookings();
    }
}
