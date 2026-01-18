package medicalrecord.repository;

import medicalrecord.domain.Pacient;
import medicalrecord.domain.Programare;
import medicalrecord.exceptions.FactoryException;
import medicalrecord.exceptions.ObjectNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ProgramareFactory implements Factory<Programare> {

    private static final String DELIM = ";";
    private IRepo<Pacient> pacientRepository;  // referință la repo de pacienți

    // Constructor prin care primesc repo-ul
    public ProgramareFactory(IRepo<Pacient> pacientRepository) {
        this.pacientRepository = pacientRepository;
    }

    @Override
    public Programare fromTokens(String[] t) {
        try {
            if (t == null || t.length < 4) {
                throw new FactoryException("Linie invalidă pentru Programare: " + Arrays.toString(t));
            }
            int id = Integer.parseInt(t[0].trim());
            int id_pac = Integer.parseInt(t[1].trim());
            // Caută pacientul după id în repository
            Pacient pacient = pacientRepository.findById(id_pac);
            if (pacient == null) {
                throw new FactoryException("Pacientul cu id " + id_pac + " nu există!");
            }
            String dataInput = t[2].trim();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date data;
            try {
                data = sdf.parse(dataInput);
            } catch (ParseException e) {
                throw new FactoryException("Format invalid pentru dată: " + dataInput);
            }
            String scop = t[3].trim();
            return new Programare(id, pacient, data, scop);

        } catch (NumberFormatException e) {
            throw new FactoryException("Format numeric invalid în linie: " + Arrays.toString(t), e);
        }
    }

    @Override
    public String toLine(Programare p) {
        return p.getId() + DELIM
                + p.getPacient().getId() + DELIM
                + dateToString(p.getData()) + DELIM
                + safe(p.getScop());
    }

//    Programare(int id, Pacient pacient, Date data, String scop)
    @Override
    public Programare fromResultSet(ResultSet rs) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int id = rs.getInt("id");
        int idPacient = rs.getInt("id_pac");
        Pacient pacient = pacientRepository.findById(idPacient);

        String dataString = rs.getString("data");
        Date data = null;
        try {
            data = sdf.parse(dataString);
        } catch (ParseException e) {
            throw new SQLException("Eroare la parsarea datei din baza de date: " + dataString, e);
        }

        String scop = rs.getString("scop");

        if (pacient == null) {
            throw new FactoryException("Format invalid" );
        }
        return new Programare(id, pacient, data, scop);
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\n", " ").trim();
    }

    private static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }
}
