package py.edu.uc.lp3.domain;

public abstract class EntidadMinecraft {

    private static final int VIDA_POR_DEFECTO = 20;

    private final String nombre;
    private int vida;

    // Constructor simple: usa la vida por defecto
    public EntidadMinecraft(String nombre) {
        this(nombre, VIDA_POR_DEFECTO);
    }

    // Constructor sobrecargado: el objeto nunca queda en un estado ilegal
    public EntidadMinecraft(String nombre, int vida) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (vida <= 0) {
            throw new IllegalArgumentException("La vida inicial debe ser mayor que 0");
        }
        this.nombre = nombre;
        this.vida = vida;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public abstract String moverse();

    public void recibirDanio(int dano) {
        if (dano < 0) {
            throw new IllegalArgumentException("El daño no puede ser negativo");
        }
        vida = Math.max(0, vida - dano);
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public void desaparecer() {
        vida = 0;
    }
}
