package py.edu.uc.lp3.ja_taller_git_2026.domain;

public class Enderman extends EntidadMinecraft {

    private int bloquesTeletransporte;

    public Enderman(String nombre, int vida, int bloquesTeletransporte) {
        super(nombre, vida);
        if (bloquesTeletransporte < 0) {
            throw new IllegalArgumentException("Los bloques de teletransporte no pueden ser negativos");
        }
        this.bloquesTeletransporte = bloquesTeletransporte;
    }

    public int getBloquesTeletransporte() {
        return bloquesTeletransporte;
    }

    public void setBloquesTeletransporte(int bloquesTeletransporte) {
        if (bloquesTeletransporte >= 0) {
            this.bloquesTeletransporte = bloquesTeletransporte;
        }
    }

    @Override
    public String moverse() {
        return "El Enderman se teletransporta " + bloquesTeletransporte + " bloques de distancia.";
    }
}