package visual;

import modelo.Combate;
import modelo.Jugador;
import modelo.Resultado;
import torneo.Ronda;
import torneo.Torneo;

import java.util.List;

public class Tablero {

    public String construir(Torneo torneo) {
        if (torneo.getEstado() == Torneo.Estado.TERMINADO && torneo.getCampeon() != null) {
            return pantallaCampeon(torneo);
        }
        StringBuilder sb = new StringBuilder();
        encabezado(sb, torneo);
        Ronda ronda = torneo.getRondaActual();
        if (ronda != null) {
            for (Combate combate : ronda.getCombates()) {
                lineaCombate(sb, combate);
            }
        }
        panelGanadores(sb, torneo);
        return sb.toString();
    }

    private void encabezado(StringBuilder sb, Torneo torneo) {
        sb.append(Colores.CYAN).append(Colores.NEGRITA);
        sb.append(Simbolos.ESPADAS).append("  TORNEO PIEDRA PAPEL TIJERA  ").append(Simbolos.ESPADAS);
        sb.append(Colores.RESET).append("\n");
        sb.append(Colores.AMARILLO);
        sb.append("Ronda ").append(torneo.getNumeroRonda()).append(" de ").append(torneo.getTotalRondas());
        sb.append(Colores.RESET);
        sb.append(Colores.CYAN).append("    Arenas libres: ");
        sb.append(torneo.getArenas().getLibres()).append("/").append(torneo.getArenas().getCantidad());
        sb.append(Colores.RESET).append("\n");
        sb.append(Colores.GRIS).append("------------------------------------------------");
        sb.append(Colores.RESET).append("\n");
    }

    private void lineaCombate(StringBuilder sb, Combate combate) {
        Jugador a = combate.getJugadorA();
        Jugador b = combate.getJugadorB();
        sb.append(estadoTexto(combate)).append(" ");
        sb.append(arenaTexto(combate)).append(" ");
        sb.append(nombreJugador(a, combate)).append(" ");
        sb.append(Simbolos.deJugada(a.getJugadaActual()));
        sb.append(Colores.MAGENTA).append("  vs  ").append(Colores.RESET);
        sb.append(Simbolos.deJugada(b.getJugadaActual())).append(" ");
        sb.append(nombreJugador(b, combate));
        Resultado r = combate.getResultado();
        if (r != null) {
            sb.append(Colores.VERDE).append("   ").append(Simbolos.TROFEO).append(" ");
            sb.append(r.getGanador().getNombre()).append(Colores.RESET);
        }
        sb.append("\n");
    }

    private String estadoTexto(Combate combate) {
        switch (combate.getEstado()) {
            case EN_ESPERA:
                return Colores.GRIS + Simbolos.ESPERA + " ESPERA ARENA" + Colores.RESET;
            case EN_CURSO:
                return Colores.AMARILLO + Simbolos.RELOJ + " EN CURSO    " + Colores.RESET;
            case FINALIZADO:
                return Colores.VERDE + Simbolos.CHECK + " FINALIZADO  " + Colores.RESET;
            default:
                return "";
        }
    }

    private String arenaTexto(Combate combate) {
        if (combate.getArena() <= 0) {
            return Colores.GRIS + "[ --- ]" + Colores.RESET;
        }
        return Colores.AZUL + "[Arena " + combate.getArena() + "]" + Colores.RESET;
    }

    private String nombreJugador(Jugador j, Combate combate) {
        Resultado r = combate.getResultado();
        if (r != null && r.getGanador() == j) {
            return Colores.VERDE + Colores.NEGRITA + j.getNombre() + Colores.RESET;
        }
        if (r != null && r.getPerdedor() == j) {
            return Colores.ROJO + j.getNombre() + " " + Simbolos.CALAVERA + Colores.RESET;
        }
        return Colores.BLANCO + j.getNombre() + Colores.RESET;
    }

    private void panelGanadores(StringBuilder sb, Torneo torneo) {
        sb.append(Colores.GRIS).append("------------------------------------------------");
        sb.append(Colores.RESET).append("\n");
        sb.append(Colores.VERDE).append(Simbolos.TROFEO).append(" Avanzan: ").append(Colores.RESET);
        List<Jugador> ganadores = torneo.getArbitro().getGanadores();
        if (ganadores.isEmpty()) {
            sb.append(Colores.GRIS).append("(aun no hay ganadores en esta ronda)").append(Colores.RESET);
        } else {
            for (int i = 0; i < ganadores.size(); i++) {
                sb.append(ganadores.get(i).getNombre());
                if (i < ganadores.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("\n");
    }

    private String pantallaCampeon(Torneo torneo) {
        Jugador campeon = torneo.getCampeon();
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(Colores.AMARILLO).append(Colores.NEGRITA);
        sb.append("        ").append(Simbolos.CORONA).append("  CAMPEON DEL TORNEO  ").append(Simbolos.CORONA);
        sb.append("\n\n");
        sb.append("        ").append(Simbolos.TROFEO).append("   ").append(campeon.getNombre());
        sb.append("   ").append(Simbolos.TROFEO).append("\n\n");
        sb.append(Colores.RESET);
        sb.append(Colores.CYAN).append("        Combates ganados: ").append(campeon.getVictorias());
        sb.append(Colores.RESET).append("\n");
        return sb.toString();
    }
}
