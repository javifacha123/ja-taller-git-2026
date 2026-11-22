package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.EntidadMinecraft;
import py.edu.uc.lp3.domain.Explosivo;
import py.edu.uc.lp3.domain.Impacto;

/**
 * Contrato de una regla de juego que decide qué le pasa a UNA entidad cuando
 * UN explosivo detona cerca.
 *
 * <p>Las reglas viven en la capa de servicios, no en las entities: una entity
 * guarda estado, una regla decide. Hay una regla general (la onda expansiva) y
 * una especialización para el Enderman, que la sobreescribe.</p>
 *
 * <p>El servicio de simulación no pregunta de qué tipo es la entidad: recorre
 * las reglas en orden, se queda con la primera que {@link #corresponde} y le
 * pide que se aplique. Agregar un mob con otra reacción es agregar una regla,
 * no tocar un {@code if}.</p>
 */
public abstract class ReglaExplosion {

    /** true si esta regla es la que gobierna a esa entidad. */
    public abstract boolean corresponde(EntidadMinecraft entidad);

    /**
     * Aplica la regla y cambia el estado de la entidad.
     *
     * @return el impacto para el registro de la explosión, o {@code null} si la
     *         onda no la alcanzó (está muerta o quedó fuera del radio)
     */
    public abstract Impacto aplicar(EntidadMinecraft entidad, Explosivo fuente);

    /** true si la entidad está viva y dentro del radio del explosivo. */
    protected boolean alcanzada(EntidadMinecraft entidad, Explosivo fuente) {
        return entidad.estaVivo()
                && entidad.distanciaA(fuente.getX(), fuente.getZ()) <= fuente.radioExplosion();
    }

    // ---------------- Las dos fórmulas, en un solo lugar ----------------
    // Las usan las reglas al simular y ReglaServiceImpl al responderle a un
    // compañero que consulta por HTTP. Si una fórmula cambia, se toca acá y
    // las dos puertas quedan de acuerdo solas.

    /**
     * REGLA 1 — Daño de la onda expansiva a una distancia dada.
     * {@code ceil(potencia * (radio + 1 - distancia) / (radio + 1))}.
     */
    public static int danioPorDistancia(int potencia, int radio, double distancia) {
        double factor = (radio + 1 - distancia) / (radio + 1);
        return (int) Math.ceil(potencia * factor);
    }

    /**
     * REGLA 2 — Bloques de teletransporte que necesita un Enderman para salir
     * del radio desde esa distancia: {@code ceil(radio + 1 - distancia)}.
     */
    public static int bloquesNecesarios(int radio, double distancia) {
        return (int) Math.ceil(radio + 1 - distancia);
    }
}
