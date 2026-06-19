package torneo;

import config.Logger;
import modelo.Combate;

public class Arenas implements Combate.Recinto {

    private final boolean[] ocupada;
    private int libres;

    public Arenas(int cantidad) {
        this.ocupada = new boolean[cantidad];
        this.libres = cantidad;
    }

    public synchronized int adquirir() {
        while (libres == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return 0;
            }
        }
        for (int i = 0; i < ocupada.length; i++) {
            if (!ocupada[i]) {
                ocupada[i] = true;
                libres--;
                Logger.sync("Arena " + (i + 1) + " ocupada, quedan libres " + libres);
                return i + 1;
            }
        }
        return 0;
    }

    public synchronized void liberar(int arena) {
        int indice = arena - 1;
        if (indice >= 0 && indice < ocupada.length && ocupada[indice]) {
            ocupada[indice] = false;
            libres++;
            Logger.sync("Arena " + arena + " liberada, quedan libres " + libres);
            notifyAll();
        }
    }

    public synchronized int getLibres() {
        return libres;
    }

    public int getCantidad() {
        return ocupada.length;
    }
}
