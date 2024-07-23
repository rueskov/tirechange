package com.smit.tirechange.model;

import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TireTimeBooking {
    private String contactInformation;

    @XmlElement
    public String getContactInformation() {
        return contactInformation;
    }

}
