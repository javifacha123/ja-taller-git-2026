package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ALDEANO")
public class Aldeano extends EntidadMinecraft {

    /** Solo para JPA. */
    protected Aldeano() {
    }

    public Aldeano(String nombre) {
        super(nombre);
    }

    public Aldeano(String nombre, int vida) {
        super(nombre, vida);
    }

    public Aldeano(String nombre, int vida, int x, int z) {
        super(nombre, vida, x, z);
    }

    @Override
    public String moverse() {
        return "El Aldeano camina hacia la aldea.";
    }
}
