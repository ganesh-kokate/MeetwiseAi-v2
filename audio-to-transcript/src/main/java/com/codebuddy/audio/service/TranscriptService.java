package com.codebuddy.audio.service;

import com.codebuddy.audio.model.MeetData;
import com.codebuddy.audio.repo.MeetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;


@Service
public class TranscriptService {

    @Autowired
    private MeetRepository repository;
    @Autowired
    private  MeetData meetData;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebClient webClient;

    public void audioToTranscript(MultipartFile file,String meetId)
    {

        String transcript=webClient.post()
                .header("Content-Type", file.getContentType())
                .bodyValue(file.getResource())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        meetData.setTranscript(transcript);
        meetData.setId(meetId);
        repository.save(meetData);

    }

    public Optional<MeetData> getTranscript(String meetId) {
        Optional<MeetData> meetData1= repository.findById(meetId);
        System.out.println(meetData1);
        System.out.println("Connected to DB: " + mongoTemplate.getDb().getName());
        System.out.println("Collection exists? " + mongoTemplate.collectionExists("meetData"));
        return meetData1;
    }
}
