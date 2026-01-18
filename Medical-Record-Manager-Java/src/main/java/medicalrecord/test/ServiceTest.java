package medicalrecord.test;

import medicalrecord.domain.Pacient;
import medicalrecord.domain.Programare;
import medicalrecord.exceptions.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import medicalrecord.repository.IRepo;
import medicalrecord.repository.InMemoryRepository;
import medicalrecord.service.CabinetService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class ServiceTest {

    private IRepo<Pacient> pacientRepo;
    private IRepo<Programare> programareRepo;
    private CabinetService service;

    @BeforeEach
    void setUp() {
        pacientRepo = new InMemoryRepository<>();
        programareRepo = new InMemoryRepository<>();
        service = new CabinetService(pacientRepo, programareRepo);
    }

    @Test
    void testAdaugaPacient() {
        service.adaugaPacient("Popescu", "Ana", 30);

        assertEquals(1, service.getPacienti().size());
        assertEquals("Popescu", service.getPacienti().get(0).getNume());
        assertEquals("Ana", service.getPacienti().get(0).getPrenume());
        assertEquals(30, service.getPacienti().get(0).getVarsta());
    }

    @Test
    void testAdaugaProgramare() {
        Pacient p = new Pacient(1, "Popescu", "Ana", 30);
        pacientRepo.add(p);

        Date data = new Date();
        service.adaugaProgramare(p, data, "Control");

        assertEquals(1, service.getProgramari().size());
        assertEquals("Control", service.getProgramari().get(0).getScop());
        assertEquals(p, service.getProgramari().get(0).getPacient());
    }

    @Test
    void testDeletePacient() throws ServiceException {
        Pacient p = new Pacient(1, "Popescu", "Ana", 30);
        pacientRepo.add(p);

        service.deletePacient(1);

        assertEquals(0, service.getPacienti().size());
    }

    @Test
    void testDeletePacientThrowsIfNotExists() {
        assertThrows(ServiceException.class, () -> service.deletePacient(999));
    }

    @Test
    void testDeleteProgramare() throws ServiceException {
        Pacient p = new Pacient(1, "Popescu", "Ana", 30);
        pacientRepo.add(p);

        Programare pr = new Programare(10, p, new Date(), "Consultatie");
        programareRepo.add(pr);

        service.deleteProgramare(10);

        assertEquals(0, service.getProgramari().size());
    }

    @Test
    void testDeleteProgramareThrowsIfNotExists() {
        assertThrows(ServiceException.class, () -> service.deleteProgramare(999));
    }

    @Test
    void testUpdatePacient() throws ServiceException {
        Pacient p = new Pacient(1, "Popescu", "Ana", 30);
        pacientRepo.add(p);

        service.updatePacient(1, "Ionescu", "Maria", 25);

        Pacient updated = service.getPacienti().get(0);
        assertEquals("Ionescu", updated.getNume());
        assertEquals("Maria", updated.getPrenume());
        assertEquals(25, updated.getVarsta());
    }

    @Test
    void testUpdatePacientThrowsIfNotExists() {
        assertThrows(ServiceException.class, () -> service.updatePacient(123, "x", "y", 10));
    }

    @Test
    void testUpdateProgramare() throws ServiceException {
        Pacient p = new Pacient(1, "Popescu", "Ana", 30);
        pacientRepo.add(p);

        Programare pr = new Programare(10, p, new Date(), "Initial");
        programareRepo.add(pr);

        Date newDate = new Date();
        service.updateProgramare(10, p, newDate, "Control nou");

        Programare updated = service.getProgramari().get(0);

        assertEquals("Control nou", updated.getScop());
        assertEquals(newDate, updated.getData());
    }

    @Test
    void testUpdateProgramareThrowsIfNotExists() {
        Pacient p = new Pacient(1, "Popescu", "Ana", 30);

        assertThrows(ServiceException.class,
                () -> service.updateProgramare(999, p, new Date(), "Control"));
    }
}
