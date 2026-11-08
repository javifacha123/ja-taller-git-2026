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
import py.edu.uc.lp3.exceptions.EntidadInvalidaException;

/**
 * Clase base de la familia persistida.
 *
 * JPA: toda la familia vive en UNA tabla (SINGLE_TABLE). La columna
 * "tipo_entidad" dice si la fila es un Creeper, un Aldeano o un Enderman.
 *
 * JSON: Jackson agrega la propiedad "tipo" (creeper, aldeano, enderman) al
 * serializar, asi el cliente distingue el subtipo al listar. Las entidades
 * NUNCA se deserializan directo desde JSON: el controller recibe un DTO y
 * llama a los constructores, que son los que validan.
 *
 * Ocultamiento: no hay setters publicos. La vida cambia solo por mensajes del
 * juego (recibirDanio, recibirExplosion, desaparecer) y la posicion por moverA.
 * JPA accede a los campos directamente (la anotacion @Id esta en un campo) y usa
 * el constructor vacio PROTEGIDO, que nadie de afuera del paquete puede llamar.
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

    private static final int VIDA_POR_DEFECTO = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private int vida;

    /** Posicion en el plano horizontal del mundo (eje X). */
    private int x;

    /** Posicion en el plano horizontal del mundo (eje Z). */
    private int z;

    /** Solo para JPA. Protegido: deja el objeto "vacio" y no se usa en el juego. */
    protected EntidadMinecraft() {
    }

    // Constructor simple: usa la vida por defecto y aparece en el origen
    public EntidadMinecraft(String nombre) {
        this(nombre, VIDA_POR_DEFECTO);
    }

    // Constructor sobrecargado: vida a eleccion, aparece en el origen
    public EntidadMinecraft(String nombre, int vida) {
        this(nombre, vida, 0, 0);
    }

    // Constructor sobrecargado completo: el objeto nunca queda en un estado ilegal
    public EntidadMinecraft(String nombre, int vida, int x, int z) {
        validarNombre(nombre);
        if (vida <= 0) {
            throw new EntidadInvalidaException("La vida inicial debe ser mayor que 0");
        }
        this.nombre = nombre;
        this.vida = vida;
        this.x = x;
        this.z = z;
    }

    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new EntidadInvalidaException("El nombre es obligatorio");
        }
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

    // ---------------- Comportamiento del juego ----------------

    /** Metodo abstracto de POO-06: cada hija lo sobreescribe. */
    @JsonProperty("movimiento")
    public abstract String moverse();

    @JsonProperty("vivo")
    public boolean estaVivo() {
        return vida > 0;
    }

    public void recibirDanio(int dano) {
        if (dano < 0) {
            throw new EntidadInvalidaException("El daño no puede ser negativo");
        }
        vida = Math.max(0, vida - dano);
    }

    // SOBRECARGA: mismo nombre, otra lista de argumentos
    public void recibirDanio(int dano, boolean conArmadura) {
        recibirDanio(conArmadura ? dano / 2 : dano);
    }

    public void desaparecer() {
        vida = 0;
    }

    /** Cambia el nombre visible (etiqueta con nombre del juego). */
    public void renombrar(String nuevoNombre) {
        validarNombre(nuevoNombre);
        this.nombre = nuevoNombre;
    }

    /** Mueve la entidad a otra posicion del mundo. Una entidad muerta no camina. */
    public void moverA(int nuevoX, int nuevoZ) {
        if (!estaVivo()) {
            throw new EntidadInvalidaException("Una entidad sin vida no puede moverse");
        }
        this.x = nuevoX;
        this.z = nuevoZ;
    }

    /** Distancia en bloques (euclidiana en el plano X-Z) hasta un punto. */
    public double distanciaA(int otroX, int otroZ) {
        int dx = this.x - otroX;
        int dz = this.z - otroZ;
        return Math.sqrt((double) dx * dx + (double) dz * dz);
    }

    /**
     * REGLA 1 — Onda expansiva.
     * Si la entidad esta viva y dentro del radio del explosivo, recibe
     * dano = ceil(potencia * (radio + 1 - distancia) / (radio + 1)).
     * Cuanto mas cerca del centro, mas dano; en el borde del radio, el minimo.
     *
     * @return el impacto registrado, o null si la explosion no la alcanzo
     */
    public Impacto recibirExplosion(Explosivo fuente) {
        double distancia = distanciaA(fuente.getX(), fuente.getZ());
        if (!estaVivo() || distancia > fuente.radioExplosion()) {
            return null;
        }
        int dano = danioPorDistancia(fuente.potenciaExplosion(), fuente.radioExplosion(), distancia);
        recibirDanio(dano);
        return Impacto.conDanio(this, distancia, dano);
    }

    protected static int danioPorDistancia(int potencia, int radio, double distancia) {
        double factor = (radio + 1 - distancia) / (radio + 1);
        return (int) Math.ceil(potencia * factor);
    }

    /** Usado por las hijas que reaccionan distinto a una explosion (Enderman). */
    protected void teletransportarA(int nuevoX, int nuevoZ) {
        this.x = nuevoX;
        this.z = nuevoZ;
    }

    /** Nombre del subtipo tal como viaja en el JSON ("creeper", "aldeano"...). */
    public String tipoEntidad() {
        return getClass().getSimpleName().toLowerCase();
    }
}
