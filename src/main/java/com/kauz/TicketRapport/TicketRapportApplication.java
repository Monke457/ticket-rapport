package com.kauz.TicketRapport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A ticketing app for internal use at Kauz Informatik.
 * The app can be used by employees to create tickets for work tasks and assign them to learners at Kauz.
 * With this app it should be easy to track what learners are working on and keep an eye on their progress.
 *
 * @author Beth Williams
 * @version 1.0
 * @since 15.01.2024
 */
@SpringBootApplication
public class TicketRapportApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketRapportApplication.class, args);
	}

}
