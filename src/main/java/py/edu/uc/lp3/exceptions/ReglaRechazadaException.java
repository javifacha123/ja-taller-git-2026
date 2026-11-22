package py.edu.uc.lp3.exceptions;

/**
 * Una de mis dos reglas del juego rechazó la jugada que me propusieron.
 *
 * <p>La lanza {@code ReglaService} cuando un compañero (o mi propio escenario)
 * consulta una regla por HTTP y la jugada no es legal: un daño declarado que no
 * coincide con la fórmula de la onda expansiva, o un Enderman que no tiene
 * bloques suficientes para esquivar.</p>
 *
 * <p>El pedido está bien formado y los valores son válidos; lo que no pasa es
 * la regla. Por eso es una hermana de {@link EntidadInvalidaException} y no la
 * misma excepción: distingue "me mandaste un valor imposible" de "tu jugada no
 * cumple mi regla".</p>
 */
public class ReglaRechazadaException extends MinecraftException {

    private final String regla;

    public ReglaRechazadaException(String regla, String mensaje) {
        super(mensaje);
        this.regla = regla;
    }

    /** Nombre de la regla que rechazó: "onda-expansiva" o "esquive-enderman". */
    public String getRegla() {
        return regla;
    }

    @Override
    public String getCodigo() {
        return "REGLA_RECHAZADA";
    }
}
