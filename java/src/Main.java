
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    static Path buscarCSV() throws IOException {
    Path carpeta = Path.of("").toAbsolutePath();

    while (carpeta != null) {
        Path archivo = carpeta.resolve("datos/solicitudes.csv");

        if (Files.exists(archivo)) {
            return archivo;
        }

        carpeta = carpeta.getParent();
    }

    throw new IOException("No se encontro datos/solicitudes.csv");
}

    static final Map<String, Integer> RANGO = Map.of(
        "critica", 0,
        "alta", 1,
        "media", 2,
        "baja", 3
    );

    static final Map<String, Integer> RESOLUCION = Map.of(
        "critica", 4,
        "alta", 3,
        "media", 2,
        "baja", 1
    );

    static final Map<String, Integer> TIEMPO_RESPUESTA = Map.of(
        "critica", 1, "alta", 2, "media", 8, "baja", 24
    );

    static class Solicitud {
        int id, tiempo, tiempoRespuesta;
        String solicitante, categoria, prioridad;
        String estado, descripcion;
        String tecnico = "En cola";

        Solicitud(String[] c) {
            id = Integer.parseInt(c[0]);
            solicitante = c[1];
            categoria = c[2];
            prioridad = c[3];
            estado = c[4];
            descripcion = c[5];

            if (!RANGO.containsKey(prioridad)) {
                throw new IllegalArgumentException(
                    "Prioridad invalida en solicitud " + id
                );
            }
            tiempoRespuesta = TIEMPO_RESPUESTA.get(prioridad);
        }
    }

    static List<Solicitud> cargar(String ruta) throws IOException {
        List<Solicitud> datos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(Path.of(ruta));

        for (int i = 1; i < lineas.size(); i++) {
            String[] c = lineas.get(i).split(",", -1);

            if (c.length != 6) {
                throw new IllegalArgumentException(
                    "Registro CSV invalido en linea " + (i + 1)
                );
            }

            datos.add(new Solicitud(c));
        }

        return datos;
    }

    static void procesarConcurrente(List<Solicitud> datos)
            throws InterruptedException, ExecutionException {

        ExecutorService pool =
            Executors.newFixedThreadPool(Math.max(1, datos.size()));

        try {
            List<Future<Integer>> tareas = new ArrayList<>();

            for (Solicitud s : datos) {
                tareas.add(pool.submit(
                    () -> RESOLUCION.get(s.prioridad)
                ));
            }

            for (int i = 0; i < datos.size(); i++) {
                datos.get(i).tiempo = tareas.get(i).get();
            }

        } finally {
            pool.shutdown();
        }
    }

    static void asignar(List<Solicitud> datos) {

        datos.sort(
            Comparator
                .comparingInt((Solicitud s) -> RANGO.get(s.prioridad))
                .thenComparingInt(s -> s.id)
        );

        Map<String, String> tecnicos = new LinkedHashMap<>();

        tecnicos.put("hardware", "Tecnico Ana");
        tecnicos.put("software", "Tecnico Luis");
        tecnicos.put("red", "Tecnico Mario");

        for (Solicitud s : datos) {
            String tecnico = tecnicos.get(s.categoria);

            if (tecnico != null && s.estado.equals("abierta")) {
                s.tecnico = tecnico;
                s.estado = "asignada";
                tecnicos.remove(s.categoria);
            } else {
                s.tecnico = "En cola";
            }
        }
    }

    static void generarEstadisticas(List<Solicitud> datos) {

        int asignadas = 0;
        int cola = 0;

        Map<String, Integer> conteo = new TreeMap<>();

        for (Solicitud s : datos) {
            conteo.merge(s.prioridad, 1, Integer::sum);

            if (s.estado.equals("asignada")) {
                asignadas++;
            } else {
                cola++;
            }
        }

        System.out.println("\n========== ESTADISTICAS ==========");
        System.out.println("Total: " + datos.size());
        System.out.println("Asignadas: " + asignadas);
        System.out.println("En cola: " + cola);

        System.out.println("\nPor prioridad:");

        conteo.forEach((k, v) ->
            System.out.println(k + ": " + v)
        );
    }

    public static void main(String[] args) {

        try {
            List<Solicitud> datos =
                cargar(buscarCSV().toString());

            procesarConcurrente(datos);
            asignar(datos);

            System.out.println("========================================");
            System.out.println(" SISTEMA DE SOPORTE TECNICO - JAVA");
            System.out.println("========================================\n");

            for (Solicitud s : datos) {
                System.out.println("Solicitud #" + s.id);
                System.out.println("Solicitante: " + s.solicitante);
                System.out.println("Categoria: " + s.categoria);
                System.out.println("Prioridad: " + s.prioridad);
                System.out.println("Estado: " + s.estado);
                System.out.println("Tecnico: " + s.tecnico);
                System.out.println("Primera respuesta maxima: " + s.tiempoRespuesta + " horas");
                System.out.println("Tiempo estimado de resolucion: " + s.tiempo + " horas");
                System.out.println("----------------------------------------");
            }

            generarEstadisticas(datos);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
