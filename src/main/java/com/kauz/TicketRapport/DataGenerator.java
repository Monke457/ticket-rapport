package com.kauz.TicketRapport;

import com.kauz.TicketRapport.models.*;
import com.kauz.TicketRapport.models.templates.ChecklistItemTemplate;
import com.kauz.TicketRapport.models.templates.ChecklistTemplate;
import com.kauz.TicketRapport.services.DBServices;
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
    public CommandLineRunner seedData(DBServices DBServices,
                                      PasswordEncoder encoder) {
        return args -> {
            if (DBServices.getRoleService().anyExists(Role.class)) return;

            // create roles
            Role adminRole = new Role("ADMIN");
            Role learnerRole = new Role("LEARNER");
            DBServices.getRoleService().create(Set.of(adminRole, learnerRole));

            // create users
            User adminUser = new User("Test", "Admin", "admin@ticket-kauz.ch", encoder.encode("Pass123!"), adminRole);
            User learnerUser1 = new User("Test", "Learner", "learner@ticket-kauz.ch", encoder.encode("Pass123!"), learnerRole);
            User learnerUser2 = new User("John", "Smith", "john.smith@ticket-kauz.ch", encoder.encode("Pass123!"), learnerRole);
            User learnerUser3 = new User("Mary", "Jones", "mary.jones@ticket-kauz.ch", encoder.encode("Pass123!"), learnerRole);
            User learnerUser4 = new User("Tim", "Brown", "tim.brown@ticket-kauz.ch", encoder.encode("Pass123!"), learnerRole);
            DBServices.getUserService().create(Set.of(adminUser, learnerUser1, learnerUser2, learnerUser3, learnerUser4));

            // create statuses
            Status open = new Status("In Progress");
            Status complete = new Status("Completed");
            Status closed = new Status("Closed");
            DBServices.getStatusService().create(Set.of(open, complete, closed));

            // create clients
            Client client1 = new Client("Company Inc.");
            Client client2 = new Client("Firm & Co.");
            Client client3 = new Client("Undertakings AG");
            Client client4 = new Client("Kauz Informatik AG");
            DBServices.getClientService().create(Set.of(client1, client2, client3, client4));

            // create tickets
            Ticket ticket1 = new Ticket("VPN",
                    "Set up and test internal VPN server.",
                    "", "",
                    0, 0, learnerUser1, client1, open, new HashSet<>());

            Ticket ticket2 = new Ticket("Fix Laptop",
                    "Laptop has performance issues and shuts down unexpectedly.",
                    "Ran laptop in safe mode. Ran antivirus scan. Found and removed 5 viruses. Ran tests to check if the problem was fixed.",
                    "Antivirus scan.",
                    3, 0, learnerUser1, client1, complete, new HashSet<>());

            Ticket ticket3 = new Ticket("Website Images",
                    "Replace all png images on company site with webp.",
                    "Downloaded all images from site. Converted to webp. Re-uploaded the images. Removed original images.",
                    "Conversion and re-upload.",
                    2, 30, learnerUser1, client2, closed, new HashSet<>());

            Ticket ticket4 = new Ticket("Website Redesign",
                    "Draft a new design (wireframe mockups) for the company homepage. Content must be the same.",
                    "Researched current web design trends. Looked for inspiration. used draw.io to create a mock up.",
                    "Minimalist with large banner.",
                    1, 45, learnerUser2, client2, closed, new HashSet<>());

            Ticket ticket5 = new Ticket("Network Configuration",
                    "Set up an internal network controls and operations.",
                    "", "",
                    0, 0, learnerUser2, client2, open, new HashSet<>());

            Ticket ticket6 = new Ticket("Test Frameworks",
                    "Test three frontend JavaScript frameworks and compile a report.",
                    "Researched current frameworks. Decided on three. Tested the frameworks by making a simple app. wrote up a report of the process.",
                    "Report is uploaded to OneDrive",
                    6, 30, learnerUser2, client2, complete, new HashSet<>());

            Ticket ticket7 = new Ticket("Routing issue in App",
                    "Problem with routing when editing database entries in the app.",
                    "Ran the app and investigated the bug. Searched online for a solution. Applied the solution. Tested the result.",
                    "Added referer to the url parameters.",
                    1, 25, learnerUser3, client3, closed, new HashSet<>());

            Ticket ticket8 = new Ticket("Security bugs in App ",
                    "Fix the security problems allowing unauthenticated users into restricted areas.",
                    "Ran the app to test the problem. Updated the security configuration. Tested access with various user account.",
                    "Added restrictions in the security configuration file.",
                    1, 0, learnerUser3, client3, closed, new HashSet<>());

            Ticket ticket9 = new Ticket("Network connectivity issues",
                    "Fix the problems with accessing internal organization network.",
                    "", "",
                    0, 0, learnerUser3, client3, open, new HashSet<>());

            Ticket ticket10 = new Ticket("Install new computers",
                    "Five new pcs to be installed with windows 11.",
                    "", "",
                    0, 0, learnerUser4, client3, open, new HashSet<>());

            Ticket ticket11 = new Ticket("Asynchronous queries",
                    "Update all the queries in the app to function asynchronously.",
                    "Ran the app to test the issue. Updated the ORM queries to function asynchronously. Tested all queries.",
                    "Added async and await to queries.",
                    0, 0, learnerUser4, client4, complete, new HashSet<>());

            Ticket ticket12 = new Ticket("Email bugs",
                    "Fix the issues with sending emails.",
                    "Tested the issue by trying to send emails from company account. Looked online for help. Restarted the PC." +
                            " Reconnected to the internet. Updated the ports in the email account config. Tested the result by successfully sending an email.",
                    "Changed the ports in the email config.",
                    2, 20, learnerUser4, client4, closed, new HashSet<>());

            Ticket ticket13 = new Ticket("App Translation",
                    "Add German translation to on-boarding app.",
                    "Added english and german translation variables for all text in the app. Added session config to get locale. " +
                            "Replaced all text with references to the appropriate language variable.",
                    "Replaced hardcoded text with locale variables.",
                    4, 55, learnerUser4, client2, closed, new HashSet<>());

            Ticket ticket14 = new Ticket("Install new computers",
                    "Five new laptops to be installed with windows 11.",
                    "", "",
                    0, 0, null, client2, open, new HashSet<>());

            Ticket ticket15 = new Ticket("Test Frameworks",
                    "Test two ORM frameworks and compile a report.",
                    "","",
                    0, 0, null, client2, open, new HashSet<>());

            Set<Ticket> tickets = Set.of(ticket1, ticket2, ticket3, ticket4, ticket5, ticket6, ticket7, ticket8, ticket9, ticket10, ticket11, ticket12, ticket13, ticket14, ticket15);
            DBServices.getTicketService().create(tickets);

            // create checklist templates
            ChecklistTemplate template1 = new ChecklistTemplate("Setup workspace", new HashSet<>());
            ChecklistTemplate template2 = new ChecklistTemplate("Install access points", new HashSet<>());
            ChecklistTemplate template3 = new ChecklistTemplate("Set up and deliver laptops", new HashSet<>());
            ChecklistTemplate template4 = new ChecklistTemplate("Basic Check", new HashSet<>());
            DBServices.getChecklistTemplateService().create(Set.of(template1, template2, template3, template4));

            // create checklist item templates
            ChecklistItemTemplate itemT1 = new ChecklistItemTemplate("Obtain and check hardware", new HashSet<>());
            ChecklistItemTemplate itemT2 = new ChecklistItemTemplate("Accept requests from customers", new HashSet<>());
            ChecklistItemTemplate itemT3 = new ChecklistItemTemplate("Install hardware", new HashSet<>());
            ChecklistItemTemplate itemT4 = new ChecklistItemTemplate("Connect the cable using Velcro", new HashSet<>());
            ChecklistItemTemplate itemT5 = new ChecklistItemTemplate("Watch out for tripping hazards", new HashSet<>());
            ChecklistItemTemplate itemT6 = new ChecklistItemTemplate("Test hardware", new HashSet<>());
            ChecklistItemTemplate itemT7 = new ChecklistItemTemplate("Clean workplace & hardware", new HashSet<>());
            ChecklistItemTemplate itemT8 = new ChecklistItemTemplate("Get feedback from customers", new HashSet<>());

            ChecklistItemTemplate itemT9 = new ChecklistItemTemplate("Define specifications", new HashSet<>());
            ChecklistItemTemplate itemT10 = new ChecklistItemTemplate("Put the access point into operation", new HashSet<>());
            ChecklistItemTemplate itemT11 = new ChecklistItemTemplate("Access the AP via IP", new HashSet<>());
            ChecklistItemTemplate itemT12 = new ChecklistItemTemplate("Configure AP according to specifications", new HashSet<>());
            ChecklistItemTemplate itemT13 = new ChecklistItemTemplate("Create test scenario", new HashSet<>());
            ChecklistItemTemplate itemT14 = new ChecklistItemTemplate("Test AP", new HashSet<>());
            ChecklistItemTemplate itemT15 = new ChecklistItemTemplate("Apply Kauz glue", new HashSet<>());
            ChecklistItemTemplate itemT16 = new ChecklistItemTemplate("Install AP at customer's site", new HashSet<>());
            ChecklistItemTemplate itemT17 = new ChecklistItemTemplate("Check connection", new HashSet<>());

            ChecklistItemTemplate itemT18 = new ChecklistItemTemplate("Unpack laptops", new HashSet<>());
            ChecklistItemTemplate itemT19 = new ChecklistItemTemplate("Connect laptops to power", new HashSet<>());
            ChecklistItemTemplate itemT20 = new ChecklistItemTemplate("Create boot stick", new HashSet<>());
            ChecklistItemTemplate itemT21 = new ChecklistItemTemplate("Set up laptops via boot stick", new HashSet<>());
            ChecklistItemTemplate itemT22 = new ChecklistItemTemplate("Test laptops", new HashSet<>());
            ChecklistItemTemplate itemT23 = new ChecklistItemTemplate("Clean laptops", new HashSet<>());
            ChecklistItemTemplate itemT24 = new ChecklistItemTemplate("Deliver laptops", new HashSet<>());
            ChecklistItemTemplate itemT25 = new ChecklistItemTemplate("Set up laptop together with customers", new HashSet<>());


            ChecklistItemTemplate itemT26 = new ChecklistItemTemplate("Carry out any necessary updates", new HashSet<>());
            ChecklistItemTemplate itemT27 = new ChecklistItemTemplate("Required tools were provided at the designated location", new HashSet<>());
            ChecklistItemTemplate itemT28 = new ChecklistItemTemplate("Customer's hardware labeled and returned", new HashSet<>());

            DBServices.getChecklistItemTemplateService().create(Set.of(itemT1, itemT2, itemT3, itemT4, itemT5));

            // add item templates to checklist templates
            template1.setItems(Set.of(itemT1, itemT2, itemT3, itemT4, itemT5, itemT6, itemT7, itemT8));
            template2.setItems(Set.of(itemT9, itemT10, itemT11, itemT12, itemT13, itemT14, itemT15, itemT16, itemT17, itemT8));
            template3.setItems(Set.of(itemT18, itemT19, itemT20, itemT21, itemT22, itemT23, itemT15, itemT24, itemT25));
            template4.setItems(Set.of(itemT7, itemT26, itemT27, itemT28));
            DBServices.getChecklistTemplateService().update(Set.of(template1, template2, template3, template4));

            // create checklist items
            Set<ChecklistItem> checklistItems = new HashSet<>();
            for (Ticket t : tickets) {
                if (t.getClient() == client1) {
                    for (ChecklistItemTemplate itemTemplate : template1.getItems()) {
                        checklistItems.add(new ChecklistItem(itemTemplate.getDescription(), t.getStatus() != open, t));
                    }
                }
                if (t.getClient() == client2) {
                    for (ChecklistItemTemplate itemTemplate : template2.getItems()) {
                        checklistItems.add(new ChecklistItem(itemTemplate.getDescription(), t.getStatus() != open, t));
                    }
                }
                if (t.getClient() == client3) {
                    for (ChecklistItemTemplate itemTemplate : template3.getItems()) {
                        checklistItems.add(new ChecklistItem(itemTemplate.getDescription(), t.getStatus() != open, t));
                    }
                }
                for (ChecklistItemTemplate itemTemplate : template4.getItems()) {
                    checklistItems.add(new ChecklistItem(itemTemplate.getDescription(), t.getStatus() != open, t));
                }
            }
            DBServices.getChecklistItemService().create(checklistItems);
        };
    }
}
