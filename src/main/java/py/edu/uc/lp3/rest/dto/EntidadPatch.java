package py.edu.uc.lp3.rest.dto;

/**
 * Cuerpo del PATCH /api/entidades/{id}. Todos los campos son opcionales:
 * el que viene se cambia, el que se omite (o viene null) queda como estaba.
 * "vida" esta declarada solo para poder rechazarla: la vida cambia por las
 * reglas del juego, no por un PATCH.
 */
public record EntidadPatch(String nombre, Integer x, Integer z, Integer bloquesTeletransporte, Integer vida) {
}
