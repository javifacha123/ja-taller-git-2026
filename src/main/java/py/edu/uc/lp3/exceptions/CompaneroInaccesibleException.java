package py.edu.uc.lp3.exceptions;

/**
 * POO-09: la API de un compañero no respondió (está apagada, cambió de puerto
 * o devolvió algo que no se puede interpretar).
 *
 * <p>Se distingue de {@link CompaneroRechazoException} a propósito: una cosa es
 * que el compañero diga "no" y otra que no haya nadie del otro lado. En los dos
 * casos la base queda igual, pero el JSON lo dice distinto y el código HTTP
 * también (502 en vez de 409).</p>
 */
public class CompaneroInaccesibleException extends MinecraftException {

    private final String rol;
    private final String companero;

    public CompaneroInaccesibleException(String rol, String companero, String motivo) {
        super(motivo);
        this.rol = rol;
        this.companero = companero;
    }

    /** "A2" o "A3". */
    public String getRol() {
        return rol;
    }

    public String getCompanero() {
        return companero;
    }

    @Override
    public String getCodigo() {
        return "COMPANERO_INACCESIBLE";
    }
}
