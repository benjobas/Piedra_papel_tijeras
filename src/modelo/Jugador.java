package modelo;

import java.util.Random;

public class Jugador implements Runnable {

    public enum Jugada {
        PIEDRA, PAPEL, TIJERA;

        public boolean vence(Jugada otra) {
            return (this == PIEDRA && otra == TIJERA)
                || (this == PAPEL && otra == PIEDRA)
                || (this == TIJERA && otra == PAPEL);
        }
    }

    private final int id;
    private final String nombre;
    private final Random random;
    private final Jugada preferida;

    private int victorias;
    private boolean eliminado;
    private volatile Jugada jugadaActual;

    private Combate combateActual;

    public Jugador(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.random = new Random();
        this.preferida = Jugada.values()[this.random.nextInt(3)];
        this.victorias = 0;
        this.eliminado = false;
    }

    public void prepararCombate(Combate combate) {
        this.combateActual = combate;
        this.jugadaActual = null;
    }

    public Jugada decidirJugada() {
        Jugada elegida;
        if (random.nextInt(100) < 50) {
            elegida = preferida;
        } else {
            elegida = Jugada.values()[random.nextInt(3)];
        }
        this.jugadaActual = elegida;
        return elegida;
    }

    public void run() {
        combateActual.disputar(this);
    }

    public void sumarVictoria() {
        this.victorias++;
    }

    public void eliminar() {
        this.eliminado = true;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVictorias() {
        return victorias;
    }

    public boolean estaEliminado() {
        return eliminado;
    }

    public Jugada getJugadaActual() {
        return jugadaActual;
    }
}
