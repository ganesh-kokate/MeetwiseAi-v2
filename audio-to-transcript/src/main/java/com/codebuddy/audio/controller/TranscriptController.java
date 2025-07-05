package com.codebuddy.audio.controller;

import com.codebuddy.audio.model.MeetData;
import com.codebuddy.audio.service.TranscriptService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/audiototext")
@RequiredArgsConstructor
public class TranscriptController {

    @Autowired
    private TranscriptService transcriptService;
    private static final Logger logger = LoggerFactory.getLogger(TranscriptController.class);


    @PostMapping
    public ResponseEntity<String> uploadAudioFile(@RequestParam("file") MultipartFile file,@RequestParam("meetId") String meetId)
    {
        if(file==null || file.isEmpty())
        {
            logger.error("No file received or file is empty");
        }

        logger.info("File received: {}", file.getOriginalFilename());
        logger.info("Content type: {}", file.getContentType());

        transcriptService.audioToTranscript(file,meetId);


        return ResponseEntity.ok("Done");
    }

    @GetMapping
    public ResponseEntity<Optional<MeetData>> getTranscription(@RequestParam String meetId)
    {
        Optional<MeetData> meetData = transcriptService.getTranscript(meetId);
        return ResponseEntity.ok(meetData);
    }

}
