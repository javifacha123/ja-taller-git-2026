package py.edu.uc.lp3.domain;

/**
 * Protocolo de dominio (como en POO-07): cualquier cosa del mundo que puede
 * explotar.
 *
 * <p>Lo implementan clases de familias distintas: Creeper (una
 * EntidadMinecraft, un mob) y BloqueTnt (un bloque, que no hereda de
 * EntidadMinecraft). La simulación y el endpoint GET /api/explosivos solo
 * conocen este tipo, no la clase concreta.</p>
 *
 * <p>Son todos mensajes de consulta, salvo {@link #consumir()}, que solo
 * cambia estado. La decisión de si corresponde consumirlo, y la excepción
 * cuando no corresponde, están en la capa de servicios.</p>
 */
public interface Explosivo {

    Long getId();

    /** Posición del centro de la explosión. */
    int getX();

    int getZ();

    /** Daño máximo que causa en el centro de la explosión. */
    int potenciaExplosion();

    /** Distancia máxima (en bloques) que alcanza la onda. */
    int radioExplosion();

    /** true si todavía está en condiciones de detonar. */
    boolean puedeExplotar();

    /**
     * Gasta el explosivo. No verifica nada: el servicio consulta antes
     * {@link #puedeExplotar()} y lanza
     * {@link py.edu.uc.lp3.exceptions.ExplosionImposibleException} si no se puede.
     */
    void consumir();

    String nombreExplosivo();

    /** "creeper" o "tnt": cómo se lo nombra en la URL y en el JSON. */
    String tipoExplosivo();
}
