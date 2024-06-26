package ch.kauz.ticketrapport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A ticketing app for internal use at Kauz Informatik Medien AG.
 * <p>
 *     The app can be used by employees to create tickets for work tasks and assign them to learners at Kauz.
 *     The goal is to allow Admins to easily track what learners are working on and keep an eye on their progress.
 * </p>
 *
 * @author Beth Williams
 * @version 1.0
 * @since 27.03.2024
 */
@SpringBootApplication
public class TicketrapportApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketrapportApplication.class, args);
	}

}
