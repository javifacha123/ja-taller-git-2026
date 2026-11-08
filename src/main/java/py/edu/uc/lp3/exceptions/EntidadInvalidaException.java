package py.edu.uc.lp3.exceptions;

/**
 * Se lanza cuando un constructor o un mensaje del dominio recibe valores que
 * dejarian al objeto en un estado imposible (vida <= 0, nombre vacio,
 * bloques de teletransporte negativos, tipo de entidad desconocido...).
 */
public class EntidadInvalidaException extends MinecraftException {

    public EntidadInvalidaException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getCodigo() {
        return "ENTIDAD_INVALIDA";
    }
}
