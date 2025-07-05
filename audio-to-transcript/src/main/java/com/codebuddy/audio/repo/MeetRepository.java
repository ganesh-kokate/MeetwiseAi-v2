package com.codebuddy.audio.repo;

import com.codebuddy.audio.model.MeetData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeetRepository extends MongoRepository<MeetData,String> {
}
