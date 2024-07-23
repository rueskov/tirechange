package com.smit.tirechange.model;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManchesterTireTime {
    private String id;
    private OffsetDateTime time;
    private boolean available;
    private List<String> supportedVehicles;
    private String address;

}
