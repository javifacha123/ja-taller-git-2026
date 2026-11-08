package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.exceptions.ExplosionImposibleException;

/**
 * El Creeper es una EntidadMinecraft ("es un") y ademas un Explosivo.
 * Un Creeper cargado (alcanzado por un rayo) explota con el doble de potencia y radio.
 */
@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends EntidadMinecraft implements Explosivo {

    private static final int POTENCIA = 12;
    private static final int RADIO = 3;

    // Envoltorios (Boolean) porque en SINGLE_TABLE las filas de Aldeano y Enderman dejan esta columna en NULL
    private Boolean cargado;
    private Boolean explotado;

    /** Solo para JPA. */
    protected Creeper() {
    }

    public Creeper(String nombre) {
        super(nombre);
        inicializar(false);
    }

    public Creeper(String nombre, int vida) {
        super(nombre, vida);
        inicializar(false);
    }

    public Creeper(String nombre, int vida, int x, int z) {
        this(nombre, vida, x, z, false);
    }

    public Creeper(String nombre, int vida, int x, int z, boolean cargado) {
        super(nombre, vida, x, z);
        inicializar(cargado);
    }

    private void inicializar(boolean estaCargado) {
        this.cargado = estaCargado;
        this.explotado = false;
    }

    public boolean isCargado() {
        return Boolean.TRUE.equals(cargado);
    }

    public boolean isExplotado() {
        return Boolean.TRUE.equals(explotado);
    }

    @Override
    public String moverse() {
        if (isExplotado()) {
            return "Del Creeper solo queda un cráter.";
        }
        return "El Creeper se acerca silenciosamente al jugador.";
    }

    // ---------------- Protocolo Explosivo ----------------

    @Override
    public int potenciaExplosion() {
        return isCargado() ? POTENCIA * 2 : POTENCIA;
    }

    @Override
    public int radioExplosion() {
        return isCargado() ? RADIO * 2 : RADIO;
    }

    @Override
    @JsonProperty("puedeExplotar")
    public boolean puedeExplotar() {
        return estaVivo() && !isExplotado();
    }

    /** Al explotar, el Creeper se consume: queda marcado y sin vida. */
    @Override
    public void detonar() {
        if (!puedeExplotar()) {
            throw new ExplosionImposibleException(
                    "El Creeper '" + getNombre() + "' no puede explotar: ya explotó o no tiene vida");
        }
        this.explotado = true;
        desaparecer();
    }

    @Override
    public String nombreExplosivo() {
        return getNombre();
    }

    @Override
    public String tipoExplosivo() {
        return "creeper";
    }
}
