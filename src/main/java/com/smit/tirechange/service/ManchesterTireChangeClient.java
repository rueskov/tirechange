package com.smit.tirechange.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.smit.tirechange.model.ManchesterTireCreateParams;
import com.smit.tirechange.model.ManchesterTireSearchParams;
import com.smit.tirechange.model.ManchesterTireTime;

@Service
public class ManchesterTireChangeClient {

    private static final Logger logger = LoggerFactory.getLogger(ManchesterTireChangeClient.class);
    private static final String BOOKING_ENDPOINT_TEMPLATE = "/%s/booking";
    private static final String JSON_REQUEST_TEMPLATE = "{ \"contactInformation\": \"%s\" }";

    public List<ManchesterTireTime> getTimes(RestTemplate restTemplate,
            ManchesterTireSearchParams manchesterTireSearchParams,
            String manchesterApiUrl) {
        String url = manchesterApiUrl + "?from=" + manchesterTireSearchParams.getFrom().toLocalDate();

        logger.info("Fetching available times from URL: {}", url);
        try {
            List<ManchesterTireTime> manchesterTimes = Arrays
                    .asList(restTemplate.getForObject(url, ManchesterTireTime[].class));
            return manchesterTimes;
        } catch (Exception e) {
            logger.error("Error fetching available times from URL: {}", url, e);
            return Collections.emptyList();
        }
    }

    public boolean bookTime(RestTemplate restTemplate, ManchesterTireCreateParams manchesterTireCreateParams,
            String manchesterApiUrl) {
        String url = String.format(manchesterApiUrl + BOOKING_ENDPOINT_TEMPLATE, manchesterTireCreateParams.getId());
        String jsonRequest = String.format(JSON_REQUEST_TEMPLATE, manchesterTireCreateParams.getContactInformation());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

        logger.info("Booking time with URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Error booking time with URL: {}", url, e);
            return false;
        }
    }
}
