package visual;

import config.Config;
import torneo.Torneo;

public class Renderer implements Runnable {

    private final Torneo torneo;
    private final Tablero tablero;
    private volatile boolean activo;

    public Renderer(Torneo torneo) {
        this.torneo = torneo;
        this.tablero = new Tablero();
        this.activo = true;
    }

    public void run() {
        while (activo && !torneo.estaTerminado()) {
            dibujar();
            torneo.esperarCambio(Config.INTERVALO_RENDER_MS);
        }
        dibujar();
    }

    public void detener() {
        activo = false;
        torneo.alCambiar();
    }

    private void dibujar() {
        System.out.print(Colores.LIMPIAR);
        System.out.print(tablero.construir(torneo));
        System.out.flush();
    }
}
