package medicalrecord.test;

import medicalrecord.domain.Pacient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import medicalrecord.repository.InMemoryRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {
    InMemoryRepository <Pacient> repository=new InMemoryRepository<>();

    @BeforeEach
    public void setup() {
        repository.clear();
    }

    @Test
    public void testadd() {
        //pentru add
        Pacient p=new Pacient(100,"Popescu","Ana",30);
        repository.add(p);
        ArrayList<Pacient> list=repository.getAll();
        assertTrue(list.contains(p));
    }
    @Test
    public void testUpdate() {
        Pacient p=new Pacient(100,"Popescu","Ana",30);
        repository.add(p);
        Pacient p1=new Pacient(100,"AAA","BBB",50);
        repository.update(p1);
        ArrayList<Pacient> list1=repository.getAll();
        assertEquals(list1.get(0).getNume(),p1.getNume());
    }
    @Test
    public void testRemove() {
        Pacient p=new Pacient(100,"Popescu","Ana",30);
        repository.add(p);
        repository.remove(100);
        assertEquals(repository.getAll().size(), 0);
    }
    @Test
    public void testClear() {
        Pacient p1=new Pacient(100,"Popescu","Ana",30);
        Pacient p2=new Pacient(101,"Popescu","Ana",30);
        repository.add(p1);
        repository.add(p2);
        repository.clear();
        assertEquals(repository.getAll().size(), 0);
    }
    @Test
    public void testFind() {
        Pacient p=new Pacient(100,"Popescu","Ana",30);
        repository.add(p);
        assertEquals(repository.findById(100),p);
    }
    @Test
    public void testGetAll() {
        Pacient p=new Pacient(100,"Popescu","Ana",30);
        repository.add(p);
        ArrayList<Pacient> list = new ArrayList<>();
        list.add(p);
        assertEquals(repository.getAll(),list);
    }

}
