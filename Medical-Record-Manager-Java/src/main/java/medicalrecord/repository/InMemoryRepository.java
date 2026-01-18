package medicalrecord.repository;

import medicalrecord.domain.Entity;
import java.util.ArrayList;
import java.util.Iterator;

public class InMemoryRepository<T extends Entity> implements IRepo<T> {
    private ArrayList<T> elements;

    public InMemoryRepository() {
        elements = new ArrayList<>();
    }

    @Override
    public void add(T element) {
        elements.add(element);
    }

    @Override
    public void remove(int id) {
        Iterator<T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            T element = iterator.next();
            if (element.getId() == id) {
                iterator.remove();
                break;
            }
        }
    }


    @Override
    public void update(T element) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getId() == element.getId()) {
                elements.set(i, element);
                return;
            }
        }
    }

    @Override
    public T findById(int id) {
        for (T element : elements) {
            if (id == element.getId())
                return element;
        }
        return null;
    }

    @Override
    public ArrayList<T> getAll() {
        return elements;
    }

    @Override
    public void clear(){
        elements.clear();
    }
}
