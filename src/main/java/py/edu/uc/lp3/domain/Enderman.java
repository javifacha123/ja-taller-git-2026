package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;

@Entity
@DiscriminatorValue("ENDERMAN")
public class Enderman extends EntidadMinecraft {

    private static final int VIDA_ENDERMAN = 40;
    private static final int BLOQUES_POR_DEFECTO = 8;

    // Envoltorio (Integer) porque en SINGLE_TABLE las otras hijas dejan esta columna en NULL
    private Integer bloquesTeletransporte;

    /** Solo para JPA. */
    protected Enderman() {
    }

    public Enderman(String nombre) {
        this(nombre, VIDA_ENDERMAN);
    }

    public Enderman(String nombre, int vida) {
        this(nombre, vida, BLOQUES_POR_DEFECTO);
    }

    public Enderman(String nombre, int vida, int bloquesTeletransporte) {
        this(nombre, vida, bloquesTeletransporte, 0, 0);
    }

    public Enderman(String nombre, int vida, int bloquesTeletransporte, int x, int z) {
        super(nombre, vida, x, z);
        validarBloques(bloquesTeletransporte);
        this.bloquesTeletransporte = bloquesTeletransporte;
    }

    public int getBloquesTeletransporte() {
        return bloquesTeletransporte == null ? 0 : bloquesTeletransporte;
    }

    // No es un setter "tonto": valida igual que el constructor
    public void setBloquesTeletransporte(int bloquesTeletransporte) {
        validarBloques(bloquesTeletransporte);
        this.bloquesTeletransporte = bloquesTeletransporte;
    }

    private static void validarBloques(int bloques) {
        if (bloques < 0) {
            throw new EntidadInvalidaException("Los bloques de teletransporte no pueden ser negativos");
        }
    }

    @Override
    public String moverse() {
        return "El Enderman se teletransporta " + getBloquesTeletransporte() + " bloques de distancia.";
    }

    /**
     * REGLA 2 — Esquive del Enderman (sobreescribe la regla 1).
     * Si la onda lo alcanza y le quedan bloques de teletransporte suficientes
     * para salir del radio (ceil(radio + 1 - distancia)), gasta esos bloques,
     * aparece justo afuera del radio, en la direccion opuesta a la explosion,
     * y no recibe dano. Si no le alcanzan, recibe el dano normal de la regla 1.
     */
    @Override
    public Impacto recibirExplosion(Explosivo fuente) {
        double distancia = distanciaA(fuente.getX(), fuente.getZ());
        if (!estaVivo() || distancia > fuente.radioExplosion()) {
            return null;
        }
        int necesarios = (int) Math.ceil(fuente.radioExplosion() + 1 - distancia);
        if (getBloquesTeletransporte() < necesarios) {
            return super.recibirExplosion(fuente);
        }
        this.bloquesTeletransporte = getBloquesTeletransporte() - necesarios;
        alejarseDe(fuente);
        return Impacto.esquivado(this, distancia, necesarios);
    }

    /** Aparece a radio + 1 bloques del centro, sobre la recta centro -> Enderman. */
    private void alejarseDe(Explosivo fuente) {
        double dx = getX() - fuente.getX();
        double dz = getZ() - fuente.getZ();
        double largo = Math.sqrt(dx * dx + dz * dz);
        if (largo == 0) {        // justo encima de la explosion: escapa hacia +X
            dx = 1;
            dz = 0;
            largo = 1;
        }
        int destino = fuente.radioExplosion() + 1;
        int nuevoX = fuente.getX() + (int) Math.round(dx / largo * destino);
        int nuevoZ = fuente.getZ() + (int) Math.round(dz / largo * destino);
        teletransportarA(nuevoX, nuevoZ);
    }
}
