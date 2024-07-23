package com.smit.tirechange.controller;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smit.tirechange.model.AvailableTime;
import com.smit.tirechange.model.CreateParams;
import com.smit.tirechange.model.SearchParams;
import com.smit.tirechange.service.TireChangeService;

@RestController
@RequestMapping("/api")
public class TireChangeController {

    private static final Logger logger = LoggerFactory.getLogger(TireChangeController.class);
    private final TireChangeService tireChangeService;

    public TireChangeController(TireChangeService tireChangeService) {
        this.tireChangeService = tireChangeService;
    }

    @GetMapping("/available-times")
    public ResponseEntity<Page<AvailableTime>> getAvailableTimes(
            @RequestParam String from,
            @RequestParam String until,
            @RequestParam int amount,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String vehicleType) {
        try {

            OffsetDateTime fromDate = OffsetDateTime.parse(from);
            OffsetDateTime untilDate = OffsetDateTime.parse(until);

            Page<AvailableTime> availableTimes = tireChangeService
                    .getAvailableTimes(new SearchParams(fromDate, untilDate, amount, page, size, city, vehicleType));

            logger.info("Returning {} available times with total pages {}", availableTimes.getContent().size(),
                    availableTimes.getTotalPages());

            return ResponseEntity.ok(availableTimes);
        } catch (Exception e) {
            logger.error("Error getting available times: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/available-times/{id}/booking")
    public ResponseEntity<String> bookTireChange(
            @PathVariable String id,
            @RequestParam String city,
            @RequestParam String contactInformation) {
        try {
            boolean success = tireChangeService.bookTireChangeTime(new CreateParams(id, city, contactInformation));

            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Booking successful");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking failed");
            }
        } catch (Exception e) {
            logger.error("Error booking tire change: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking failed: " + e.getMessage());
        }
    }
}
