package medicalrecord.test;
import medicalrecord.domain.Pacient;
import medicalrecord.domain.Programare;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class domainTest {
    @Test
    public void test_Pacient() {
        Pacient pacient=new Pacient(100,"Dominte","Ana",30);
        assertEquals(100,pacient.getId());
        assertEquals("Dominte", pacient.getNume());
        assertEquals("Ana", pacient.getPrenume());
        assertEquals(30,pacient.getVarsta());
    }
    @Test
    public void test_Programare() {
        Pacient pacient=new Pacient(100,"Dominte","Ana",30);
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.OCTOBER, 27, 9, 0);
        Programare programare=new Programare(101,pacient,cal.getTime(),"Control" );

        assertEquals(101,programare.getId());
        assertEquals(100,programare.getPacient().getId());
        assertEquals(cal.getTime(),programare.getData());
        assertEquals("Control",programare.getScop());

    }
}





