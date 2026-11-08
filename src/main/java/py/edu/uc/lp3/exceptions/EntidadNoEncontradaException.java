package py.edu.uc.lp3.exceptions;

/**
 * Se lanza cuando se pide un recurso del mundo (entidad, TNT o explosion)
 * por un id que no existe en la base.
 */
public class EntidadNoEncontradaException extends MinecraftException {

    private final String recurso;
    private final Long id;

    public EntidadNoEncontradaException(String recurso, Long id) {
        super("No existe " + recurso + " con id " + id);
        this.recurso = recurso;
        this.id = id;
    }

    public String getRecurso() {
        return recurso;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getCodigo() {
        return "ENTIDAD_NO_ENCONTRADA";
    }
}
