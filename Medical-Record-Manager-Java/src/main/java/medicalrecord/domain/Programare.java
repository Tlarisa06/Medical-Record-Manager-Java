package medicalrecord.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Programare extends Entity  implements Serializable {
    private Pacient pacient;
    private Date data;
    private String scop;

    public Programare(int id, Pacient pacient, Date data, String scop) {
        super(id);
        this.pacient = pacient;
        this.data = data;
        this.scop = scop;
    }

    public Pacient getPacient() {
        return pacient;
    }

    public Date getData() {
        return data;
    }

    public String getScop() {
        return scop;
    }

    @Override
    public String toString() {
        // formatare data în formatul "dd.MM.yyyy HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        return "Programare: " +
                "id=" + getId() +
                ", pacient=" + pacient.getNume() + " " + pacient.getPrenume() +
                ", data=" + sdf.format(data) +
                ", scop='" + scop + '\'';
    }

}
