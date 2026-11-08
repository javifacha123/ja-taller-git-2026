package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registro persistido de una simulacion: que exploto, donde, con que fuerza
 * y que le paso a cada entidad alcanzada.
 *
 * La simulacion se hace en {@link #provocar(Explosivo, List)}: el controller
 * (via el servicio) solo carga los objetos y guarda el resultado.
 */
@Entity
@Table(name = "explosion")
@JsonPropertyOrder({"id", "tipoExplosivo", "explosivoId", "nombreExplosivo", "x", "z",
        "potencia", "radio", "momento", "impactos"})
public class Explosion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoExplosivo;
    private Long explosivoId;
    private String nombreExplosivo;
    private int x;
    private int z;
    private int potencia;
    private int radio;
    private LocalDateTime momento;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "explosion_impacto", joinColumns = @JoinColumn(name = "explosion_id"))
    private List<Impacto> impactos = new ArrayList<>();

    /** Solo para JPA. */
    protected Explosion() {
    }

    private Explosion(Explosivo fuente) {
        this.tipoExplosivo = fuente.tipoExplosivo();
        this.explosivoId = fuente.getId();
        this.nombreExplosivo = fuente.nombreExplosivo();
        this.x = fuente.getX();
        this.z = fuente.getZ();
        this.potencia = fuente.potenciaExplosion();
        this.radio = fuente.radioExplosion();
        this.momento = LocalDateTime.now();
    }

    /**
     * Simulacion: detona el explosivo y aplica la onda a cada entidad.
     * Regla 1 (onda expansiva) vive en EntidadMinecraft.recibirExplosion.
     * Regla 2 (esquive del Enderman) es la sobreescritura de ese metodo en Enderman.
     *
     * @throws py.edu.uc.lp3.exceptions.ExplosionImposibleException si el explosivo ya no puede explotar
     */
    public static Explosion provocar(Explosivo fuente, List<? extends EntidadMinecraft> entidades) {
        fuente.detonar();                       // falla antes de tocar a nadie si no puede explotar
        Explosion explosion = new Explosion(fuente);
        for (EntidadMinecraft entidad : entidades) {
            if (entidad == fuente) {
                continue;                       // el Creeper no se dana a si mismo: ya se consumio
            }
            Impacto impacto = entidad.recibirExplosion(fuente);   // despacho dinamico: Enderman esquiva
            if (impacto != null) {
                explosion.impactos.add(impacto);
            }
        }
        return explosion;
    }

    public Long getId() {
        return id;
    }

    public String getTipoExplosivo() {
        return tipoExplosivo;
    }

    public Long getExplosivoId() {
        return explosivoId;
    }

    public String getNombreExplosivo() {
        return nombreExplosivo;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getPotencia() {
        return potencia;
    }

    public int getRadio() {
        return radio;
    }

    public LocalDateTime getMomento() {
        return momento;
    }

    public List<Impacto> getImpactos() {
        return Collections.unmodifiableList(impactos);
    }
}
