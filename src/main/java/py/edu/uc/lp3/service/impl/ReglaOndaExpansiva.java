package py.edu.uc.lp3.service.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.service.ReglaExplosion;

/**
 * REGLA 1 — Onda expansiva.
 *
 * <p>Toda entidad viva que esté a una distancia {@code d} menor o igual al
 * radio del explosivo pierde
 * {@code ceil(potencia * (radio + 1 - d) / (radio + 1))} puntos de vida.
 * Cuanto más cerca del centro, más daño; en el borde del radio, el mínimo.</p>
 *
 * <p>Es la regla general: {@link #corresponde} acepta cualquier entidad, así
 * que vale para todas las que no tengan una regla más específica.</p>
 */
@Component
@Order(2)      // última: es la regla general, acepta cualquier entidad
public class ReglaOndaExpansiva extends ReglaExplosion {

    @Override
    public boolean corresponde(EntidadMinecraft entidad) {
        return true;
    }

    @Override
    public Impacto aplicar(EntidadMinecraft entidad, Explosivo fuente) {
        if (!alcanzada(entidad, fuente)) {
            return null;
        }
        double distancia = entidad.distanciaA(fuente.getX(), fuente.getZ());
        int danio = danioPorDistancia(fuente.potenciaExplosion(), fuente.radioExplosion(), distancia);
        entidad.aplicarDanio(danio);
        return Impacto.conDanio(entidad, distancia, danio);
    }
}
