package medicalrecord.domain;

import java.io.Serializable;

public abstract class Entity  implements Serializable {
    private int id;

    Entity(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
}
