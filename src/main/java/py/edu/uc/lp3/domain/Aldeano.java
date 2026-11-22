package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * El Aldeano no explota: solo recibe el daño de las explosiones.
 * Entity: solo estado y su propia descripción.
 */
@Entity
@DiscriminatorValue("ALDEANO")
public class Aldeano extends EntidadMinecraft {

    /** Solo para JPA. */
    protected Aldeano() {
    }

    /** Constructor simple: vida por defecto, aparece en el origen. */
    public Aldeano(String nombre) {
        this(nombre, VIDA_POR_DEFECTO);
    }

    public Aldeano(String nombre, int vida) {
        this(nombre, vida, 0, 0);
    }

    /**
     * Constructor completo: el único que llama a {@code super(...)}. Los otros
     * dos delegan en él con {@code this(...)}.
     */
    public Aldeano(String nombre, int vida, int x, int z) {
        super(nombre, vida, x, z);
    }

    @Override
    public String moverse() {
        return "El Aldeano camina hacia la aldea.";
    }
}
