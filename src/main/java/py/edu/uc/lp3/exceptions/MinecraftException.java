package py.edu.uc.lp3.exceptions;

/**
 * Base de la jerarquia de excepciones del dominio Minecraft.
 *
 * Es no comprobada (extiende RuntimeException): las reglas del juego se rompen
 * dentro de constructores y mensajes del dominio, y obligar a declarar "throws"
 * en cada firma ensuciaria el modelo. Ademas, Spring revierte la transaccion
 * automaticamente ante una RuntimeException, que es lo que queremos cuando una
 * simulacion falla a mitad de camino.
 *
 * Es abstracta: nadie lanza "un error de Minecraft" generico, siempre una hija
 * que nombra la regla que fallo.
 */
public abstract class MinecraftException extends RuntimeException {

    protected MinecraftException(String mensaje) {
        super(mensaje);
    }

    /** Codigo corto y estable que viaja en el JSON de error. */
    public abstract String getCodigo();
}
