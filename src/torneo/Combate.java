package torneo;

import config.Config;
import config.Logger;
import modelo.Jugador;
import modelo.Resultado;

public class Combate implements Runnable {

    private final Jugador jugadorA;
    private final Jugador jugadorB;
    private final Arbitro arbitro;
    private final Arenas arenas;

    public Combate(Jugador jugadorA, Jugador jugadorB, Arbitro arbitro, Arenas arenas) {
        this.jugadorA = jugadorA;
        this.jugadorB = jugadorB;
        this.arbitro = arbitro;
        this.arenas = arenas;
    }

    public void run() {
        arenas.adquirir();
        Logger.combate(jugadorA.getNombre() + " vs " + jugadorB.getNombre() + " comienza");
        Resultado resultado = jugar();
        arbitro.registrar(resultado);
        arenas.liberar();
    }

    private Resultado jugar() {
        while (true) {
            dormir(Config.DURACION_COMBATE_MS);
            Jugador.Jugada ja = jugadorA.decidirJugada();
            Jugador.Jugada jb = jugadorB.decidirJugada();
            if (ja == jb) {
                Logger.combate(jugadorA.getNombre() + " y " + jugadorB.getNombre() + " empatan con " + ja);
                continue;
            }
            if (ja.vence(jb)) {
                Logger.combate(jugadorA.getNombre() + " vence a " + jugadorB.getNombre()
                    + " (" + ja + " vs " + jb + ")");
                return new Resultado(jugadorA, jugadorB);
            }
            Logger.combate(jugadorB.getNombre() + " vence a " + jugadorA.getNombre()
                + " (" + jb + " vs " + ja + ")");
            return new Resultado(jugadorB, jugadorA);
        }
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
