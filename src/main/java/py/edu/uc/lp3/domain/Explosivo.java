package py.edu.uc.lp3.domain;

/**
 * Protocolo de dominio (como en POO-07): cualquier cosa del mundo que puede explotar.
 *
 * Lo implementan clases de familias distintas: Creeper (una EntidadMinecraft,
 * un mob) y BloqueTnt (un bloque, que no hereda de EntidadMinecraft).
 * La simulacion de explosiones y el endpoint GET /api/explosivos solo conocen
 * este tipo, no la clase concreta.
 */
public interface Explosivo {

    Long getId();

    /** Posicion del centro de la explosion. */
    int getX();

    int getZ();

    /** Dano maximo que causa en el centro de la explosion. */
    int potenciaExplosion();

    /** Distancia maxima (en bloques) que alcanza la onda. */
    int radioExplosion();

    /** true si todavia puede detonar. */
    boolean puedeExplotar();

    /**
     * Consume el explosivo. Si ya no puede explotar lanza
     * {@link py.edu.uc.lp3.exceptions.ExplosionImposibleException}.
     */
    void detonar();

    String nombreExplosivo();

    /** "creeper" o "tnt": como se lo nombra en la URL y en el JSON. */
    String tipoExplosivo();
}
