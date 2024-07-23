package com.smit.tirechange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.smit.tirechange.controller.TireChangeController;
import com.smit.tirechange.model.CreateParams;
import com.smit.tirechange.service.TireChangeService;

class TireChangeControllerTest {

    @Mock
    private TireChangeService tireChangeService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TireChangeController tireChangeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookManchesterTireChangeSuccess() {
        String id = "manchester:id1";
        String city = "manchester";
        String contactInformation = "+1777777777";

        when(tireChangeService.bookTireChangeTime(any(CreateParams.class))).thenReturn(true);

        ResponseEntity<String> response = tireChangeController.bookTireChange(id, city, contactInformation);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Booking successful", response.getBody());
    }

    @Test
    void testBookManchesterTireChangeFailure() {
        String id = "manchester:id1";
        String city = "manchester";
        String contactInformation = "+1777777777";

        when(tireChangeService.bookTireChangeTime(any(CreateParams.class))).thenReturn(false);

        ResponseEntity<String> response = tireChangeController.bookTireChange(id, city, contactInformation);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Booking failed", response.getBody());
    }
}
