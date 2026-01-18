package medicalrecord.repository;

import medicalrecord.domain.Entity;
import medicalrecord.exceptions.RepositoryException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class BinaryFileRepository<T extends Entity> extends InMemoryRepository<T> {

    private final Path path;

    public BinaryFileRepository(Path path) {
        this.path = path;
        load();
    }

    private void load() {
        if (!Files.exists(path)) return;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            var list = (ArrayList<T>) in.readObject();
            super.clear();
            for (T e : list) super.add(e);
        } catch (IOException | ClassNotFoundException e) {
            throw new RepositoryException("Eroare la încărcare binară din: " + path, e);
        }
    }

    private void persist() {
        try {
            // asigură directorul
            if (path.getParent() != null)
                Files.createDirectories(path.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(
                    path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                out.writeObject(super.getAll());
            }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la salvare binară în: " + path, e);
        }
    }

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
