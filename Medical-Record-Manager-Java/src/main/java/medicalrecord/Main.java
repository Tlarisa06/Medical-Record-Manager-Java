package medicalrecord;

import medicalrecord.domain.*;
import medicalrecord.repository.*;
import medicalrecord.service.CabinetService;
import medicalrecord.ui.*;
import com.github.javafaker.Faker;
import javafx.application.Application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Main {

    private static void generateAndAddPacienti(CabinetService service, int count) {
        Faker faker = new Faker();
        System.out.println(" Generare si adaugare " + count + " pacienți aleatori...");

        try {
            for (int i = 0; i < count; i++) {
                String nume = faker.name().lastName();
                String prenume = faker.name().firstName();
                int varsta = faker.number().numberBetween(1, 120);

                service.adaugaPacient(nume, prenume, varsta);
            }
            System.out.println( count + " pacienți au fost adăugați cu succes");

        } catch (Exception e) {
            System.err.println("Eroare la generarea și adaugarea pacientilor " + e.getMessage());
        }
    }

    private static void generateAndAddProgramari(CabinetService service, int count) {
        Faker faker = new Faker();
        ArrayList<Pacient> pacientiExistenti = service.getPacienti();
        if (pacientiExistenti.isEmpty()) {
            System.out.println("Nu exista pacienti in baza de date pentru a genera programari ");
            return;
        }
        String[] scopuri = {
                "Control periodic", "Detartraj", "Plomba", "Extracție",
                "Consultaaie de urgența", "Albire dentara", "Tratament de canal",
                "Proteză dentară", "Radiografie"
        };

        try {
            for (int i = 0; i < count; i++) {
                int indexPacient = faker.number().numberBetween(0, pacientiExistenti.size());
                Pacient pacientAles = pacientiExistenti.get(indexPacient);

                Date dataAleatorie = faker.date().past(30, TimeUnit.DAYS);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataAleatorie);
                cal.set(Calendar.HOUR_OF_DAY, faker.number().numberBetween(8, 20));
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                String scopAles = scopuri[faker.number().numberBetween(0, scopuri.length)];

                service.adaugaProgramare(pacientAles, cal.getTime(), scopAles);
            }
            System.out.println( count + " programari au fost adaugate cu succes!");

        } catch (Exception e) {
            System.err.println("Eroare la generarea și adaugarea programarilor: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        // Citim setarile
        Properties props = new Properties();
        try {
            Path cfg = Path.of("src/main/java/medicalrecord/settings.properties");
            if (Files.exists(cfg)) {
                try (var in = Files.newInputStream(cfg)) {
                    props.load(in);
                }
            }
        } catch (Exception e) {
            System.out.println("Nu am putut citi settings.properties: " + e.getMessage());
            // continuam cu valori implicite (memory)
        }
        String repoType = props.getProperty("Repository", "memory").trim().toLowerCase();

        Path pacientiBin = Path.of(props.getProperty("PacientiBin", "src/main/java/medicalrecord/data/pacienti.bin"));
        Path programariBin = Path.of(props.getProperty("ProgramariBin", "src/main/java/medicalrecord/data/programari.bin"));

        Path pacientiTxt = Path.of(props.getProperty("PacientiTxt", "src/data/pacienti.txt"));
        Path programariTxt = Path.of(props.getProperty("ProgramariTxt", "src/data/programari.txt"));

        String  SQL = props.getProperty("BazaDeDate", "src/main/java/medicalrecord/data/serverSptital.sqlite");


//         Construim repo-urile pe baza setarilor
        IRepo<Pacient> pacientRepo = switch (repoType) {
            case "memory" -> new InMemoryRepository<>();
            case "binary" -> new BinaryFileRepository<>(pacientiBin);
            case "text" -> new TextFileRepository<>(pacientiTxt, new PacientFactory());
            case "sql"->new SqlRepository<>(SQL, "pacient", new PacientFactory());
            default -> throw new IllegalArgumentException("Tip repo necunoscut: " + repoType);

        };

        IRepo<Programare> programareRepo = switch (repoType) {
            case "memory" -> new InMemoryRepository<>();
            case "binary" -> new BinaryFileRepository<>(programariBin);
            case "text" -> new TextFileRepository<>(programariTxt, new ProgramareFactory(pacientRepo));
            case "sql" -> new SqlRepository<>(SQL, "programare", new ProgramareFactory(pacientRepo));
            default -> throw new IllegalArgumentException("Tip repo necunoscut: " + repoType);
        };

        pacientRepo.clear();
        programareRepo.clear();

        CabinetService service = new CabinetService(pacientRepo, programareRepo);

        if (repoType.equalsIgnoreCase("sql")) {
            generateAndAddPacienti(service, 100);
            generateAndAddProgramari(service, 100);
        } else {
            service.adaugaPacient("Popescu", "Ion", 30);
            service.adaugaPacient("Ionescu", "Maria", 25);
            service.adaugaPacient("Georgescu", "Andrei", 45);
            service.adaugaPacient("Dumitrescu", "Elena", 32);
            service.adaugaPacient("Marin", "Paul", 29);

            ArrayList<Pacient> pacients = service.getPacienti();
            if (pacients.size() >= 5) {
                Calendar cal = Calendar.getInstance();

                cal.set(2025, Calendar.OCTOBER, 27, 9, 0);
                service.adaugaProgramare(pacients.get(0), cal.getTime(), "Control");

                cal.set(2025, Calendar.OCTOBER, 27, 10, 0);
                service.adaugaProgramare(pacients.get(1), cal.getTime(), "Detartraj");

                cal.set(2025, Calendar.OCTOBER, 27, 11, 0);
                service.adaugaProgramare(pacients.get(2), cal.getTime(), "Plombă");

                cal.set(2025, Calendar.OCTOBER, 27, 12, 0);
                service.adaugaProgramare(pacients.get(3), cal.getTime(), "Albire");

                cal.set(2025, Calendar.OCTOBER, 27, 13, 0);
                service.adaugaProgramare(pacients.get(4), cal.getTime(), "Consultație");
            }
        }
        System.out.println("======================================");
        System.out.println(" TIP repository încărcat: " + repoType.toUpperCase());

        switch (repoType) {
            case "memory" -> {
                System.out.println("  -> Folosește repository IN MEMORIE (nu se salvează pe disc)");
            }
            case "binary" -> {
                System.out.println("  -> Repository BINARY");
                System.out.println("     PacientiBin: " + pacientiBin.toAbsolutePath());
                System.out.println("     ProgramariBin: " + programariBin.toAbsolutePath());
            }
            case "text" -> {
                System.out.println("  -> Repository TEXT");
                System.out.println("     PacientiTxt: " + pacientiTxt.toAbsolutePath());
                System.out.println("     ProgramariTxt: " + programariTxt.toAbsolutePath());
            }
            case "sql" -> {
                System.out.println("  -> Repository SQL");
                System.out.println("     PacientiTxt: " + pacientiTxt.toAbsolutePath());
            }
        }

        System.out.println("======================================");

        String uiType = props.getProperty("UI", "console").trim().toLowerCase();

        if (uiType.equals("javafx")) {
            System.out.println("  -> PORNESC INTERFAȚA GRAFICĂ (JavaFX)");
            HelloApplication.run(service);

        } else {
            System.out.println(" PORNESC INTERFAȚA CONSOLĂ");
            ConsoleUI ui = new ConsoleUI(service);
            ui.run();
        }
    }
}
