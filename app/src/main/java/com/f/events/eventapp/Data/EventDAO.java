package com.f.events.eventapp.Data;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class EventDAO {
    private List<Double> position;
    private String name;
    private String description;
    private Date eventTime;
    private int category;
    private List<String> participants;
    private String placeName;

    public EventDAO(List<Double> position, String name, String description, Date eventTime, int category,
                    List<String> participants, String placeName) {
        this.position = position;
        this.name = name;
        this.description = description;
        this.eventTime = eventTime;
        this.category = category;
        this.participants = participants;
        this.placeName = placeName;
    }

    public EventDAO() {
    }

    public List<Double> getPosition() {
        return position;
    }

    public void setPosition(List<Double> position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public LatLng getLatLng(){
        return new LatLng(position.get(0), position.get(1));
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
