package medicalrecord.test;

import medicalrecord.domain.Pacient;
import medicalrecord.exceptions.FactoryException;
import org.junit.jupiter.api.Test;
import medicalrecord.repository.PacientFactory;

import static org.junit.jupiter.api.Assertions.*;

class PacientFactoryTest {

    PacientFactory factory = new PacientFactory();

    @Test
    void testFromTokensValid() {
        String[] tokens = {"100", "Popescu", "Ana", "30"};

        Pacient pacient = factory.fromTokens(tokens);

        assertEquals(100, pacient.getId());
        assertEquals("Popescu", pacient.getNume());
        assertEquals("Ana", pacient.getPrenume());
        assertEquals(30, pacient.getVarsta());
    }

    @Test
    void testFromTokensThrowsWhenTooFewFields() {
        String[] tokens = {"100", "Popescu"}; // doar 2 elemente

        assertThrows(FactoryException.class, () -> factory.fromTokens(tokens));
    }

    @Test
    void testFromTokensThrowsWhenInvalidNumber() {
        String[] tokens = {"abc", "Popescu", "Ana", "30"};  // id invalid (nu e numeric)

        assertThrows(FactoryException.class, () -> factory.fromTokens(tokens));
    }

    @Test
    void testToLine() {
        Pacient p = new Pacient(100, "Popescu", "Ana", 30);

        String line = factory.toLine(p);

        assertEquals("100;Popescu;Ana;30", line);
    }

    @Test
    void testSafeString() {
        Pacient p = new Pacient(200, "Popescu ", " Ana ", 25);

        String line = factory.toLine(p);

        assertEquals("200;Popescu;Ana;25", line); // \n și spațiile sunt eliminate
    }
}
