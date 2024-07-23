package com.smit.tirechange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import com.smit.tirechange.model.ManchesterTireSearchParams;
import com.smit.tirechange.model.ManchesterTireTime;
import com.smit.tirechange.service.ManchesterTireChangeClient;

public class ManchesterTireChangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ManchesterTireChangeClient manchesterTireChangeClient;

    @Value("${tire.manchester.api-url}")
    private String manchesterApiUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTimes() {
        ManchesterTireSearchParams params = new ManchesterTireSearchParams(
                OffsetDateTime.parse("2023-01-01T10:00:00Z"));

        ManchesterTireTime[] mockResponse = {
                new ManchesterTireTime(),
                new ManchesterTireTime()
        };

        when(restTemplate.getForObject(any(String.class), eq(ManchesterTireTime[].class)))
                .thenReturn(mockResponse);

        List<ManchesterTireTime> result = manchesterTireChangeClient.getTimes(restTemplate, params,
                manchesterApiUrl);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(restTemplate, times(1)).getForObject(any(String.class), eq(ManchesterTireTime[].class));
    }
}
