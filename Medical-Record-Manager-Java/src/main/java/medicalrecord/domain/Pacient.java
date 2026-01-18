package medicalrecord.domain;

import java.io.Serializable;

public class Pacient extends Entity  implements Serializable{
    private String nume;
    private String prenume;
    private int varsta;

    public Pacient(int id, String nume, String prenume, int varsta) {
        super(id);
        this.nume = nume;
        this.prenume = prenume;
        this.varsta = varsta;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public int getVarsta() {
        return varsta;
    }

    @Override
    public String toString() {
        return "Pacient: " +
                "id=" + getId() +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", varsta=" + varsta ;
    }
}
