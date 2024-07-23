package com.smit.tirechange.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LondonTireSearchParams {
    OffsetDateTime from;
    OffsetDateTime until;

}
