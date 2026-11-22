package py.edu.uc.lp3.service.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import py.edu.uc.lp3.domain.Enderman;
import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.domain.Impacto;

/**
 * REGLA 2 — Esquive del Enderman.
 *
 * <p>Si la onda alcanza a un Enderman y le quedan al menos
 * {@code ceil(radio + 1 - d)} bloques de teletransporte, los gasta, aparece
 * justo afuera del radio en la dirección opuesta a la explosión y no recibe
 * daño. Si no le alcanzan los bloques, le toca la regla 1.</p>
 *
 * <p>Hereda de {@link ReglaOndaExpansiva} y sobreescribe {@link #aplicar}:
 * cuando el esquive no se puede pagar, delega en {@code super.aplicar(...)} en
 * vez de repetir la cuenta del daño. Esa es la especialización que pedía el
 * enunciado, acá en la capa que decide.</p>
 */
@Component
@Order(1)      // se evalúa antes que la regla general
public class ReglaEsquiveEnderman extends ReglaOndaExpansiva {

    /** Solo gobierna a los Endermans; del resto se encarga la regla general. */
    @Override
    public boolean corresponde(EntidadMinecraft entidad) {
        return entidad instanceof Enderman;
    }

    @Override
    public Impacto aplicar(EntidadMinecraft entidad, Explosivo fuente) {
        if (!alcanzada(entidad, fuente)) {
            return null;
        }
        Enderman enderman = (Enderman) entidad;
        double distancia = enderman.distanciaA(fuente.getX(), fuente.getZ());
        int necesarios = bloquesNecesarios(fuente.radioExplosion(), distancia);
        if (enderman.getBloquesTeletransporte() < necesarios) {
            return super.aplicar(enderman, fuente);     // no le alcanza: recibe el daño
        }
        enderman.gastarBloques(necesarios);
        alejarseDe(enderman, fuente);
        return Impacto.esquivado(enderman, distancia, necesarios);
    }

    /** Aparece a radio + 1 bloques del centro, sobre la recta centro -> Enderman. */
    private void alejarseDe(Enderman enderman, Explosivo fuente) {
        double dx = enderman.getX() - fuente.getX();
        double dz = enderman.getZ() - fuente.getZ();
        double largo = Math.sqrt(dx * dx + dz * dz);
        if (largo == 0) {            // justo encima de la explosión: escapa hacia +X
            dx = 1;
            dz = 0;
            largo = 1;
        }
        int destino = fuente.radioExplosion() + 1;
        int nuevoX = fuente.getX() + (int) Math.round(dx / largo * destino);
        int nuevoZ = fuente.getZ() + (int) Math.round(dz / largo * destino);
        enderman.ubicarEn(nuevoX, nuevoZ);
    }
}
