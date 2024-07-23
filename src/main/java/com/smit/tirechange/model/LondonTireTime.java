package com.smit.tirechange.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "availableTime")
public class LondonTireTime {
    private String uuid;
    private OffsetDateTime time;
    private String city;
    private String address;

    public LondonTireTime(String uuid, OffsetDateTime time) {
        this.uuid = uuid;
        this.time = time;
    }

    @XmlElement(name = "time")
    public OffsetDateTime getTime() {
        return time;
    }

    @XmlElement(name = "uuid")
    public String getUuid() {
        return uuid;
    }
}
