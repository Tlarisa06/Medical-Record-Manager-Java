package medicalrecord.ui;

import medicalrecord.domain.*;
import medicalrecord.exceptions.MyException;
import medicalrecord.exceptions.ServiceException;
import medicalrecord.service.CabinetService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final CabinetService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(CabinetService service) {
        this.service = service;
    }

    public void run() throws MyException {
        while (true) {
            System.out.println("\n=== Cabinet Stomatologic ===");
            System.out.println("1. Adaugă pacient");
            System.out.println("2. Afișează pacienți");
            System.out.println("3. Adaugă programare");
            System.out.println("4. Afișează programări");
            System.out.println("5. Update pacient");
            System.out.println("6. Update programare");
            System.out.println("7. Sterge pacient");
            System.out.println("8. Sterge programare");
            System.out.println("9. Rapoarte statistice");
            System.out.println("0. Ieșire");
            System.out.print("Alege: ");

            int opt = -1;
            try {
                opt = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Intrare invalidă. Vă rugăm introduceți un număr.");
                continue;
            }

            switch (opt) {
                case 1 -> adaugaPacient();
                case 2 -> afiseazaPacienti();
                case 3 -> adaugaProgramare();
                case 4 -> afiseazaProgramari();
                case 5 -> updatePacient();
                case 6 -> updateProgramare();
                case 7 -> deletePacient();
                case 8 -> deletePorgramare();
                case 9 -> runRapoarte();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Opțiune invalidă.");
            }
        }
    }

    private void runRapoarte() {
        while (true) {
            System.out.println("\n=== Meniu Rapoarte Statistice ===");
            System.out.println("1. Numărul de programări pentru fiecare pacient (desc)");
            System.out.println("2. Numărul total de programări pentru fiecare lună (desc după număr)");
            System.out.println("3. Zile trecute de la ultima programare (desc după număr de zile)");
            System.out.println("4. Cele mai aglomerate luni ale anului (desc după număr)");
            System.out.println("0. Înapoi la meniul principal");
            System.out.print("Alege raportul: ");

            int opt = -1;
            try {
                opt = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Intrare invalidă. Vă rugăm introduceți un număr.");
                continue;
            }

            if (opt == 0) {
                return;
            }

            afiseazaRaport(opt);
        }
    }
    private void afiseazaRaport(int opt) {
        switch (opt) {
            case 1 -> {
                System.out.println("\n 1. Numarul de programari pentru fiecare pacient ");
                service.getNumarProgramariPerPacient().forEach(map ->
                        System.out.printf("Pacient: %s, Programări: %d%n", map.get("Pacient"), map.get("NumarProgramari"))
                );
            }
            case 2 -> {
                System.out.println("\n 2. Numarul total de programari pentru fiecare luna ");
                service.getNumarProgramariPerLunaSortatDupaNumar().forEach(map ->
                        System.out.printf("Luna: %s, Programări: %d%n", map.get("Luna"), map.get("NumarProgramari"))
                );
            }
            case 3 -> {
                System.out.println("\n 3. Zile trecute de la ultima programare ");
                service.getZileTrecuteDeLaUltimaProgramare().forEach(map ->
                        System.out.printf("Pacient: %s, Ultima programare: %s, Zile trecute: %d%n",
                                map.get("Pacient"),
                                new SimpleDateFormat("dd.MM.yyyy HH:mm").format((Date) map.get("DataUltimaProgramare")),
                                map.get("ZileTrecute")
                        )
                );
            }
            case 4 -> {
                System.out.println("\n 4. Cele mai aglomerate luni ale anului");
                service.getCeleMaiAglomerateLuni().forEach(map ->
                        System.out.printf("Luna: %s, Programări: %d%n", map.get("Luna"), map.get("NumarProgramari"))
                );
            }
            default -> System.out.println("Optiune de raport invalida.");
        }
    }

    private void deletePacient() throws ServiceException {
        System.out.print("ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            service.deletePacient(id);
        }
        catch(ServiceException e){
            System.out.println("Eroare: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID invalid.");
        }
    }

    private void deletePorgramare() throws ServiceException {
        System.out.print("ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            service.deleteProgramare(id);
        }
        catch(ServiceException e){
            System.out.println("Eroare: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID invalid.");
        }
    }

    private void updatePacient() throws ServiceException {
        try {
            System.out.print("ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Nume: ");
            String nume = scanner.nextLine();

            System.out.print("Prenume: ");
            String prenume = scanner.nextLine();

            System.out.print("Vârstă: ");
            int varsta = Integer.parseInt(scanner.nextLine());

            service.updatePacient(id, nume, prenume, varsta);
        }
        catch(ServiceException e){
            System.out.println("Eroare: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Eroare: Vârstă sau ID invalid.");
        }

    }

    private void adaugaPacient() {
        try {
            System.out.print("Nume: ");
            String nume = scanner.nextLine();

            System.out.print("Prenume: ");
            String prenume = scanner.nextLine();

            System.out.print("Vârstă: ");
            int varsta = Integer.parseInt(scanner.nextLine());

            service.adaugaPacient(nume, prenume, varsta);
        } catch (NumberFormatException e) {
            System.out.println("Eroare: Vârstă invalidă.");
        }
    }

    private void updateProgramare() throws ServiceException {
        try {
            System.out.print("ID Programare: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("ID pacient: ");
            int id_pac = Integer.parseInt(scanner.nextLine());
            Pacient pacient = service.getPacienti().stream()
                    .filter(p -> p.getId() == id_pac)
                    .findFirst()
                    .orElse(null);

            if (pacient == null) {
                System.out.println("Nu există niciun pacient cu ID-ul introdus: " + id_pac);
                return;
            }

            System.out.print("Introduceți data programării (format: dd.MM.yyyy HH:mm): ");
            String dataInput = scanner.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date data;
            try {
                data = sdf.parse(dataInput);
            } catch (ParseException e) {
                throw new ServiceException("Format invalid pentru dată: " + dataInput, e);
            }

            System.out.print("Scop: ");
            String scop = scanner.nextLine();

            service.updateProgramare(id, pacient, data, scop);
        }
        catch(ServiceException e){
            System.out.println("Eroare: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-ul Programării sau al Pacientului este invalid.");
        }

    }

    private void afiseazaPacienti() {
        service.getPacienti().forEach(System.out::println);
    }

    private void adaugaProgramare() throws ServiceException {
        try {
            System.out.print("ID pacient: ");
            int id_pac = Integer.parseInt(scanner.nextLine());
            Pacient pacient = service.getPacienti().stream()
                    .filter(p -> p.getId() == id_pac)
                    .findFirst()
                    .orElse(null);

            if (pacient == null) {
                System.out.println("Nu există niciun pacient cu ID-ul introdus: " + id_pac);
                return;
            }

            System.out.print("Introduceți data programării (format: dd.MM.yyyy HH:mm): ");
            String dataInput = scanner.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date data;
            try {
                data = sdf.parse(dataInput);
            } catch (ParseException e) {
                throw new ServiceException("Format invalid pentru dată: " + dataInput, e);
            }

            System.out.print("Scop: ");
            String scop = scanner.nextLine();

            service.adaugaProgramare( pacient, data, scop);
        }
        catch(ServiceException e){
            System.out.println("Eroare: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-ul Pacientului este invalid.");
        }
    }


    private void afiseazaProgramari() {
        service.getProgramari().forEach(System.out::println);
    }
}