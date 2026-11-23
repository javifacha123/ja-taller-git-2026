package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * POO-09: una jugada del escenario que las APIs de A2 y A3 aceptaron.
 *
 * <p>Solo llega a existir una fila si los DOS compañeros dijeron que sí. Si
 * alguno rechaza, el servicio lanza la excepción antes de guardar y la
 * transacción deja la base exactamente como estaba.</p>
 *
 * <p>Entity: solo estado, como el resto del dominio. Guarda copias de los
 * nombres y de los veredictos para que la jugada siga siendo legible aunque
 * después se borren las entidades o cambien los servicios de los compañeros.</p>
 */
@Entity
@Table(name = "jugada")
@JsonPropertyOrder({"id", "nombre", "momento", "creeperId", "aldeanoId", "endermanId",
        "nombreCreeper", "nombreAldeano", "nombreEnderman",
        "danioAldeano", "bloquesEnderman",
        "companeroA2", "veredictoA2", "companeroA3", "veredictoA3"})
public class Jugada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private LocalDateTime momento;

    private Long creeperId;
    private Long aldeanoId;
    private Long endermanId;

    private String nombreCreeper;
    private String nombreAldeano;
    private String nombreEnderman;

    /** Daño que mi regla 1 calcula para el Aldeano, y que A2 validó. */
    private int danioAldeano;

    /** Bloques que mi regla 2 calcula para el Enderman, y que A3 validó. */
    private int bloquesEnderman;

    private String companeroA2;

    @Column(length = 1000)
    private String veredictoA2;

    private String companeroA3;

    @Column(length = 1000)
    private String veredictoA3;

    /** Solo para JPA. */
    protected Jugada() {
    }

    public Jugada(String nombre, Creeper creeper, EntidadMinecraft aldeano, Enderman enderman,
                  int danioAldeano, int bloquesEnderman) {
        this.nombre = nombre;
        this.momento = LocalDateTime.now();
        this.creeperId = creeper.getId();
        this.aldeanoId = aldeano.getId();
        this.endermanId = enderman.getId();
        this.nombreCreeper = creeper.getNombre();
        this.nombreAldeano = aldeano.getNombre();
        this.nombreEnderman = enderman.getNombre();
        this.danioAldeano = danioAldeano;
        this.bloquesEnderman = bloquesEnderman;
    }

    /** Deja registrado quién validó cada mitad de la jugada. */
    public void registrarVeredictos(String companeroA2, String veredictoA2,
                                    String companeroA3, String veredictoA3) {
        this.companeroA2 = companeroA2;
        this.veredictoA2 = veredictoA2;
        this.companeroA3 = companeroA3;
        this.veredictoA3 = veredictoA3;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getMomento() {
        return momento;
    }

    public Long getCreeperId() {
        return creeperId;
    }

    public Long getAldeanoId() {
        return aldeanoId;
    }

    public Long getEndermanId() {
        return endermanId;
    }

    public String getNombreCreeper() {
        return nombreCreeper;
    }

    public String getNombreAldeano() {
        return nombreAldeano;
    }

    public String getNombreEnderman() {
        return nombreEnderman;
    }

    public int getDanioAldeano() {
        return danioAldeano;
    }

    public int getBloquesEnderman() {
        return bloquesEnderman;
    }

    public String getCompaneroA2() {
        return companeroA2;
    }

    public String getVeredictoA2() {
        return veredictoA2;
    }

    public String getCompaneroA3() {
        return companeroA3;
    }

    public String getVeredictoA3() {
        return veredictoA3;
    }
}
