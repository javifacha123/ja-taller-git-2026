package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Un bloque de TNT: otra familia (no es una EntidadMinecraft), pero es
 * Explosivo. Explota con más potencia y más radio que un Creeper común y se
 * consume al detonar.
 *
 * <p>Entity: solo estado. No valida y no lanza excepciones.</p>
 */
@Entity
@Table(name = "bloque_tnt")
@JsonPropertyOrder({"id", "x", "z", "detonado", "puedeExplotar"})
public class BloqueTnt implements Explosivo {

    private static final int POTENCIA = 16;
    private static final int RADIO = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;
    private int z;
    private boolean detonado;

    /** Solo para JPA. Protegido: desde afuera la TNT se coloca con una posición. */
    protected BloqueTnt() {
    }

    public BloqueTnt(int x, int z) {
        this.x = x;
        this.z = z;
        this.detonado = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    public boolean isDetonado() {
        return detonado;
    }

    @Override
    public int potenciaExplosion() {
        return POTENCIA;
    }

    @Override
    public int radioExplosion() {
        return RADIO;
    }

    @Override
    @JsonProperty("puedeExplotar")
    public boolean puedeExplotar() {
        return !detonado;
    }

    /** Marca la TNT como detonada. El control lo hace el servicio. */
    @Override
    public void consumir() {
        this.detonado = true;
    }

    @Override
    public String nombreExplosivo() {
        return "TNT #" + id;
    }

    @Override
    public String tipoExplosivo() {
        return "tnt";
    }
}
