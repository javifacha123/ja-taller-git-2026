package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * El Enderman se teletransporta. Guarda cuántos bloques de teletransporte le
 * quedan; la regla que los gasta para esquivar una explosión vive en la capa
 * de servicios ({@code ReglaEsquiveEnderman}), no acá.
 *
 * <p>Entity: solo estado. No valida y no lanza excepciones.</p>
 */
@Entity
@DiscriminatorValue("ENDERMAN")
public class Enderman extends EntidadMinecraft {

    /** Vida que usa el constructor simple de un Enderman. */
    public static final int VIDA_ENDERMAN = 40;

    /** Bloques de teletransporte que trae un Enderman recién aparecido. */
    public static final int BLOQUES_POR_DEFECTO = 8;

    // Envoltorio (Integer) porque en SINGLE_TABLE las otras hijas dejan esta
    // columna en NULL.
    private Integer bloquesTeletransporte;

    /** Solo para JPA. */
    protected Enderman() {
    }

    /** Constructor simple: vida y bloques por defecto, en el origen. */
    public Enderman(String nombre) {
        this(nombre, VIDA_ENDERMAN);
    }

    public Enderman(String nombre, int vida) {
        this(nombre, vida, BLOQUES_POR_DEFECTO);
    }

    public Enderman(String nombre, int vida, int bloquesTeletransporte) {
        this(nombre, vida, bloquesTeletransporte, 0, 0);
    }

    /**
     * Constructor completo: el único que asigna el campo propio y el que llama
     * a {@code super(...)}. Los otros tres delegan en él con {@code this(...)}.
     */
    public Enderman(String nombre, int vida, int bloquesTeletransporte, int x, int z) {
        super(nombre, vida, x, z);
        this.bloquesTeletransporte = bloquesTeletransporte;
    }

    public int getBloquesTeletransporte() {
        return bloquesTeletransporte == null ? 0 : bloquesTeletransporte;
    }

    @Override
    public String moverse() {
        return "El Enderman se teletransporta " + getBloquesTeletransporte() + " bloques de distancia.";
    }

    // ---------------- Cambios de estado ----------------
    // Los llama el servicio, que ya validó.

    /** Deja la cantidad de bloques de teletransporte en el valor indicado. */
    public void fijarBloquesTeletransporte(int bloques) {
        this.bloquesTeletransporte = bloques;
    }

    /** Descuenta bloques de teletransporte (los que gastó al esquivar). */
    public void gastarBloques(int bloques) {
        this.bloquesTeletransporte = getBloquesTeletransporte() - bloques;
    }
}
