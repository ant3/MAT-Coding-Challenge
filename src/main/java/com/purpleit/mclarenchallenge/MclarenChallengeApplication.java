package com.purpleit.mclarenchallenge;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * This is the main entry point for the application.
 * The application uses Spring Integration to do the processing.
 */
@Slf4j
@SpringBootApplication
public class MclarenChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MclarenChallengeApplication.class, args);
	}
}
