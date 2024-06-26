package ch.kauz.ticketrapport;

import ch.kauz.ticketrapport.models.*;
import ch.kauz.ticketrapport.models.helpers.RoleType;
import ch.kauz.ticketrapport.models.helpers.StatusType;
import ch.kauz.ticketrapport.models.templates.TemplateChecklist;
import ch.kauz.ticketrapport.models.templates.TemplateChecklistItems;
import ch.kauz.ticketrapport.models.templates.TemplateItem;
import ch.kauz.ticketrapport.services.base.DBServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Initial demo data seeder
 */
@Component
public class DataGenerator {

    /**
     * Generates data for roles, users, clients, statuses, tickets with checklists, and checklist templates with items.
     *  <p>
     *      If a user entry exists in the database, then it is assumed that the database is already populated and no further data is generated.
     *  </p>
     *
     * @param services the {@link DBServices} injection containing all repositories
     * @param encoder the {@link PasswordEncoder} injection
     * @return functional interface to be run in when the app starts
     */
    @Bean
    public CommandLineRunner seedData(DBServices services, PasswordEncoder encoder) {
        return args -> {
            if (services.getUserService().anyExists(User.class)) {
                return;
            }

            // Create Roles
            Role adminRole = new Role(RoleType.ADMIN.getDisplay());
            Role learnerRole = new Role(RoleType.LEARNER.getDisplay());
            services.getRoleService().create(Set.of(adminRole, learnerRole));

            // Create Users
            User admin = new User("Test", "Admin", "admin@ticketapp.ch", encoder.encode("Pass123!"), adminRole, Set.of());
            User learner1 = new User("Hans", "Muster", "hans@ticketapp.ch", encoder.encode("Pass123!"), learnerRole, Set.of());
            User learner2 = new User("Peter", "Miller", "peter@ticketapp.ch", encoder.encode("Pass123!"), learnerRole, Set.of());
            User learner3 = new User("Ludwig", "Linden", "ludwig@ticketapp.ch", encoder.encode("Pass123!"), learnerRole, Set.of());
            User learner4 = new User("Maria", "Schmidt", "maria@ticketapp.ch", encoder.encode("Pass123!"), learnerRole, Set.of());
            services.getUserService().create(Set.of(admin, learner1, learner2, learner3, learner4));

            // Create Statuses
            Status open = new Status(StatusType.OPEN.getDisplay());
            Status completed = new Status(StatusType.COMPLETED.getDisplay());
            Status closed = new Status(StatusType.CLOSED.getDisplay());
            services.getStatusService().create(Set.of(open, completed, closed));

            // Create Clients
            Client client1 = new Client("Kauz Informatik Medien");
            Client client2 = new Client("Unternehmen AG");
            Client client3 = new Client("Firma GmbH");
            Client client4 = new Client("Company International");
            services.getClientService().create(Set.of(client1, client2, client3, client4));

            // Create Checklist Templates
            TemplateChecklist checklist1 = new TemplateChecklist("Grundlegende Checkliste", Set.of());
            TemplateChecklist checklist2 = new TemplateChecklist("Arbeitsplatz einrichten", Set.of());
            TemplateChecklist checklist3 = new TemplateChecklist("Access Points installieren", Set.of());
            TemplateChecklist checklist4 = new TemplateChecklist("Laptops aufsetzen & ausliefern", Set.of());
            services.getTemplateChecklistService().create(Set.of(checklist1, checklist2, checklist3, checklist4));

            // Create Checklist Item Templates
            TemplateItem itemT1 = new TemplateItem("Der Arbeitsplatz ist sauber und ordentlich", Set.of());
            TemplateItem itemT2 = new TemplateItem("Die Hardware wurde gereinigt", Set.of());
            TemplateItem itemT3 = new TemplateItem("Es wurden Updates durchgeführt", Set.of());
            TemplateItem itemT4 = new TemplateItem("Benötigte Werkzeuge wurden am vorgesehenen Ort bereitgestellt", Set.of());
            TemplateItem itemT5 = new TemplateItem("Die Hardware des Kunden wird beschriftet und zurückgesandt", Set.of());

            TemplateItem itemT6 = new TemplateItem("Hardware besorgen & kontrollieren", Set.of());
            TemplateItem itemT7 = new TemplateItem("Wünsche von Kunden entgegennehmen", Set.of());
            TemplateItem itemT8 = new TemplateItem("Hardware installieren", Set.of());
            TemplateItem itemT9 = new TemplateItem("Kabel via Klett verkabeln", Set.of());
            TemplateItem itemT10 = new TemplateItem("Auf Stolperfallen achten", Set.of());
            TemplateItem itemT11 = new TemplateItem("Hardware testen", Set.of());
            TemplateItem itemT12 = new TemplateItem("Arbeitsplatz & Hardware reinigen", Set.of());
            TemplateItem itemT13 = new TemplateItem("Feedback von Kunden holen", Set.of());

            TemplateItem itemT14 = new TemplateItem("Vorgaben definieren", Set.of());
            TemplateItem itemT15 = new TemplateItem("Access Point in Betrieb nehmen", Set.of());
            TemplateItem itemT16 = new TemplateItem("Via IP auf den AP zugreifen", Set.of());
            TemplateItem itemT17 = new TemplateItem("AP nach Vorgaben konfigurieren", Set.of());
            TemplateItem itemT18 = new TemplateItem("Test-Szenario erstellen", Set.of());
            TemplateItem itemT19 = new TemplateItem("AP testen", Set.of());
            TemplateItem itemT20 = new TemplateItem("Kauz Kleber anbringen", Set.of());
            TemplateItem itemT21 = new TemplateItem("AP beim Kunden installieren", Set.of());
            TemplateItem itemT22 = new TemplateItem("Verbindung überprüfen", Set.of());

            TemplateItem itemT24 = new TemplateItem("Laptops auspacken", Set.of());
            TemplateItem itemT25 = new TemplateItem("Laptops an Strom nehmen", Set.of());
            TemplateItem itemT26 = new TemplateItem("Boot-Stick erstellen", Set.of());
            TemplateItem itemT27 = new TemplateItem("Laptops via Boot Stick aufsetzen", Set.of());
            TemplateItem itemT28 = new TemplateItem("Laptops testen", Set.of());
            TemplateItem itemT29 = new TemplateItem("Laptops reinigen", Set.of());
            TemplateItem itemT31 = new TemplateItem("Laptops ausliefern", Set.of());
            TemplateItem itemT32 = new TemplateItem("Mit Kunden zusammen Laptop einrichten", Set.of());

            List<TemplateItem> items1 = List.of(itemT1, itemT2, itemT3, itemT4, itemT5);
            List<TemplateItem> items2 = List.of(itemT6, itemT7, itemT8, itemT9, itemT10, itemT11, itemT12, itemT13);
            List<TemplateItem> items3 = List.of(itemT14, itemT15, itemT16, itemT17, itemT18, itemT19, itemT20, itemT21, itemT22, itemT13);
            List<TemplateItem> items4 = List.of(itemT24, itemT25, itemT26, itemT27, itemT28, itemT29, itemT20, itemT31, itemT32);

            // Create Checklist Template Items
            List<TemplateChecklistItems> templateItems = new ArrayList<>();
            for (int i = 0; i < items1.size(); i++) {
                templateItems.add(new TemplateChecklistItems(checklist1, items1.get(i), i + 1));
            }
            for (int i = 0; i < items2.size(); i++) {
                templateItems.add(new TemplateChecklistItems(checklist2, items2.get(i), i + 1));
            }
            for (int i = 0; i < items3.size(); i++) {
                templateItems.add(new TemplateChecklistItems(checklist3, items3.get(i), i + 1));
            }
            for (int i = 0; i < items4.size(); i++) {
                templateItems.add(new TemplateChecklistItems(checklist4, items4.get(i), i + 1));
            }
            services.getTemplateChecklistItemsService().create(templateItems);

            // Create Tickets
            Set<Ticket> tickets = Set.of(
                    new Ticket("VPN", "Internen VPN-Server einrichten und testen", "", "",
                            0, 0, learner1, open, client1,  new HashSet<>()),

                    new Ticket("Laptop reparieren", "Der Laptop hat Leistungsprobleme",
                            "Laptop im abgesicherten Modus laufen lassen. Antivirenscan durchgeführt. 5 Viren gefunden und entfernt. Habe Tests durchgeführt, um zu überprüfen, ob das Problem behoben wurde.",
                            "Antiviren-Scan",
                            3, 0, learner1, completed, client1,  new HashSet<>()),

                    new Ticket("Website-Bilder", "Ersetzen Sie alle PNG-Bilder durch webp",
                            "Alle Bilder von der Website heruntergeladen. In webp konvertiert. Habe die Bilder noch einmal hochgeladen. Originalbilder entfernt.",
                            "Konvertierung und erneutes Hochladen",
                            2, 30, learner1, closed, client2, new HashSet<>()),

                    new Ticket("Neugestaltung der Website", "Entwerfen Sie ein neues Design für die Firmenhomepage.",
                            "Recherchierte aktuelle Webdesign-Trends. Habe nach Inspiration gesucht. habe draw.io verwendet, um ein Modell zu erstellen.",
                            "Minimalistisch mit grossem Banner",
                            1, 45, learner2, closed, client2, new HashSet<>()),

                    new Ticket("App-Übersetzung", "Fügen Sie der Onboarding-App eine deutsche Übersetzung hinzu",
                            "Englische und deutsche Übersetzungsvariablen für den gesamten Text in der App hinzugefügt. Sitzungskonfiguration hinzugefügt, um das Gebietsschema abzurufen." +
                                    "Der gesamte Text wurde durch Verweise auf die entsprechende Sprachvariable ersetzt.",
                            "Hartcodierter Text durch Gebietsschemavariablen ersetzt.",
                            4, 55, learner4, closed, client2, new HashSet<>()),

                    new Ticket("Netzwerkkonfiguration",
                            "Richten Sie interne Netzwerkkontrollen und -abläufe ein",
                            "", "",
                            0, 0, learner2, open, client2, new HashSet<>()),

                    new Ticket("Frameworks testen", "Testen Sie drei Frontend-JavaScript-Frameworks",
                            "Aktuelle Frameworks recherchiert. Habe mich für drei entschieden. Habe die Frameworks getestet, indem ich eine einfache App erstellt habe. hat einen Bericht über den Vorgang verfasst.",
                            "Der Bericht wird auf OneDrive hochgeladen",
                            6, 30, learner2, completed, client2, new HashSet<>()),

                    new Ticket("Routing-Problem in der App", "Problem beim Routing beim Bearbeiten von Datenbankeinträgen",
                            "Habe die App ausgeführt und den Fehler untersucht. Online nach einer Lösung gesucht. Habe die Lösung angewendet. Habe das Ergebnis getestet.",
                            "Verweis auf die URL-Parameter hinzugefügt.",
                            1, 25, learner3, closed, client3, new HashSet<>()),

                    new Ticket("Sicherheitslücken in der App", "Beheben Sie die Sicherheitsprobleme.",
                            "Habe die App ausgeführt, um das Problem zu testen. Die Sicherheitskonfiguration wurde aktualisiert. Getesteter Zugriff mit verschiedenen Benutzerkonten.",
                            "Einschränkungen in der Sicherheitskonfigurationsdatei hinzugefügt.",
                            1, 0, learner3, closed, client3, new HashSet<>()),

                    new Ticket("Netzwerkverbindung", "Beheben Sie die Probleme beim Zugriff auf das interne Organisationsnetzwerk.",
                            "", "", 0, 0, learner3, open, client3, new HashSet<>()),

                    new Ticket("Installieren Sie neue Computer", "Fünf neue PCs, die mit Windows 11 installiert werden sollen",
                            "", "", 0, 0, learner4, open, client3, new HashSet<>()),

                    new Ticket("Asynchrone Abfragen", "Aktualisieren Sie alle Abfragen in der App",
                            "Habe die App ausgeführt, um das Problem zu testen. Die ORM-Abfragen wurden aktualisiert, sodass sie asynchron funktionieren. Alle Abfragen getestet.",
                            "Async und Await zu Abfragen hinzugefügt.",
                            0, 0, learner4, completed, client4, new HashSet<>()),

                    new Ticket("E-Mail-Fehler", "Beheben Sie die Probleme beim Senden von E-Mails.",
                            "Das Problem wurde getestet, indem versucht wurde, E-Mails vom Firmenkonto zu senden. Habe online nach Hilfe gesucht. Habe den PC neu gestartet. " +
                                    "Wieder mit dem Internet verbunden. Die Ports in der E-Mail-Kontokonfiguration wurden aktualisiert. Das Ergebnis wurde durch erfolgreiches Versenden einer E-Mail getestet.",
                            "Die Ports in der E-Mail-Konfiguration wurden geändert.",
                            2, 20, learner4, closed, client4, new HashSet<>()),

                    new Ticket("Neue Computer Installierung", "Fünf neue Laptops sollen mit Windows 11 installiert werden",
                            "", "", 0, 0, null, open,  client2,  new HashSet<>()),

                    new Ticket("Frameworks testen", "Testen Sie zwei ORM-Frameworks und erstellen Sie einen Bericht",
                            "","", 0, 0, null, open, client2,  new HashSet<>())
            );

            // Create ticket checklist items
            for (Ticket t : tickets) {
                if (t.getClient() == client1) {
                    for (int i = 0; i < items1.size(); i++) {
                        t.getChecklistItems().add(new ChecklistItem(i+1, items1.get(i).getDescription(), t.getStatus() != open, t));
                    }
                }
                if (t.getClient() == client2) {
                    for (int i = 0; i < items2.size(); i++) {
                        t.getChecklistItems().add(new ChecklistItem(i+1, items2.get(i).getDescription(),  t.getStatus() != open, t));
                    }
                }
                if (t.getClient() == client3) {
                    for (int i = 0; i < items3.size(); i++) {
                        t.getChecklistItems().add(new ChecklistItem(i+1, items3.get(i).getDescription(),  t.getStatus() != open, t));
                    }
                }
                if (t.getClient() == client4) {
                    for (int i = 0; i < items4.size(); i++) {
                        t.getChecklistItems().add(new ChecklistItem(i+1, items4.get(i).getDescription(),  t.getStatus() != open, t));
                    }
                }
            }
            services.getTicketService().create(tickets);
        };
    }
}
