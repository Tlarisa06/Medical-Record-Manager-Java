package medicalrecord.test;

import medicalrecord.domain.Pacient;
import medicalrecord.domain.Programare;
import medicalrecord.exceptions.FactoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import medicalrecord.repository.InMemoryRepository;
import medicalrecord.repository.ProgramareFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProgramareFactoryTest {

    private ProgramareFactory factory;
    private InMemoryRepository<Pacient> pacientRepo;

    @BeforeEach
    void setup() {
        pacientRepo = new InMemoryRepository<>();
        pacientRepo.add(new Pacient(101, "Popescu", "Ana", 30));  // pacient existent
        factory = new ProgramareFactory(pacientRepo);
    }

    @Test
    void testFromTokensValid() {
        String[] tokens = {"102", "101", "20/11/2024 14:30:00", "Consultatie"};

        Programare programare = factory.fromTokens(tokens);

        assertEquals(102, programare.getId());
        assertEquals(101, programare.getPacient().getId());
        assertEquals("Popescu", programare.getPacient().getNume());
        assertEquals("Consultatie", programare.getScop());
    }

    @Test
    void testFromTokensThrowsIfPacientNotFound() {
        String[] tokens = {"102", "999", "20/11/2024 14:30:00", "Consultatie"};

        FactoryException ex =
                assertThrows(FactoryException.class, () -> factory.fromTokens(tokens));

        assertTrue(ex.getMessage().contains("nu există"));
    }

    @Test
    void testToLine() throws Exception {
        Pacient p = pacientRepo.findById(101);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date data = sdf.parse("20/11/2024 14:30:00");

        Programare programare = new Programare(102, p, data, "Control");

        String line = factory.toLine(programare);

        assertEquals("102;101;20/11/2024 14:30:00;Control", line);
    }
}
