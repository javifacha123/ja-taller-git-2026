package py.edu.uc.lp3.exceptions;

/**
 * La lanza la capa de servicios cuando un pedido trae valores que dejarían a
 * la entidad en un estado imposible (vida <= 0, nombre vacío, bloques de
 * teletransporte negativos, tipo de entidad desconocido...).
 *
 * <p>Las entities no validan: el servicio revisa los valores antes de llamar
 * al constructor, así que ningún camino puede saltearse esta comprobación.</p>
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
