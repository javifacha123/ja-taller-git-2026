package py.edu.uc.lp3.exceptions;

/**
 * Regla de juego incumplida: se pidio detonar un explosivo que ya no puede
 * explotar (un Creeper que ya exploto o esta muerto, una TNT ya detonada).
 * El pedido esta bien formado; lo que choca es el estado actual del mundo.
 */
public class ExplosionImposibleException extends MinecraftException {

    public ExplosionImposibleException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getCodigo() {
        return "EXPLOSION_IMPOSIBLE";
    }
}
