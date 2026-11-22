package py.edu.uc.lp3.exceptions;

/**
 * Base de la jerarquía de excepciones del dominio Minecraft.
 *
 * Es no comprobada (extiende RuntimeException): las lanza la capa de servicios
 * al validar un pedido o al aplicar una regla del juego, y obligar a declarar
 * "throws" en cada firma de servicio ensuciaría el contrato. Además, Spring
 * revierte la transacción automáticamente ante una RuntimeException, que es lo
 * que queremos cuando una simulación falla a mitad de camino.
 *
 * Es abstracta: nadie lanza "un error de Minecraft" genérico, siempre una hija
 * que nombra la regla que falló.
 */
public abstract class MinecraftException extends RuntimeException {

    protected MinecraftException(String mensaje) {
        super(mensaje);
    }

    /** Código corto y estable que viaja en el JSON de error. */
    public abstract String getCodigo();
}
