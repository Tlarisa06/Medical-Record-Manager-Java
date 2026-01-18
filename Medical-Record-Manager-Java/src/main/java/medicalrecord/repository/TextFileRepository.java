package medicalrecord.repository;

import medicalrecord.domain.Entity;
import medicalrecord.exceptions.RepositoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class TextFileRepository<T extends Entity> extends InMemoryRepository<T> {
    private final Path path;
    private final Factory<T> factory;

    public TextFileRepository(Path path, Factory<T> factory) {
        this.path = path;
        this.factory = factory;
        load();
    }

    private void load() {
        if (!Files.exists(path)) return; // prima rulare: nu există încă fișierul
        try {
            for (String line : Files.readAllLines(path)) {
                if (line.isBlank()) continue;
                String[] tokens = line.split(";");
                //string->obiecte
                super.add(factory.fromTokens(tokens));
            }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citire din: " + path, e);
        }
    }
    private void persist() {
            try {
                if (path.getParent() != null)
                    Files.createDirectories(path.getParent());
                var lines = new ArrayList<String>();
                //obiecte->string
                for (T e : super.getAll()) {
                    String line = factory.toLine(e);
                    lines.add(line);
                }
                Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            } catch (IOException e) {
                throw new RepositoryException("Eroare la scriere în: " + path, e);
            }
        }

    // orice modificare în memorie e salvată imediat
    @Override
    public void add(T element) {
        super.add(element);
        persist();
    }

    @Override
    public void update(T newElement) {
        super.update(newElement);
        persist();
    }

    @Override
    public void remove(int id) {
        super.remove(id);
        persist();
    }


    @Override
    public void clear() {
        super.clear();
        persist();
    }
}
