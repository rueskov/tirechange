package com.smit.tirechange.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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

import com.smit.tirechange.model.LondonTireCreateParams;
import com.smit.tirechange.model.LondonTireSearchParams;
import com.smit.tirechange.model.LondonTireTime;

@Service
public class LondonTireChangeClient {

    private static final Logger logger = LoggerFactory.getLogger(LondonTireChangeClient.class);

    private static final String BOOKING_ENDPOINT_TEMPLATE = "/%s/booking";
    private static final String XML_REQUEST_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<london.tireChangeBookingRequest>\n" +
            "\t<contactInformation>%s</contactInformation>\n" +
            "</london.tireChangeBookingRequest>";

    public List<LondonTireTime> getTimes(RestTemplate restTemplate, LondonTireSearchParams londonTireSearchParams,
            String londonUrl) {
        OffsetDateTime modifiedFromDateTime = londonTireSearchParams.getFrom().withSecond(0).withNano(0);
        OffsetDateTime modifiedUntilDateTime = londonTireSearchParams.getUntil().plusDays(1).withSecond(0).withNano(0);

        String modifiedFromDateTimeString = modifiedFromDateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String modifiedUntilDateTimeString = modifiedUntilDateTime.toLocalDate()
                .format(DateTimeFormatter.ISO_LOCAL_DATE);

        String url = londonUrl + "/available" + "?from=" + modifiedFromDateTimeString + "&until="
                + modifiedUntilDateTimeString;

        logger.info("Fetching available times from URL: {}", url);
        try {
            List<LondonTireTime> londonTimes = Arrays.asList(restTemplate.getForObject(url, LondonTireTime[].class));
            return londonTimes;
        } catch (Exception e) {
            logger.error("Error fetching available times from URL: {}", url, e);
            return Collections.emptyList();
        }
    }

    public boolean bookTime(RestTemplate restTemplate, LondonTireCreateParams londonTireCreateParams,
            String londonApiUrl) {
        String url = String.format(londonApiUrl + BOOKING_ENDPOINT_TEMPLATE, londonTireCreateParams.getUuid());
        String xmlRequest = String.format(XML_REQUEST_TEMPLATE, londonTireCreateParams.getContactInformation());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setAccept(Collections.singletonList(MediaType.TEXT_XML));

        HttpEntity<String> requestEntity = new HttpEntity<>(xmlRequest, headers);

        logger.info("Booking time with URL: {}", url);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Error booking time with URL: {}", url, e);
            return false;
        }
    }
}
