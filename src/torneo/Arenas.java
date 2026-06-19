package torneo;

import config.Logger;

public class Arenas {

    private int libres;

    public Arenas(int cantidad) {
        this.libres = cantidad;
    }

    public synchronized void adquirir() {
        while (libres == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        libres--;
        Logger.sync("Arena ocupada, quedan libres " + libres);
    }

    public synchronized void liberar() {
        libres++;
        Logger.sync("Arena liberada, quedan libres " + libres);
        notifyAll();
    }
}
