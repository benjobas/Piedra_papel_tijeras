package torneo;

import config.Logger;
import modelo.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Ronda {

    private final int numero;
    private final List<Jugador> participantes;
    private final Arbitro arbitro;
    private final Arenas arenas;

    public Ronda(int numero, List<Jugador> participantes, Arbitro arbitro, Arenas arenas) {
        this.numero = numero;
        this.participantes = participantes;
        this.arbitro = arbitro;
        this.arenas = arenas;
    }

    public void ejecutar() {
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i + 1 < participantes.size(); i += 2) {
            Combate combate = new Combate(participantes.get(i), participantes.get(i + 1), arbitro, arenas);
            hilos.add(new Thread(combate, "Combate-" + numero + "-" + (i / 2 + 1)));
        }
        for (Thread hilo : hilos) {
            hilo.start();
            Logger.hilo("Inicia " + hilo.getName());
        }
        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
