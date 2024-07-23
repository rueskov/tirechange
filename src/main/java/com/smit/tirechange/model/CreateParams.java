package com.smit.tirechange.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateParams {
    String id;
    String uuid;
    String city;
    String contactInformation;

    public CreateParams(String id, String city, String contactInformation) {
        this.id = id;
        this.city = city;
        this.contactInformation = contactInformation;
    }
}