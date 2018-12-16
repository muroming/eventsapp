package com.f.events.eventapp.Presentation.ParticipantsFragment;

public class Participant {
    private String name;
    private String imageUri;

    public Participant(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public Participant() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
