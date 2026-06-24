package torneo;

import config.Config;
import config.Logger;
import modelo.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Torneo {

    private final List<Jugador> jugadores;
    private final Arbitro arbitro;
    private final Arenas arenas;

    public Torneo(List<Jugador> jugadores) {
        this.jugadores = new ArrayList<>(jugadores);
        this.arbitro = new Arbitro();
        this.arenas = new Arenas(Config.ARENAS_DISPONIBLES);
    }

    public Jugador ejecutar() {
        List<Jugador> vivos = new ArrayList<>(jugadores);
        int numero = 1;
        while (vivos.size() > 1) {
            arbitro.reiniciarRonda();
            Logger.ronda("Inicia ronda " + numero + " con " + vivos.size() + " jugadores");
            new Ronda(numero, vivos, arbitro, arenas).ejecutar();
            vivos = arbitro.getGanadores();
            Logger.ronda("Fin de ronda " + numero + ", avanzan " + vivos.size());
            numero++;
            System.out.println("---------------------------");
            dormir(Config.PAUSA_ENTRE_RONDAS_MS);
        }
        Jugador campeon = vivos.get(0);
        Logger.ronda("Campeon del torneo: " + campeon.getNombre());
        return campeon;
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
