package py.edu.uc.lp3.ja_taller_git_2026.domain;

public class Creeper extends EntidadMinecraft {

    public Creeper(String nombre, int vida) {
        super(nombre, vida);
    }

    @Override
    public String moverse() {
        return "El Creeper se acerca silenciosamente al jugador.";
    }
}
