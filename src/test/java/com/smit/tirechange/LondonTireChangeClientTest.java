package com.smit.tirechange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.smit.tirechange.model.LondonTireCreateParams;
import com.smit.tirechange.model.LondonTireSearchParams;
import com.smit.tirechange.model.LondonTireTime;
import com.smit.tirechange.service.LondonTireChangeClient;

class LondonTireChangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LondonTireChangeClient londonTireChangeClient;

    @Value("${tire.london.api-url}")
    private String londonApiUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTimes() {
        LondonTireSearchParams params = new LondonTireSearchParams(
                OffsetDateTime.parse("2023-01-01T10:00:00Z"),
                OffsetDateTime.parse("2023-01-02T10:00:00Z"));

        LondonTireTime[] mockResponse = {
                new LondonTireTime("uuid1", OffsetDateTime.parse("2023-01-01T12:00:00Z")),
                new LondonTireTime("uuid2", OffsetDateTime.parse("2023-01-01T14:00:00Z"))
        };

        when(restTemplate.getForObject(any(String.class), eq(LondonTireTime[].class)))
                .thenReturn(mockResponse);

        List<LondonTireTime> result = londonTireChangeClient.getTimes(restTemplate, params, londonApiUrl);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("uuid1", result.get(0).getUuid());
        assertEquals("uuid2", result.get(1).getUuid());

        verify(restTemplate, times(1)).getForObject(any(String.class), eq(LondonTireTime[].class));
    }

    @Test
    void testBookTime() {
        LondonTireCreateParams createParams = new LondonTireCreateParams("uuid1", "test@example.com");

        String expectedUrl = londonApiUrl + "/uuid1/booking";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Collections.singletonList(MediaType.TEXT_XML));

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<london.tireChangeBookingRequest>\n" +
                "\t<contactInformation>test@example.com</contactInformation>\n" +
                "</london.tireChangeBookingRequest>";

        HttpEntity<String> requestEntity = new HttpEntity<>(xmlRequest, headers);

        ResponseEntity<String> mockResponse = new ResponseEntity<>("<response>success</response>",
                HttpStatus.OK);

        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.PUT),
                eq(requestEntity),
                eq(String.class))).thenReturn(mockResponse);

        boolean result = londonTireChangeClient.bookTime(restTemplate, createParams, londonApiUrl);

        assertTrue(result);
        verify(restTemplate, times(1)).exchange(
                eq(expectedUrl),
                eq(HttpMethod.PUT),
                eq(requestEntity),
                eq(String.class));
    }
}