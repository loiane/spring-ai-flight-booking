package com.loiane.springfly.controller;

import com.loiane.springfly.model.BookingDetails;
import com.loiane.springfly.service.BookingService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class DataController {

    private final BookingService bookingService;

    public DataController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    
    @GetMapping
    public List<BookingDetails> getAllBookings() {
        return bookingService.getBookings();
    }
}
