package com.kauz.TicketRapport;

import com.kauz.TicketRapport.models.*;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import com.kauz.TicketRapport.services.UnitOfWork;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * This component generates data and stores it in the database for development, testing, and demonstration purposes
 */
@Component
public class DataGenerator {

    @Bean
    public CommandLineRunner seedData(UnitOfWork unitOfWork,
                                      PasswordEncoder encoder) {
        return args -> {
            if (unitOfWork.getRoleService().anyExists(Role.class)) return;

            // create roles
            Role adminRole = new Role("ADMIN");
            Role learnerRole = new Role("LEARNER");
            unitOfWork.getRoleService().create(Set.of(adminRole, learnerRole));

            // create users
            User adminUser = new User("Test", "Admin", "admin@ticket-kauz.ch", encoder.encode("Pass123!"), adminRole);
            User learnerUser1 = new User("Test", "Learner", "learner@ticket-kauz.ch", encoder.encode("Pass123!"), learnerRole);
            User learnerUser2 = new User("John", "Smith", "john.smith@ticket-kauz.ch", encoder.encode("Pass123!"), learnerRole);
            unitOfWork.getUserService().create(Set.of(adminUser, learnerUser1, learnerUser2));

            // create statuses
            Status open = new Status("In Progress");
            Status complete = new Status("Completed");
            Status closed = new Status("Closed");
            unitOfWork.getStatusService().create(Set.of(open, complete, closed));

            // create clients
            Client client1 = new Client("Company Inc.");
            Client client2 = new Client("Firm & Co.");
            unitOfWork.getClientService().create(Set.of(client1, client2));

            // create tickets
            Ticket ticket1 = new Ticket("VPN", "Set up and test internal VPN server.",
                    "", "", 0, 0, learnerUser1, client1, open, new HashSet<>());
            Ticket ticket2 = new Ticket("Fix Laptop", "Laptop has performance issues and shuts down unexpectedly.",
                    "Ran laptop in safe mode. Ran antivirus scan. Found and removed 5 viruses. Ran tests to check if the problem was fixed.",
                    "Antivirus scan.", 3, 0, learnerUser1, client1, closed, new HashSet<>());
            Ticket ticket3 = new Ticket("Website Images", "Replace all png images on company site with webp.",
                    "Downloaded all images from site. Converted to webp. Re-uploaded the images. Removed original images.",
                    "Conversion and re-upload.",2, 30, learnerUser2, client2, closed, new HashSet<>());
            Ticket ticket4 = new Ticket("Website Redesign", "Draft a new design (wireframe mockups) for the company homepage. Content must be the same.",
                    "Researched current web design trends. Looked for inspiration. used draw.io to create a mock up.", "Minimalist with large banner.",
                    1, 45, learnerUser2, client2, complete, new HashSet<>());
            Ticket ticket5 = new Ticket("App Translation", "Add German translation to on-boarding app.",
                    "", "", 0, 0, learnerUser2, client2, open, new HashSet<>());
            Set<Ticket> tickets = Set.of(ticket1, ticket2, ticket3, ticket4, ticket5);
            unitOfWork.getTicketService().create(tickets);

            // create checklist items
            Set<ChecklistItem> checklistItems = new HashSet<>();
            for (Ticket t : tickets) {
                if (t.getClient() == client1) {
                    checklistItems.add(new ChecklistItem("The workplace is clean and tidy", t.getStatus() != open, t));
                    checklistItems.add(new ChecklistItem("The hardware has been cleaned", t.getStatus() != open, t));
                    checklistItems.add(new ChecklistItem("Updates have been carried out", t.getStatus() != open, t));
                    checklistItems.add(new ChecklistItem("Required tools were provided at the designated location", t.getStatus() != open, t));
                    checklistItems.add(new ChecklistItem("Customer's hardware labeled and returned", t.getStatus() != open, t));
                } else {
                    checklistItems.add(new ChecklistItem("The workplace is clean and tidy", t.getStatus() != open, t));
                    checklistItems.add(new ChecklistItem("The hardware has been cleaned", t.getStatus() != open, t));
                    checklistItems.add(new ChecklistItem("Updates have been carried out", t.getStatus() != open, t));
                }
            }
            unitOfWork.getChecklistItemService().create(checklistItems);

            // create checklist templates
            ChecklistTemplate template1 = new ChecklistTemplate("Full Check", new HashSet<>());
            ChecklistTemplate template2 = new ChecklistTemplate("Software Only", new HashSet<>());
            unitOfWork.getChecklistTemplateService().create(Set.of(template1, template2));

            // create checklist item templates
            ChecklistItemTemplate itemT1 = new ChecklistItemTemplate("The workplace is clean and tidy", new HashSet<>());
            ChecklistItemTemplate itemT2 = new ChecklistItemTemplate("The hardware has been cleaned", new HashSet<>());
            ChecklistItemTemplate itemT3 = new ChecklistItemTemplate("Updates have been carried out", new HashSet<>());
            ChecklistItemTemplate itemT4 = new ChecklistItemTemplate("Required tools were provided at the designated location", new HashSet<>());
            ChecklistItemTemplate itemT5 = new ChecklistItemTemplate("Customer's hardware labeled and returned", new HashSet<>());
            unitOfWork.getChecklistItemTemplateService().create(Set.of(itemT1, itemT2, itemT3, itemT4, itemT5));

            // add item templates to checklist templates
            template1.setItems(Set.of(itemT1, itemT2, itemT3, itemT4, itemT5));
            template2.setItems(Set.of(itemT1, itemT2, itemT3));
            unitOfWork.getChecklistTemplateService().update(Set.of(template1, template2));
        };
    }
}
