package com.loiane.springfly.service;

import com.loiane.springfly.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Order(2) // Run after DocumentIngestionService
public class DataInitializationService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    private final SpringFlyDB springFlyDB;
    private final Random random = new Random();

    public DataInitializationService(SpringFlyDB springFlyDB) {
        this.springFlyDB = springFlyDB;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting data initialization...");
        generateSampleData();
        logger.info("Data initialization completed. Generated {} passengers and {} bookings", 
                   springFlyDB.getPassengers().size(), springFlyDB.getBookings().size());
    }

    private void generateSampleData() {
        List<Passenger> passengers = generatePassengers();
        List<Booking> bookings = generateBookings(passengers);
        
        springFlyDB.setPassengers(passengers);
        springFlyDB.setBookings(bookings);
    }

    private List<Passenger> generatePassengers() {
        List<Passenger> passengers = new ArrayList<>();
        
        // Generate 10 different passengers
        passengers.add(new Passenger("John", "Smith"));
        passengers.add(new Passenger("Emily", "Johnson"));
        passengers.add(new Passenger("Michael", "Brown"));
        passengers.add(new Passenger("Sarah", "Davis"));
        passengers.add(new Passenger("David", "Wilson"));
        passengers.add(new Passenger("Jennifer", "Garcia"));
        passengers.add(new Passenger("Christopher", "Martinez"));
        passengers.add(new Passenger("Lisa", "Anderson"));
        passengers.add(new Passenger("Robert", "Taylor"));
        passengers.add(new Passenger("Amanda", "Thomas"));
        
        return passengers;
    }

    private List<Booking> generateBookings(List<Passenger> passengers) {
        List<Booking> bookings = new ArrayList<>();
        
        // Flight routes
        String[][] routes = {
            {"JFK", "LAX"},
            {"MIA", "ORD"},
            {"SFO", "SEA"},
            {"BOS", "ATL"},
            {"DEN", "PHX"},
            {"DFW", "LAS"},
            {"MCO", "DCA"},
            {"DTW", "MSP"},
            {"IAH", "SAN"},
            {"PHL", "TPA"}
        };

        BookingStatus[] statuses = BookingStatus.values();
        BookingClass[] classes = BookingClass.values();

        for (int i = 0; i < 10; i++) {
            Passenger passenger = passengers.get(i);
            String[] route = routes[i];
            
            // Generate booking dates (some in the past, some in the future)
            LocalDate bookingDate = LocalDate.now().minusDays(random.nextInt(30));
            LocalDate departureDate = bookingDate.plusDays((long) random.nextInt(60) + 1);
            
            // Generate booking number
            String bookingNumber = String.format("%04d", 1000 + i);
            
            // Generate seat number based on booking class
            BookingClass bookingClass = classes[random.nextInt(classes.length)];
            String seatNumber = generateSeatNumber(bookingClass);
            
            // Generate booking status (mostly confirmed)
            BookingStatus status = i < 8 ? BookingStatus.CONFIRMED : statuses[random.nextInt(statuses.length)];
            
            Booking booking = new Booking(
                bookingNumber,
                bookingDate,
                departureDate,
                passenger,
                route[0],
                route[1],
                status,
                seatNumber,
                bookingClass
            );
            
            bookings.add(booking);
        }
        
        return bookings;
    }

    private String generateSeatNumber(BookingClass bookingClass) {
        int row;
        char letter = (char) ('A' + random.nextInt(6)); // A-F
        
        switch (bookingClass) {
            case BUSINESS:
                row = 1 + random.nextInt(5); // Rows 1-5
                break;
            case PREMIUM_ECONOMY:
                row = 6 + random.nextInt(5); // Rows 6-10
                break;
            case ECONOMY:
            default:
                row = 11 + random.nextInt(20); // Rows 11-30
                break;
        }
        
        return row + String.valueOf(letter);
    }
}
