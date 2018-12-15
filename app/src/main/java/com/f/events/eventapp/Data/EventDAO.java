package com.f.events.eventapp.Data;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class EventDAO {
    private LatLng position;
    private String name;
    private String description;
    private Date eventTime;
    private List<Integer> participants;
    private Uri eventPhoto;

    public EventDAO(LatLng position, String name, String description, Date eventTime,
                    List<Integer> participants, Uri eventPhoto) {
        this.position = position;
        this.name = name;
        this.description = description;
        this.eventTime = eventTime;
        this.participants = participants;
        this.eventPhoto = eventPhoto;
    }

    public EventDAO() {
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
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

    public List<Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Integer> participants) {
        this.participants = participants;
    }

    public Uri getEventPhoto() {
        return eventPhoto;
    }

    public void setEventPhoto(Uri eventPhoto) {
        this.eventPhoto = eventPhoto;
    }
}
