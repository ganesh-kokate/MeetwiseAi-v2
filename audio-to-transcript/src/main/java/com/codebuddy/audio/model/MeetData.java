package com.codebuddy.audio.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Component
@Document(collection = "meetData")
public class MeetData {
    @Id
    private String id;
    private String transcript;

    public String getId() {
        return id;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    @Override
    public String toString() {
        return "MeetData{" +
                "id='" + id + '\'' +
                ", transcript='" + transcript + '\'' +
                '}';
    }
}
