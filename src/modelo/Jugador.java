package modelo;

import java.util.Random;

public class Jugador {

    public enum Jugada {
        PIEDRA, PAPEL, TIJERA;

        public boolean vence(Jugada otra) {
            return (this == PIEDRA && otra == TIJERA)
                || (this == PAPEL && otra == PIEDRA)
                || (this == TIJERA && otra == PAPEL);
        }
    }

    private final String nombre;
    private final Random random;
    private int victorias;
    private boolean eliminado;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.random = new Random();
        this.victorias = 0;
        this.eliminado = false;
    }

    public Jugada decidirJugada() {
        return Jugada.values()[random.nextInt(3)];
    }

    public void sumarVictoria() {
        this.victorias++;
    }

    public void eliminar() {
        this.eliminado = true;
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
}
