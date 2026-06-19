import config.Config;
import modelo.Jugador;
import torneo.Torneo;
import visual.Renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int cantidad = pedirCantidad(scanner);
        List<Jugador> jugadores = crearJugadores(cantidad);

        Torneo torneo = new Torneo(jugadores);
        Renderer renderer = new Renderer(torneo);
        Thread hiloRender = new Thread(renderer, "Renderer");

        hiloRender.start();
        torneo.ejecutar();
        renderer.detener();

        try {
            hiloRender.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        scanner.close();
    }

    private static int pedirCantidad(Scanner scanner) {
        while (true) {
            System.out.println("Cantidad de jugadores (potencia de 2, entre "
                + Config.MIN_JUGADORES + " y " + Config.MAX_JUGADORES + "):");
            String linea = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(linea);
                if (n < Config.MIN_JUGADORES || n > Config.MAX_JUGADORES) {
                    System.out.println("Fuera de rango.");
                } else if ((n & (n - 1)) != 0) {
                    System.out.println("Debe ser potencia de 2.");
                } else {
                    return n;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida, ingrese un numero.");
            }
        }
    }

    private static List<Jugador> crearJugadores(int cantidad) {
        List<String> nombres = new ArrayList<>();
        Collections.addAll(nombres, Config.NOMBRES);
        Collections.shuffle(nombres);
        List<Jugador> jugadores = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            jugadores.add(new Jugador(i + 1, nombres.get(i)));
        }
        return jugadores;
    }
}
