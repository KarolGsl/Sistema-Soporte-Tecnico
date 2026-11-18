
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {

    static Path buscarCSV() throws IOException {
        Path carpeta = Path.of("").toAbsolutePath();

        while (carpeta != null) {
            Path archivo = carpeta.resolve("datos/solicitudes.csv");

            if (Files.exists(archivo)) return archivo;

            carpeta = carpeta.getParent();
        }

        throw new IOException("No se encontro datos/solicitudes.csv");
    }

    static final Map<String, Integer> RANGO = Map.of(
        "critica", 0, "alta", 1, "media", 2, "baja", 3
    );

    static final Map<String, Integer> RESOLUCION = Map.of(
        "critica", 4, "alta", 3, "media", 2, "baja", 1
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
            categoria = c[2].toLowerCase().trim();
            prioridad = c[3].toLowerCase().trim();
            estado = c[4].toLowerCase().trim();
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
        if (lineas.get(i).isBlank()) continue;

        String[] c = lineas.get(i).split(",", -1);

        if (c.length != 6) {
            throw new IllegalArgumentException(
                "CSV invalido en linea " + (i + 1)
            );
        }

        datos.add(new Solicitud(c));
    }

    return datos;
}

    static void procesarConcurrente(List<Solicitud> datos)
        throws InterruptedException, ExecutionException {

    ExecutorService pool =
        Executors.newFixedThreadPool(
            Math.max(1, Math.min(datos.size(), 8))
        );

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

    // INTERFAZ GRAFICA

    private JTable tabla;
    private DefaultTableModel modelo;

    private JLabel totalLabel;
    private JLabel asignadasLabel;
    private JLabel colaLabel;
    private JLabel estadoLabel;

    private JButton procesarBtn;
    private JButton actualizarBtn;

    private List<Solicitud> datos = new ArrayList<>();

    private final Color fondo = new Color(15, 23, 42);
    private final Color panel = new Color(30, 41, 59);
    private final Color azul = new Color(37, 99, 235);
    private final Color blanco = new Color(241, 245, 249);

    public Main() {
        setTitle("Sistema de Soporte Tecnico");
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(fondo);
        setLayout(new BorderLayout(15, 15));

        crearEncabezado();
        crearEstadisticas();
        crearTabla();
        crearBotones();

        cargarInicial();
    }

    private JLabel crearTexto(String texto, int tamanio) {
        JLabel label = new JLabel(texto);
        label.setForeground(blanco);
        label.setFont(new Font("Arial", Font.BOLD, tamanio));
        return label;
    }

    private void crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(fondo);
        encabezado.setBorder(
            BorderFactory.createEmptyBorder(25, 25, 10, 25)
        );

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setBackground(fondo);

        JLabel titulo = crearTexto("SOPORTE TECNICO", 26);
        JLabel subtitulo = new JLabel(
            "Sistema de gestion de solicitudes"
        );
        subtitulo.setForeground(Color.LIGHT_GRAY);

        textos.add(titulo);
        textos.add(subtitulo);

        encabezado.add(textos, BorderLayout.WEST);

        estadoLabel = new JLabel("Sistema iniciado");
        estadoLabel.setForeground(new Color(74, 222, 128));
        encabezado.add(estadoLabel, BorderLayout.EAST);

        add(encabezado, BorderLayout.NORTH);
    }

    private JPanel crearTarjeta(String titulo, JLabel valor, Color color) {
        JPanel tarjeta = new JPanel(new GridLayout(2, 1, 5, 5));
        tarjeta.setBackground(panel);
        tarjeta.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
            )
        );

        JLabel nombre = new JLabel(titulo);
        nombre.setForeground(Color.LIGHT_GRAY);
        nombre.setFont(new Font("Arial", Font.PLAIN, 14));

        valor.setForeground(blanco);
        valor.setFont(new Font("Arial", Font.BOLD, 27));

        tarjeta.add(nombre);
        tarjeta.add(valor);

        return tarjeta;
    }

    private void crearEstadisticas() {
        JPanel contenedor = new JPanel(new GridLayout(1, 3, 15, 0));
        contenedor.setBackground(fondo);
        contenedor.setBorder(
            BorderFactory.createEmptyBorder(0, 25, 15, 25)
        );

        totalLabel = new JLabel("0");
        asignadasLabel = new JLabel("0");
        colaLabel = new JLabel("0");

        contenedor.add(crearTarjeta(
            "TOTAL DE SOLICITUDES", totalLabel, azul
        ));

        contenedor.add(crearTarjeta(
            "SOLICITUDES ASIGNADAS",
            asignadasLabel,
            new Color(34, 197, 94)
        ));

        contenedor.add(crearTarjeta(
            "SOLICITUDES EN COLA",
            colaLabel,
            new Color(234, 179, 8)
        ));

        JPanel zona = new JPanel(new BorderLayout());
        zona.setBackground(fondo);
        zona.add(contenedor, BorderLayout.CENTER);

        add(zona, BorderLayout.AFTER_LAST_LINE);
    }

    private void crearTabla() {
        String[] columnas = {
            "ID", "Solicitante", "Categoria",
            "Prioridad", "Estado", "Tecnico",
            "Respuesta (h)", "Resolucion (h)"
        };

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(32);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setForeground(blanco);
        tabla.setBackground(panel);
        tabla.setGridColor(new Color(71, 85, 105));
        tabla.setSelectionBackground(azul);
        tabla.setSelectionForeground(Color.WHITE);

        tabla.getTableHeader().setBackground(
            new Color(51, 65, 85)
        );

        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(panel);
        scroll.setBorder(
            BorderFactory.createEmptyBorder(0, 25, 10, 25)
        );

        JPanel zona = new JPanel(new BorderLayout());
        zona.setBackground(fondo);
        zona.add(scroll, BorderLayout.CENTER);

        JLabel titulo = crearTexto("SOLICITUDES REGISTRADAS", 16);
        titulo.setBorder(
            BorderFactory.createEmptyBorder(5, 25, 12, 0)
        );

        zona.add(titulo, BorderLayout.NORTH);

        add(zona, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(190, 42));
        return boton;
    }

    private void crearBotones() {
        JPanel botones = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 15, 15)
        );

        botones.setBackground(fondo);

        actualizarBtn = crearBoton(
            "Actualizar datos",
            new Color(71, 85, 105)
        );

        procesarBtn = crearBoton(
            "Procesar solicitudes",
            azul
        );

        actualizarBtn.addActionListener(e -> cargarInicial());
        procesarBtn.addActionListener(e -> procesar());

        botones.add(actualizarBtn);
        botones.add(procesarBtn);

        add(botones, BorderLayout.SOUTH);
    }

    private void cargarInicial() {
        try {
            datos = cargar(buscarCSV().toString());
            mostrarDatos();
            actualizarEstadisticas();
            estadoLabel.setText("Datos cargados correctamente");
            estadoLabel.setForeground(new Color(74, 222, 128));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Error al cargar CSV",
                JOptionPane.ERROR_MESSAGE
            );

            estadoLabel.setText("Error al cargar datos");
            estadoLabel.setForeground(Color.RED);
        }
    }

    private void mostrarDatos() {
        modelo.setRowCount(0);

        for (Solicitud s : datos) {
            modelo.addRow(new Object[]{
                s.id,
                s.solicitante,
                s.categoria,
                s.prioridad,
                s.estado,
                s.tecnico,
                s.tiempoRespuesta,
                s.tiempo
            });
        }
    }

    private void actualizarEstadisticas() {
        int asignadas = 0;

        for (Solicitud s : datos) {
            if (s.estado.equals("asignada")) {
                asignadas++;
            }
        }

        totalLabel.setText(String.valueOf(datos.size()));
        asignadasLabel.setText(String.valueOf(asignadas));
        colaLabel.setText(String.valueOf(datos.size() - asignadas));
    }

    private void procesar() {
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No hay solicitudes para procesar."
            );
            return;
        }

        procesarBtn.setEnabled(false);
        actualizarBtn.setEnabled(false);
        estadoLabel.setText("Procesando solicitudes...");

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                procesarConcurrente(datos);
                asignar(datos);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    mostrarDatos();
                    actualizarEstadisticas();

                    estadoLabel.setText("Procesamiento completado");
                    estadoLabel.setForeground(
                        new Color(74, 222, 128)
                    );

                    JOptionPane.showMessageDialog(
                        Main.this,
                        "Las solicitudes se procesaron correctamente.",
                        "Proceso terminado",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception e) {
                    estadoLabel.setText("Error al procesar");

                    JOptionPane.showMessageDialog(
                        Main.this,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    procesarBtn.setEnabled(true);
                    actualizarBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {}

            Main ventana = new Main();
            ventana.setVisible(true);
        });
    }
}
