package com.smit.tirechange.model;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchParams {
    OffsetDateTime from;
    OffsetDateTime until;
    int amount;
    int page;
    int size;
    String city;
    String vehicleType;

    public SearchParams(OffsetDateTime from, OffsetDateTime until, int amount, int page, int size, String city,
            String vehicleType) {
        this.from = from;
        this.until = until;
        this.until.withHour(23);
        this.until.withMinute(0);
        this.amount = amount;
        this.page = page;
        this.size = size;
        this.city = city;
        this.vehicleType = vehicleType;
    }
}
