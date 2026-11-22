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
 * Registro persistido de una simulación: qué explotó, dónde, con qué fuerza y
 * qué le pasó a cada entidad alcanzada.
 *
 * <p>Entity: solo estado. La simulación (detonar el explosivo y aplicar la
 * onda a cada entidad) la hace {@code SimulacionServiceImpl}; esta clase
 * guarda el resultado que el servicio le va cargando.</p>
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

    /** Copia del explosivo los datos del momento de la detonación. */
    public Explosion(Explosivo fuente) {
        this.tipoExplosivo = fuente.tipoExplosivo();
        this.explosivoId = fuente.getId();
        this.nombreExplosivo = fuente.nombreExplosivo();
        this.x = fuente.getX();
        this.z = fuente.getZ();
        this.potencia = fuente.potenciaExplosion();
        this.radio = fuente.radioExplosion();
        this.momento = LocalDateTime.now();
    }

    /** El servicio agrega un impacto por cada entidad que la onda alcanzó. */
    public void registrar(Impacto impacto) {
        this.impactos.add(impacto);
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
