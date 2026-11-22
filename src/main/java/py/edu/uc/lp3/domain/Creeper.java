package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * El Creeper es una EntidadMinecraft ("es un") y además un Explosivo.
 * Un Creeper cargado (alcanzado por un rayo) explota con el doble de potencia
 * y el doble de radio.
 *
 * <p>Entity: solo estado. Quien decide si puede detonar y quien lanza la
 * excepción cuando no puede es la capa de servicios.</p>
 */
@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends EntidadMinecraft implements Explosivo {

    private static final int POTENCIA = 12;
    private static final int RADIO = 3;

    // Envoltorios (Boolean) porque en SINGLE_TABLE las filas de Aldeano y
    // Enderman dejan estas columnas en NULL.
    private Boolean cargado;
    private Boolean explotado;

    /** Solo para JPA. */
    protected Creeper() {
    }

    /** Constructor simple: vida por defecto, en el origen, sin carga. */
    public Creeper(String nombre) {
        this(nombre, VIDA_POR_DEFECTO);
    }

    public Creeper(String nombre, int vida) {
        this(nombre, vida, 0, 0);
    }

    public Creeper(String nombre, int vida, int x, int z) {
        this(nombre, vida, x, z, false);
    }

    /**
     * Constructor completo: es el único que asigna los campos propios y el que
     * llama a {@code super(...)}. Los otros tres delegan en él con
     * {@code this(...)}, así la inicialización queda en un solo lugar.
     */
    public Creeper(String nombre, int vida, int x, int z, boolean cargado) {
        super(nombre, vida, x, z);
        this.cargado = cargado;
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

    /**
     * Consume el explosivo: queda marcado y sin vida. No verifica si podía
     * explotar; eso lo controla el servicio antes de llamar.
     */
    @Override
    public void consumir() {
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
