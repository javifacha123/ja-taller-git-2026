package py.edu.uc.lp3.domain;

public class Creeper extends EntidadMinecraft {

    public Creeper(String nombre) {
        super(nombre);
    }

    public Creeper(String nombre, int vida) {
        super(nombre, vida);
    }

    @Override
    public String moverse() {
        return "El Creeper se acerca silenciosamente al jugador.";
    }
}
