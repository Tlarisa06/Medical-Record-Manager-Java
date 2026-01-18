package medicalrecord.service;

import medicalrecord.domain.IdGenerator;
import medicalrecord.domain.Pacient;
import medicalrecord.domain.Programare;
import medicalrecord.exceptions.RepositoryException;
import medicalrecord.exceptions.ServiceException;
import medicalrecord.repository.IRepo;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CabinetService {
    private IRepo<Pacient> pacientRepo;
    private IRepo<Programare> programareRepo;

    public CabinetService(IRepo<Pacient> pacientRepo, IRepo<Programare> programareRepo) {
        this.pacientRepo = pacientRepo;
        this.programareRepo = programareRepo;
    }

    public void deletePacient(int id) throws ServiceException {
        boolean ok = true;
        for(Pacient pacient : pacientRepo.getAll()){
            if(pacient.getId() == id){
                ok = false;
            }
        }
        if(ok){
            throw new ServiceException("Pacientul nu exista");
        }
        pacientRepo.remove(id);
    }

    public void deleteProgramare(int id) throws ServiceException {
        boolean ok = true;
        for(Programare prog : programareRepo.getAll()){
            if(prog.getId() == id){
                ok = false;
            }
        }
        if(ok){
            throw new ServiceException("Programarea nu exista");
        }
        programareRepo.remove(id);
    }

    public ArrayList<Pacient> getPacienti() {
        return pacientRepo.getAll();
    }

    public ArrayList<Programare> getProgramari() {
        return programareRepo.getAll();
    }

    public void adaugaPacient(String nume, String prenume, int varsta) {
        Pacient p = new Pacient(IdGenerator.generateId(), nume, prenume, varsta);
        pacientRepo.add(p);
    }

    public void adaugaProgramare( Pacient pacient, Date data, String scop) {
        Programare programare = new Programare(IdGenerator.generateId(), pacient, data, scop);
        programareRepo.add(programare);
    }
    public void updatePacient(int id,String nume, String prenume, int varsta) throws ServiceException {

        boolean ok = true;
        for(Pacient pacient : pacientRepo.getAll()){
            if(pacient.getId() == id){
                ok = false;
            }
        }
        if(ok){
            throw new ServiceException("Pacientul nu exista");
        }
        Pacient p = new Pacient(id, nume, prenume, varsta);
        try {
            pacientRepo.update(p);
        }
        catch(RepositoryException e){
            throw new ServiceException("Eroare la actualizare Music!", e);
        }
    }
    public void updateProgramare(int id,Pacient pacient, Date data, String scop) throws ServiceException {
        boolean ok = true;
        for(Programare prog : programareRepo.getAll()){
            if(prog.getId() == id){
                ok = false;
            }
        }
        if(ok){
            throw new ServiceException("Programarea nu exista");
        }

        Programare programare = new Programare(id, pacient, data, scop);
        programareRepo.update(programare);
    }


    private Pacient getPacientById(int id) {
        return pacientRepo.findById(id);
    }

    // 1. Numarul de programari pentru fiecare pacient (ordine descrescătoare)
    public List<Map<String, Object>> getNumarProgramariPerPacient() {
        // (id_pac , nr_programari)
        Map<Integer, Long> countPerPacientId = programareRepo.getAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPacient().getId(),
                        Collectors.counting()
                ));

        // (nume complet, numarul de programari)
        return countPerPacientId.entrySet().stream()
                .map(entry -> {
                    Pacient pacient = getPacientById(entry.getKey());
                    Map<String, Object> result = new HashMap<>();
                    if (pacient == null) {
                        result.put("Pacient", "ID necunoscut: " + entry.getKey());
                    } else {
                         result.put("Pacient", pacient.getNume() + " " + pacient.getPrenume() + " (Varsta: " + pacient.getVarsta() + ")");
                    }
                    result.put("NumarProgramari", entry.getValue());
                    return result;
                })
                // sortare descrescatoare
                .sorted((m1, m2) -> Long.compare((Long) m2.get("NumarProgramari"), (Long) m1.get("NumarProgramari")))
                .collect(Collectors.toList());
    }

    //  2. numarul total de programari pentru fiecare luna a anului (ordine descrescatoare)
    public List<Map<String, Object>> getNumarProgramariPerLunaSortatDupaNumar() {
        return calculeazaProgramariPerLuna();
    }

    // 3. numarul de zile trecute de la ultima programare (ordine descrescatoare)

    public List<Map<String, Object>> getZileTrecuteDeLaUltimaProgramare() {
        // ultima programare pentru fiecare pacient (data cea mai mare)
        //(pac_id, prog)
        Map<Integer, Programare> ultimaProgramarePerPacientId = programareRepo.getAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPacient().getId(),
                        Collectors.maxBy(Comparator.comparing(Programare::getData))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  //cheia
                        entry -> entry.getValue().get() //valoarea
                ));

        Date dataCurenta = new Date();

        // calcularea numarului de zile trecute
        return ultimaProgramarePerPacientId.values().stream()
                .map(ultimaProg -> {
                    Pacient pacient = ultimaProg.getPacient();
                    Date ultimaData = ultimaProg.getData();
                    long diffMillis = dataCurenta.getTime() - ultimaData.getTime();
                    // conversia în zile
                    long zileTrecute = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

                    Map<String, Object> result = new HashMap<>();
                    result.put("Pacient", pacient.getNume() + " " + pacient.getPrenume());
                    result.put("DataUltimaProgramare", ultimaData);
                    result.put("ZileTrecute", zileTrecute);
                    return result;
                })
                // 3. Sortarea descrescătoare după ZileTrecute
                .sorted((m1, m2) -> Long.compare((Long) m2.get("ZileTrecute"), (Long) m1.get("ZileTrecute")))
                .collect(Collectors.toList());
    }

    // 4. cele mai aglomerate luni ale anului (sortate descrescator)
    public List<Map<String, Object>> getCeleMaiAglomerateLuni() {
        return calculeazaProgramariPerLuna(); // descrescator dupa numar
    }

    private List<Map<String, Object>> calculeazaProgramariPerLuna() {
        String[] numeLuni = {"IANUARIE", "FEBRUARIE", "MARTIE", "APRILIE", "MAI", "IUNIE", "IULIE", "AUGUST", "SEPTEMBRIE", "OCTOMBRIE", "NOIEMBRIE", "DECEMBRIE"};

        // gruparea programarilor dupa luna (0-11) si numararea lor.
        // (luna, nr)
        Map<Integer, Long> countPerMonth = programareRepo.getAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getData().getMonth(), // cheia
                        Collectors.counting()        // valoarea
                ));

        // extragerea rezultatelor si maparea la numele lunii
        List<Map<String, Object>> results = countPerMonth.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("Luna", numeLuni[entry.getKey()]);
                    result.put("NumarProgramari", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());

        // sortarea
        results.sort((m1, m2) -> Long.compare((Long) m2.get("NumarProgramari"), (Long) m1.get("NumarProgramari")));
        return results;
    }
}