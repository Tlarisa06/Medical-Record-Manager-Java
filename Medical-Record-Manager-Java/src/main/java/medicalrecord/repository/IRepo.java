package medicalrecord.repository;

import medicalrecord.domain.Entity;

import java.util.ArrayList;

public interface IRepo<T extends Entity> {
    void add(T element);
    void remove(int id);
    void update(T element);
    T findById(int id);
    ArrayList<T> getAll();
    public void clear();
}
