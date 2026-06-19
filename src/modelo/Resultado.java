package modelo;

public class Resultado {

    private final Jugador ganador;
    private final Jugador perdedor;
    private final Jugador.Jugada jugadaGanadora;
    private final Jugador.Jugada jugadaPerdedora;

    public Resultado(Jugador ganador, Jugador perdedor, Jugador.Jugada jugadaGanadora, Jugador.Jugada jugadaPerdedora) {
        this.ganador = ganador;
        this.perdedor = perdedor;
        this.jugadaGanadora = jugadaGanadora;
        this.jugadaPerdedora = jugadaPerdedora;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public Jugador getPerdedor() {
        return perdedor;
    }

    public Jugador.Jugada getJugadaGanadora() {
        return jugadaGanadora;
    }

    public Jugador.Jugada getJugadaPerdedora() {
        return jugadaPerdedora;
    }
}
