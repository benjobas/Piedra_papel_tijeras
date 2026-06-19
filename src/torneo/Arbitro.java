package torneo;

import config.Logger;
import modelo.Combate;
import modelo.Jugador;
import modelo.Resultado;

import java.util.ArrayList;
import java.util.List;

public class Arbitro implements Combate.Registrador {

    private final List<Resultado> historial;
    private final List<Jugador> ganadores;
    private int combatesRegistrados;

    public Arbitro() {
        this.historial = new ArrayList<>();
        this.ganadores = new ArrayList<>();
        this.combatesRegistrados = 0;
    }

    public synchronized void registrar(Resultado resultado) {
        resultado.getGanador().sumarVictoria();
        resultado.getPerdedor().eliminar();
        ganadores.add(resultado.getGanador());
        historial.add(resultado);
        combatesRegistrados++;
        Logger.sync("Arbitro registra combate " + combatesRegistrados
            + ": gana " + resultado.getGanador().getNombre());
    }

    public synchronized List<Jugador> getGanadores() {
        return new ArrayList<>(ganadores);
    }

    public synchronized int getCombatesRegistrados() {
        return combatesRegistrados;
    }

    public synchronized void reiniciarRonda() {
        ganadores.clear();
        combatesRegistrados = 0;
    }
}
