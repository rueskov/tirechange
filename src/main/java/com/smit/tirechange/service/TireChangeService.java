package com.smit.tirechange.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.smit.tirechange.model.AvailableTime;
import com.smit.tirechange.model.CreateParams;
import com.smit.tirechange.model.LondonTireCreateParams;
import com.smit.tirechange.model.LondonTireSearchParams;
import com.smit.tirechange.model.LondonTireTime;
import com.smit.tirechange.model.ManchesterTireCreateParams;
import com.smit.tirechange.model.ManchesterTireSearchParams;
import com.smit.tirechange.model.ManchesterTireTime;
import com.smit.tirechange.model.SearchParams;
import org.springframework.beans.factory.annotation.Value;

@Service
public class TireChangeService {

    @Autowired
    public final LondonTireChangeClient londonTireChangeClient;

    @Autowired
    public final ManchesterTireChangeClient manchesterTireChangeClient;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${tire.london.address}")
    private String londonAddress;

    @Value("${tire.london.api-url}")
    private String londonApiUrl;

    @Value("${tire.london.supported-vehicles}")
    private String[] londonSupportedVehicles;

    @Value("${tire.manchester.address}")
    private String manchesterAddress;

    @Value("${tire.manchester.api-url}")
    private String manchesterApiUrl;

    @Value("${tire.manchester.supported-vehicles}")
    private String[] manchesterSupportedVehicles;

    public TireChangeService(LondonTireChangeClient londonTireChangeClient,
            ManchesterTireChangeClient manchesterTireChangeClient) {
        this.londonTireChangeClient = londonTireChangeClient;
        this.manchesterTireChangeClient = manchesterTireChangeClient;
    }

    public List<AvailableTime> loadLondonTimes(SearchParams searchParams) {

        if ("london".equals(searchParams.getCity()) == false && searchParams.getCity() != null ||
                searchParams.getVehicleType() != null
                        && !Arrays.asList(londonSupportedVehicles).contains(searchParams.getVehicleType())) {
            return List.of();
        }

        List<LondonTireTime> londonTimes = londonTireChangeClient.getTimes(restTemplate,
                new LondonTireSearchParams(searchParams.getFrom(), searchParams.getUntil()), londonApiUrl);

        return londonTimes.stream()
                .filter(londonTireTime -> londonTireTime.getTime().isAfter(searchParams.getFrom()))
                .map(londonTireTime -> AvailableTime.builder()
                        .id("london:" + londonTireTime.getUuid())
                        .city("London")
                        .time(londonTireTime.getTime())
                        .supportedVehicles(List.of(londonSupportedVehicles))
                        .address(londonAddress)
                        .build())
                .toList();

    }

    public List<AvailableTime> loadManchesterTimes(SearchParams searchParams) {

        if ("manchester".equals(searchParams.getCity()) == false && searchParams.getCity() != null ||
                searchParams.getVehicleType() != null
                        && !Arrays.asList(manchesterSupportedVehicles).contains(searchParams.getVehicleType())) {
            return List.of();
        }

        List<ManchesterTireTime> manchesterTimes = manchesterTireChangeClient.getTimes(restTemplate,
                new ManchesterTireSearchParams(searchParams.getFrom()), manchesterApiUrl);

        return manchesterTimes.stream()
                .filter(manchesterTireTime -> manchesterTireTime.isAvailable() &&
                        manchesterTireTime.getTime().isBefore(searchParams.getUntil()) &&
                        manchesterTireTime.getTime().isAfter(searchParams.getFrom()))
                .map(manchesterTireTime -> AvailableTime.builder()
                        .id("manchester:" + manchesterTireTime.getId())
                        .city("Manchester")
                        .time(manchesterTireTime.getTime())
                        .supportedVehicles(Arrays.asList(manchesterSupportedVehicles))
                        .address(manchesterAddress)
                        .build())
                .toList();
    }

    public Page<AvailableTime> getAvailableTimes(SearchParams searchParams) {

        List<AvailableTime> combinedTimes = new ArrayList<>();

        combinedTimes.addAll(loadLondonTimes(searchParams));
        combinedTimes.addAll(loadManchesterTimes(searchParams));

        Pageable pageable = PageRequest.of(searchParams.getPage(), searchParams.getSize());
        int start = Math.min((int) pageable.getOffset(), combinedTimes.size());
        int end = Math.min((start + pageable.getPageSize()), combinedTimes.size());
        List<AvailableTime> paginatedList = combinedTimes.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, combinedTimes.size());
    }

    public boolean bookTireChangeTime(CreateParams createParams) {

        if ("london".equals(createParams.getCity())) {
            return londonTireChangeClient.bookTime(restTemplate,
                    new LondonTireCreateParams(
                            createParams.getId()
                                    .substring(createParams.getId().indexOf("london:") + "london:".length()),
                            createParams.getContactInformation()),
                    londonApiUrl);
        } else if ("manchester".equals(createParams.getCity())) {
            return manchesterTireChangeClient.bookTime(restTemplate,
                    new ManchesterTireCreateParams(
                            createParams.getId()
                                    .substring(createParams.getId().indexOf("manchester:") + "manchester:".length()),
                            createParams.getContactInformation()),
                    manchesterApiUrl);
        }
        return true;
    }

}
