package medicalrecord.repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import medicalrecord.exceptions.ObjectNotFoundException;
import medicalrecord.domain.*;
import medicalrecord.exceptions.RepositoryException;
import java.sql.*;

public class SqlRepository<T extends Entity> implements IRepo<T> {
    private final String url;
    private final Factory<T> factory;
    private final String tableName;

    public SqlRepository(String dbPath, String tableName, Factory<T> factory) {
        this.url = "jdbc:sqlite:" + dbPath;
        this.factory = factory;
        this.tableName = tableName;
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql;
        if (tableName.equalsIgnoreCase("pacient")) {
            sql = """
                    CREATE TABLE IF NOT EXISTS pacient(
                        id INTEGER PRIMARY KEY,
                        nume TEXT NOT NULL,
                        prenume TEXT NOT NULL,
                        varsta INTEGER NOT NULL
                    );
                    """;
        } else if (tableName.equalsIgnoreCase("programare")) {
            sql = """
                    CREATE TABLE IF NOT EXISTS programare(
                        id INTEGER PRIMARY KEY,
                        id_pac INTEGER NOT NULL,
                        data TEXT NOT NULL,
                        scop TEXT NOT NULL
                    );
                    """;
        } else {
            throw new RepositoryException("Tip tabel necunoscut: " + tableName);
        }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la crearea tabelei SQLite!", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    @Override
    public void add(T element) {
        String sql;
        if (element instanceof Pacient pacient) {
//            Pacient(int id, String nume, String prenume, int varsta)
            sql = "INSERT INTO pacient (id, nume, prenume, varsta) VALUES(?, ?, ?, ?)";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pacient.getId());
                ps.setString(2, pacient.getNume());
                ps.setString(3, pacient.getPrenume());
                ps.setInt(4, pacient.getVarsta());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Eroare la inserare Pacient in baza de date!", e);
            }
        } else if (element instanceof Programare programare) {
            sql = "INSERT INTO programare (id, id_pac, data, scop) VALUES(?, ?, ?, ?)";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, programare.getId());
                ps.setInt(2, programare.getPacient().getId());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dataFormatata = sdf.format(programare.getData());
                ps.setString(3, dataFormatata);

                ps.setString(4, programare.getScop());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Eroare la inserare Programare in baza de date!", e);
            }

        } else {
            throw new RepositoryException("Tip de entitate necunoscut!");
        }
    }

    @Override
    public void remove(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) throw new ObjectNotFoundException(id);
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la stergere!", e);
        }
    }


    @Override
    public T findById(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return factory.fromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la getById!", e);
        }
    }

    @Override
    public void update(T element) {
        String sql;

        if (element instanceof Pacient pacient) {
            sql = "UPDATE pacient SET nume=?, prenume=?, varsta=? WHERE id=?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, pacient.getNume());
                ps.setString(2, pacient.getPrenume());
                ps.setInt(3, pacient.getVarsta());
                ps.setInt(4, pacient.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Eroare la actualizare Pacient în baza de date!", e);
            }

        } else if (element instanceof Programare programare) {
            sql = "UPDATE programare SET id_pac=?, data=?, scop=? WHERE id=?";
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, programare.getPacient().getId());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String dataFormatata = sdf.format(programare.getData());
                ps.setString(2, dataFormatata);

                ps.setString(3, programare.getScop());
                ps.setInt(4, programare.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RepositoryException("Eroare la actualizare Programare în baza de date!", e);
            }

        } else {
            throw new RepositoryException("Tip de entitate necunoscut!");
        }
    }


    @Override
    public ArrayList<T> getAll() {
        ArrayList<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(factory.fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la citirea tuturor elementelor!", e);
        }
        return list;
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM " + tableName;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RepositoryException("Eroare la clear!", e);
        }
    }

    @Override
    public String toString() {
        return "SqlRepository{" + tableName + "@" + url + "}";
    }

}
