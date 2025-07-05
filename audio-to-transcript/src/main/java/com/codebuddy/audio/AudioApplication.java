package com.codebuddy.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.codebuddy.audio")
public class AudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioApplication.class, args);
	}

}
