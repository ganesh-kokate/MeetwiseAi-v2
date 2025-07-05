package com.meetwise.summariser.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;


@Service
public class Services {

    private final OpenAiChatModel chatModel;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("classpath:/promts/promt.st")
    private Resource resource;



    public Services(OpenAiChatModel  chatModel)
    {
        this.chatModel= chatModel;
    }

    public Flux<String> summurize(String message)
    {
        PromptTemplate promptTemplate = new PromptTemplate(resource);
        Prompt prompt = promptTemplate.create(Map.of("this",message));
       // Flux<ChatResponse> responseFlux = chatModel.stream(prompt);
        return chatModel.stream(prompt).flatMap(response -> Flux.just(response.getResult().getOutput().getText()
        ));
    }

    public Mono<String> summurizeByMeetId(String meetID)
    {
        Mono<String> mono= webClientBuilder.build()
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .scheme("http")
                                .host("localhost")
                                .port(8282)
                                .path("/api/audiototext")
                                .queryParam("meetId", meetID)
                                .build()
                )
                .retrieve()
                .bodyToMono(String.class);



        ObjectMapper objectMapper = new ObjectMapper();
        Mono<String> transcriptMono = mono.map(json -> {
            try {
                JsonNode outer = objectMapper.readTree(json);
                String innerJsonString = outer.path("transcript").asText();


                // Step 2: Parse the nested transcript string
                JsonNode inner = objectMapper.readTree(innerJsonString);

                // Step 3: Safely extract transcript
                JsonNode channels = inner.path("results").path("channels");
                if (!channels.isArray() || channels.isEmpty()) {
                    return "Transcript not available (no channels)";
                }

                JsonNode alternatives = channels.get(0).path("alternatives");
                if (!alternatives.isArray() || alternatives.isEmpty()) {
                    return "Transcript not available (no alternatives)";
                }

                JsonNode transcriptNode = alternatives.get(0).path("transcript");
                if (transcriptNode.isMissingNode()) {
                    return "Transcript not found";
                }

                return transcriptNode.asText();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error parsing transcript";
            }
        });
        PromptTemplate promptTemplate = new PromptTemplate(resource);

        return transcriptMono.flatMap(transcript -> {
            Prompt prompt = promptTemplate.create(Map.of("this", transcript));

            return Mono.fromCallable(() -> {
                ChatResponse response = chatModel.call(prompt); // blocking
                return response.getResult().getOutput().getText();
            }).subscribeOn(Schedulers.boundedElastic());
        });
    }
}








//        PromptTemplate promptTemplate = new PromptTemplate(resource);
//        Prompt prompt = promptTemplate.create(Map.of("this",message));
//        // Flux<ChatResponse> responseFlux = chatModel.stream(prompt);
//        return chatModel.stream(prompt).flatMap(response -> Flux.just(response.getResult().getOutput().getText()
//        ));