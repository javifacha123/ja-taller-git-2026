package py.edu.uc.lp3.ja_taller_git_2026.domain;

public abstract class EntidadMinecraft {

    private String nombre;
    private int vida;

    public EntidadMinecraft(String nombre, int vida) {
        if (vida < 0) {
            throw new IllegalArgumentException("La vida no puede ser negativa");
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
