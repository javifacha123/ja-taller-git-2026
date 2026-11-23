package py.edu.uc.lp3.exceptions;

/**
 * POO-09: la API de un compañero rechazó la jugada del escenario.
 *
 * <p>Guarda de QUIÉN fue el rechazo (A2 o A3), para que el handler pueda
 * decirlo en el JSON y la base quede sin tocar. El pedido estaba bien formado y
 * mis propias reglas lo aceptaban; lo que no pasó fue la regla ajena, así que
 * es un conflicto con el estado del mundo compartido y no un error de formato.</p>
 */
public class CompaneroRechazoException extends MinecraftException {

    private final String rol;
    private final String companero;

    public CompaneroRechazoException(String rol, String companero, String motivo) {
        super(motivo);
        this.rol = rol;
        this.companero = companero;
    }

    /** "A2" o "A3". */
    public String getRol() {
        return rol;
    }

    /** Nombre del compañero, tal como está configurado. */
    public String getCompanero() {
        return companero;
    }

    @Override
    public String getCodigo() {
        return "COMPANERO_RECHAZO";
    }
}
