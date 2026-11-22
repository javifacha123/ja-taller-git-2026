package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Clase base de la familia persistida.
 *
 * <p>Esta clase es una <em>entity</em>: guarda estado y nada más. No valida, no
 * lanza excepciones y no decide reglas del juego. Esas tres cosas viven en la
 * capa de servicios ({@code py.edu.uc.lp3.service}), que es la única que
 * construye entidades y les manda mensajes.</p>
 *
 * <p>JPA: toda la familia vive en UNA tabla (SINGLE_TABLE). La columna
 * {@code tipo_entidad} dice si la fila es un Creeper, un Aldeano o un
 * Enderman.</p>
 *
 * <p>JSON: Jackson agrega la propiedad {@code tipo} (creeper, aldeano,
 * enderman) al serializar, así el cliente distingue el subtipo al listar. Las
 * entidades NUNCA se deserializan directo desde JSON: el controller recibe un
 * DTO y el servicio decide qué construir.</p>
 *
 * <p>Ocultamiento: los campos son privados y no hay setters. El estado cambia
 * solo por los mensajes de abajo, que son los que usa el servicio después de
 * validar. JPA accede a los campos directamente y usa el constructor vacío
 * protegido, que nadie fuera de la jerarquía puede llamar.</p>
 */
@Entity
@Table(name = "entidad_minecraft")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_entidad")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Creeper.class, name = "creeper"),
        @JsonSubTypes.Type(value = Aldeano.class, name = "aldeano"),
        @JsonSubTypes.Type(value = Enderman.class, name = "enderman")
})
@JsonPropertyOrder({"id", "nombre", "vida", "vivo", "x", "z", "movimiento"})
public abstract class EntidadMinecraft {

    /** Vida que usa el constructor simple cuando el pedido no trae una. */
    public static final int VIDA_POR_DEFECTO = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private int vida;

    /** Posición en el plano horizontal del mundo (eje X). */
    private int x;

    /** Posición en el plano horizontal del mundo (eje Z). */
    private int z;

    /** Solo para JPA. Protegido: deja el objeto vacío y no se usa en el juego. */
    protected EntidadMinecraft() {
    }

    /** Constructor simple: vida por defecto, aparece en el origen. */
    public EntidadMinecraft(String nombre) {
        this(nombre, VIDA_POR_DEFECTO);
    }

    /** Constructor sobrecargado: vida a elección, aparece en el origen. */
    public EntidadMinecraft(String nombre, int vida) {
        this(nombre, vida, 0, 0);
    }

    /**
     * Constructor sobrecargado completo. Es el único que asigna los campos; los
     * otros dos delegan en él con {@code this(...)}.
     *
     * <p>No valida: quien construye es el servicio, y valida antes de llamar.</p>
     */
    public EntidadMinecraft(String nombre, int vida, int x, int z) {
        this.nombre = nombre;
        this.vida = vida;
        this.x = x;
        this.z = z;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    // ---------------- Comportamiento propio de la entidad ----------------

    /**
     * Método abstracto de POO-06: cada hija lo sobreescribe.
     *
     * <p>Es cómo se describe la entidad a sí misma, no una regla del juego: no
     * cambia estado, no valida y no depende de otra entidad. Por eso sigue
     * acá y no en el servicio.</p>
     */
    @JsonProperty("movimiento")
    public abstract String moverse();

    /** Consulta derivada del estado: no decide nada, solo lo informa. */
    @JsonProperty("vivo")
    public boolean estaVivo() {
        return vida > 0;
    }

    /** Distancia en bloques (euclidiana en el plano X-Z) hasta un punto. */
    public double distanciaA(int otroX, int otroZ) {
        int dx = this.x - otroX;
        int dz = this.z - otroZ;
        return Math.sqrt((double) dx * dx + (double) dz * dz);
    }

    /** Nombre del subtipo tal como viaja en el JSON ("creeper", "aldeano"...). */
    public String tipoEntidad() {
        return getClass().getSimpleName().toLowerCase();
    }

    // ---------------- Cambios de estado ----------------
    // Los llama el servicio, que ya validó. Acá no hay reglas ni excepciones:
    // solo la mínima aritmética que evita que la vida quede negativa en la fila.

    /** Baja la vida en {@code danio} puntos, sin pasar de 0. */
    public void aplicarDanio(int danio) {
        this.vida = Math.max(0, this.vida - danio);
    }

    /**
     * SOBRECARGA: mismo nombre, otra lista de argumentos. Con armadura el daño
     * que llega a la vida es la mitad. Se resuelve en tiempo de compilación.
     */
    public void aplicarDanio(int danio, boolean conArmadura) {
        aplicarDanio(conArmadura ? danio / 2 : danio);
    }

    /** La entidad sale del mundo: queda sin vida. */
    public void desaparecer() {
        this.vida = 0;
    }

    /** Cambia el nombre visible (etiqueta con nombre del juego). */
    public void renombrar(String nuevoNombre) {
        this.nombre = nuevoNombre;
    }

    /** Deja la entidad en otra posición del mundo. */
    public void ubicarEn(int nuevoX, int nuevoZ) {
        this.x = nuevoX;
        this.z = nuevoZ;
    }
}
