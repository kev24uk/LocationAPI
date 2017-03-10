package com.locationApi.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "location_data")
@NamedQueries({
        @NamedQuery(name = "com.locationApi.findAll",
                query = "select e from Location e "
                        + "ORDER BY date DESC"),
        @NamedQuery(name = "com.locationApi.findByName",
                query = "select e from Location e "
                        + "where e.name like :name "
                        + "ORDER BY date DESC"),
        @NamedQuery(name = "com.locationApi.findLastPos",
                query = "select e from Location e "
                        + "where e.name = :name "
                        + "ORDER BY date DESC")
})

public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "longitude")
    private Float longitude;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "date")
    private String date;

    public Location() {
        // Jackson deserialization
    }

    public Location(long id, String name, Float longitude, Float latitude, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.parse(date);
            this.name = name;
            this.longitude = longitude;
            this.latitude = latitude;
            this.date = date;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    @JsonIgnore
    public String getLog() {
        StringBuilder builder = new StringBuilder(System.lineSeparator());
        builder.append("name: ").append(name).append(System.lineSeparator());
        builder.append("longitude: ").append(longitude).append(System.lineSeparator());
        builder.append("latitude: ").append(latitude).append(System.lineSeparator());
        builder.append("date: ").append(date).append(System.lineSeparator());
        return builder.toString();
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public Float getLongitude() {
        return longitude;
    }

    @JsonProperty
    public Float getLatitude() {
        return latitude;
    }

    @JsonProperty
    public String getDate() {
        return date;
    }


}