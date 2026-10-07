package py.edu.uc.lp3.domain;

public class Enderman extends EntidadMinecraft {

    private static final int VIDA_ENDERMAN = 40;
    private static final int BLOQUES_POR_DEFECTO = 8;

    private int bloquesTeletransporte;

    public Enderman(String nombre) {
        this(nombre, VIDA_ENDERMAN);
    }

    public Enderman(String nombre, int vida) {
        this(nombre, vida, BLOQUES_POR_DEFECTO);
    }

    public Enderman(String nombre, int vida, int bloquesTeletransporte) {
        super(nombre, vida);
        validarBloques(bloquesTeletransporte);
        this.bloquesTeletransporte = bloquesTeletransporte;
    }

    public int getBloquesTeletransporte() {
        return bloquesTeletransporte;
    }

    public void setBloquesTeletransporte(int bloquesTeletransporte) {
        validarBloques(bloquesTeletransporte);
        this.bloquesTeletransporte = bloquesTeletransporte;
    }

    private static void validarBloques(int bloques) {
        if (bloques < 0) {
            throw new IllegalArgumentException("Los bloques de teletransporte no pueden ser negativos");
        }
    }

    @Override
    public String moverse() {
        return "El Enderman se teletransporta " + bloquesTeletransporte + " bloques de distancia.";
    }
}
