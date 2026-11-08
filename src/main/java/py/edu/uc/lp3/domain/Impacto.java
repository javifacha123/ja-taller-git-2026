package py.edu.uc.lp3.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Lo que le paso a UNA entidad en UNA explosion. Se guarda como fila de la
 * tabla explosion_impacto (coleccion embebida de Explosion). Copia el id y el
 * nombre en vez de apuntar con una clave foranea: asi borrar una entidad no
 * borra la historia de las explosiones.
 */
@Embeddable
public class Impacto {

    private Long entidadId;
    private String nombreEntidad;
    private String tipoEntidad;
    private double distancia;

    @Column(name = "danio")
    private int danio;

    private boolean esquivo;
    private int bloquesGastados;
    private int vidaRestante;

    /** Solo para JPA. */
    protected Impacto() {
    }

    private Impacto(EntidadMinecraft entidad, double distancia, int danio, boolean esquivo, int bloquesGastados) {
        this.entidadId = entidad.getId();
        this.nombreEntidad = entidad.getNombre();
        this.tipoEntidad = entidad.tipoEntidad();
        this.distancia = Math.round(distancia * 100) / 100.0;
        this.danio = danio;
        this.esquivo = esquivo;
        this.bloquesGastados = bloquesGastados;
        this.vidaRestante = entidad.getVida();
    }

    static Impacto conDanio(EntidadMinecraft entidad, double distancia, int danio) {
        return new Impacto(entidad, distancia, danio, false, 0);
    }

    static Impacto esquivado(EntidadMinecraft entidad, double distancia, int bloquesGastados) {
        return new Impacto(entidad, distancia, 0, true, bloquesGastados);
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public double getDistancia() {
        return distancia;
    }

    public int getDanio() {
        return danio;
    }

    public boolean isEsquivo() {
        return esquivo;
    }

    public int getBloquesGastados() {
        return bloquesGastados;
    }

    public int getVidaRestante() {
        return vidaRestante;
    }
}
