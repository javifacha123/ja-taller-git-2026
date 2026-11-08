package py.edu.uc.lp3.rest.dto;

import py.edu.uc.lp3.domain.Explosivo;

/**
 * Vista JSON armada SOLO con mensajes del protocolo Explosivo: no importa si
 * detras hay un Creeper o una TNT.
 */
public record ExplosivoResumen(String tipo, Long id, String nombre, int x, int z,
                               int potencia, int radio, boolean puedeExplotar) {

    public static ExplosivoResumen de(Explosivo explosivo) {
        return new ExplosivoResumen(
                explosivo.tipoExplosivo(),
                explosivo.getId(),
                explosivo.nombreExplosivo(),
                explosivo.getX(),
                explosivo.getZ(),
                explosivo.potenciaExplosion(),
                explosivo.radioExplosion(),
                explosivo.puedeExplotar());
    }
}
