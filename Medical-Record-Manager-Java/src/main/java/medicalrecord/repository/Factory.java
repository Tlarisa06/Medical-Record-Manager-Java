package medicalrecord.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Factory<T> {
    T fromTokens(String[] tokens); // linie (splitată) -> obiect
    String toLine(T entity);       // obiect -> linie
    T fromResultSet(ResultSet rs) throws SQLException; // ResultSet -> obiect
}
