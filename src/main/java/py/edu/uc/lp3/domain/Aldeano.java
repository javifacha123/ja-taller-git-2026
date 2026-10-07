package py.edu.uc.lp3.domain;

public class Aldeano extends EntidadMinecraft {

    public Aldeano(String nombre) {
        super(nombre);
    }

    public Aldeano(String nombre, int vida) {
        super(nombre, vida);
    }

    @Override
    public String moverse() {
        return "El Aldeano camina hacia la aldea.";
    }
}
