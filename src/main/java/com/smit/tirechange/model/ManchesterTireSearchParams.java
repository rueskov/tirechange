package com.smit.tirechange.model;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ManchesterTireSearchParams {
    OffsetDateTime from;

    public ManchesterTireSearchParams(OffsetDateTime from) {
        this.from = from;
    }
}