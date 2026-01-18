package medicalrecord.repository;

import medicalrecord.domain.Pacient;
import medicalrecord.exceptions.FactoryException;
import medicalrecord.repository.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class PacientFactory implements Factory<Pacient> {
    private static final String DELIM =";";
    @Override
    public Pacient fromTokens(String[] t){
        try {
            // format: id;nume;prenume;varsta
            if (t == null || t.length < 4) {
                throw new FactoryException("Linie invalidă pentru Pacient: " + Arrays.toString(t));
            }
            int id = Integer.parseInt(t[0].trim());
            String nume = t[1].trim();
            String prenume = t[2].trim();
            int varsta = Integer.parseInt(t[3].trim());
            return new Pacient(id,nume,prenume,varsta);
        } catch (NumberFormatException e) {
            throw new FactoryException("Format numeric invalid în linie: " + Arrays.toString(t), e);
        }
    }
    @Override
    public String toLine(Pacient p) {
        return p.getId() + DELIM
                + safe(p.getNume()) + DELIM
                + safe(p.getPrenume())+ DELIM
                +p.getVarsta() ;
    }
//    Pacient(int id, String nume, String prenume, int varsta)
    @Override
    public Pacient fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nume = rs.getString("nume");
        String prenume = rs.getString("prenume");
        int varsta = rs.getInt("varsta");
        return new Pacient(id, nume, prenume, varsta);
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\n", " ").trim();
    }
}
