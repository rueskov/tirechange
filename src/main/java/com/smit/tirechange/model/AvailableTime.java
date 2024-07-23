package com.smit.tirechange.model;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailableTime {
    public String id;
    public OffsetDateTime time;
    public String city;
    public List<String> supportedVehicles;
    public String address;
}
