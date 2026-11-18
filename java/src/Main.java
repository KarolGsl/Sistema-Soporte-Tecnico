import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {

    // Colores compartidos con la interfaz de Python (Tkinter).
    private static final Color FONDO = new Color(17, 17, 26);
    private static final Color SUPERFICIE = new Color(27, 27, 41);
    private static final Color TARJETA = new Color(36, 36, 56);
    private static final Color PRIMARIO = new Color(139, 92, 246);
    private static final Color TEXTO = new Color(244, 244, 245);
    private static final Color SECUNDARIO = new Color(161, 161, 170);
    private static final Color BORDE = new Color(56, 56, 77);
    private static final Color VERDE = new Color(134, 239, 172);
    private static final Color AMARILLO = new Color(252, 211, 77);

    private static final Map<String, Integer> PRIORIDAD = Map.of(
        "critica", 0, "alta", 1, "media", 2, "baja", 3
    );
    private static final Map<String, Integer> TIEMPO_RESPUESTA = Map.of(
        "critica", 1, "alta", 2, "media", 8, "baja", 24
    );
    private static final Map<String, Integer> TIEMPO_RESOLUCION = Map.of(
        "critica", 4, "alta", 3, "media", 2, "baja", 1
    );

    private static final Map<String, String> TECNICOS = Map.of(
        "hardware", "Técnico Ana",
        "software", "Técnico Luis",
        "red", "Técnico Mario"
    );

    private static class Solicitud {
        int id;
        String solicitante;
        String categoria;
        String prioridad;
        String estado;
        String descripcion;
        String tecnico = "En cola";
        int tiempoRespuesta;
        int tiempoResolucion;

        Solicitud(String[] columnas) {
            if (columnas.length != 6) {
                throw new IllegalArgumentException("El CSV debe tener seis columnas por solicitud.");
            }
            id = Integer.parseInt(columnas[0].trim());
            solicitante = columnas[1].trim();
            categoria = columnas[2].trim().toLowerCase();
            prioridad = columnas[3].trim().toLowerCase();
            estado = columnas[4].trim().toLowerCase();
            descripcion = columnas[5].trim();

            if (!PRIORIDAD.containsKey(prioridad)) {
                throw new IllegalArgumentException("Prioridad inválida en solicitud " + id);
            }
            if (!TECNICOS.containsKey(categoria)) {
                throw new IllegalArgumentException("Categoría inválida en solicitud " + id);
            }
            tiempoRespuesta = TIEMPO_RESPUESTA.get(prioridad);
            tiempoResolucion = TIEMPO_RESOLUCION.get(prioridad);
        }
    }

    private static class Resultado {
        List<Solicitud> solicitudes;
        Map<String, Integer> prioridades;
        int asignadas;
        int enCola;

        Resultado(List<Solicitud> solicitudes) {
            this.solicitudes = solicitudes;
            prioridades = new LinkedHashMap<>();
            for (String p : List.of("critica", "alta", "media", "baja")) {
                prioridades.put(p, 0);
            }
            asignadas = 0;
            enCola = 0;
            for (Solicitud s : solicitudes) {
                prioridades.put(s.prioridad, prioridades.get(s.prioridad) + 1);
                if ("asignada".equals(s.estado)) asignadas++;
                else enCola++;
            }
        }
    }

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JPanel panelMetricas;
    private final JPanel panelTecnicos;
    private final JLabel lblPrioridades;
    private final JLabel lblEstado;
    private final JButton btnActualizar;
    private final JButton btnNueva;
    private List<Solicitud> solicitudes = new ArrayList<>();

    public Main() {
        setTitle("Sistema de Soporte Técnico | Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1280, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout(0, 0));

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(FONDO);
        raiz.setBorder(new EmptyBorder(22, 26, 14, 26));
        setContentPane(raiz);

        // Encabezado
        JPanel encabezado = new JPanel(new BorderLayout(12, 0));
        encabezado.setBackground(FONDO);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        JLabel titulo = label("SOPORTE TÉCNICO", 21, TEXTO, true);
        JLabel subtitulo = label("Panel de administración · Java Swing", 10, SECUNDARIO, false);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(subtitulo);
        encabezado.add(titulos, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 3));
        acciones.setOpaque(false);
        btnNueva = boton("+ Nueva solicitud", PRIMARIO);
        btnActualizar = boton("↻ Actualizar y asignar", TARJETA);
        acciones.add(btnNueva);
        acciones.add(btnActualizar);
        encabezado.add(acciones, BorderLayout.EAST);
        raiz.add(encabezado, BorderLayout.NORTH);

        // Zona central: métricas arriba, tabla y técnicos abajo.
        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setOpaque(false);

        panelMetricas = new JPanel(new GridLayout(1, 3, 12, 0));
        panelMetricas.setOpaque(false);
        centro.add(panelMetricas, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout(15, 0));
        contenido.setOpaque(false);

        JPanel zonaTabla = new JPanel(new BorderLayout(0, 9));
        zonaTabla.setOpaque(false);
        JPanel tituloTabla = new JPanel(new BorderLayout());
        tituloTabla.setOpaque(false);
        tituloTabla.add(label("Solicitudes de soporte", 14, TEXTO, true), BorderLayout.WEST);
        lblPrioridades = label("", 9, SECUNDARIO, false);
        tituloTabla.add(lblPrioridades, BorderLayout.EAST);
        zonaTabla.add(tituloTabla, BorderLayout.NORTH);

        String[] columnas = {
            "ID", "Solicitante", "Descripción", "Categoría", "Prioridad",
            "Estado", "Técnico", "Respuesta", "Resolución"
        };
        modelo = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setRowHeight(35);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);
        tabla.setGridColor(BORDE);
        tabla.setSelectionBackground(new Color(81, 64, 120));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(false);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setBackground(TARJETA);
        tabla.getTableHeader().setForeground(TEXTO);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));

        int[] anchos = {42, 90, 190, 78, 76, 76, 105, 78, 82};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 3, 4, 5, 7, 8}) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }
        tabla.setDefaultRenderer(Object.class, new RendererPrioridad());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(SUPERFICIE);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        zonaTabla.add(scroll, BorderLayout.CENTER);
        contenido.add(zonaTabla, BorderLayout.CENTER);

        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(SUPERFICIE);
        derecha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(15, 13, 15, 13)
        ));
        derecha.setPreferredSize(new Dimension(225, 0));
        JLabel tecnicosTitulo = label("Técnicos", 13, TEXTO, true);
        JLabel tecnicosSub = label("Disponibilidad por especialidad", 9, SECUNDARIO, false);
        tecnicosTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tecnicosSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(tecnicosTitulo);
        derecha.add(Box.createVerticalStrut(4));
        derecha.add(tecnicosSub);
        derecha.add(Box.createVerticalStrut(13));
        panelTecnicos = new JPanel();
        panelTecnicos.setOpaque(false);
        panelTecnicos.setLayout(new BoxLayout(panelTecnicos, BoxLayout.Y_AXIS));
        panelTecnicos.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(panelTecnicos);
        derecha.add(Box.createVerticalGlue());
        contenido.add(derecha, BorderLayout.EAST);

        centro.add(contenido, BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        lblEstado = label("Listo", 9, SECUNDARIO, false);
        raiz.add(lblEstado, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> actualizarDatos());
        btnNueva.addActionListener(e -> nuevaSolicitud());

        actualizarDatos();
    }

    private JLabel label(String texto, int tamano, Color color, boolean negrita) {
        JLabel l = new JLabel(texto);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, tamano));
        return l;
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        return b;
    }

    private JPanel tarjeta(String titulo, String valor, String detalle, Color color) {
        JPanel p = new JPanel();
        p.setBackground(SUPERFICIE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE),
            new EmptyBorder(12, 15, 12, 15)
        ));
        JLabel t = label(titulo, 9, SECUNDARIO, false);
        JLabel v = label(valor, 24, color, true);
        JLabel d = label(detalle, 8, SECUNDARIO, false);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);
        p.add(Box.createVerticalStrut(3));
        p.add(v);
        p.add(Box.createVerticalStrut(1));
        p.add(d);
        return p;
    }

    private Path buscarCSV() throws IOException {
        Path carpeta = Path.of("").toAbsolutePath();
        while (carpeta != null) {
            Path candidato = carpeta.resolve("datos").resolve("solicitudes.csv");
            if (Files.exists(candidato)) return candidato;
            carpeta = carpeta.getParent();
        }
        throw new IOException("No se encontró datos/solicitudes.csv. Ejecuta el programa desde la carpeta del proyecto o una subcarpeta.");
    }

    private List<Solicitud> cargarSolicitudes() throws IOException {
        Path archivo = buscarCSV();
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        List<Solicitud> resultado = new ArrayList<>();
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (linea.isBlank()) continue;
            // El CSV del proyecto usa campos simples sin comas internas.
            String[] columnas = linea.split(",", -1);
            try {
                resultado.add(new Solicitud(columnas));
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("Error en la línea " + (i + 1) + " del CSV: " + ex.getMessage(), ex);
            }
        }
        return resultado;
    }

    private void procesarConcurrentemente(List<Solicitud> datos) throws Exception {
        if (datos.isEmpty()) return;
        int hilos = Math.max(1, Math.min(datos.size(), 8));
        ExecutorService executor = Executors.newFixedThreadPool(hilos);
        try {
            List<Future<Integer>> tareas = new ArrayList<>();
            for (Solicitud s : datos) {
                tareas.add(executor.submit(() -> TIEMPO_RESOLUCION.get(s.prioridad)));
            }
            for (int i = 0; i < datos.size(); i++) {
                datos.get(i).tiempoResolucion = tareas.get(i).get();
            }
        } finally {
            executor.shutdown();
        }
    }

    private void asignarTecnicos(List<Solicitud> datos) {
        datos.sort(Comparator
            .comparingInt((Solicitud s) -> PRIORIDAD.get(s.prioridad))
            .thenComparingInt(s -> s.id));

        Map<String, String> disponibles = new LinkedHashMap<>();
        disponibles.put("hardware", "Técnico Ana");
        disponibles.put("software", "Técnico Luis");
        disponibles.put("red", "Técnico Mario");

        for (Solicitud s : datos) {
            String tecnico = disponibles.get(s.categoria);
            if (tecnico != null && "abierta".equals(s.estado)) {
                s.tecnico = tecnico;
                s.estado = "asignada";
                disponibles.remove(s.categoria);
            } else {
                s.tecnico = "En cola";
            }
        }
    }

    private void actualizarDatos() {
        btnActualizar.setEnabled(false);
        btnNueva.setEnabled(false);
        lblEstado.setText("Cargando y procesando solicitudes...");

        new SwingWorker<List<Solicitud>, Void>() {
            @Override
            protected List<Solicitud> doInBackground() throws Exception {
                List<Solicitud> datos = cargarSolicitudes();
                procesarConcurrentemente(datos);
                asignarTecnicos(datos);
                return datos;
            }

            @Override
            protected void done() {
                try {
                    solicitudes = get();
                    mostrarDatos();
                    actualizarResumen();
                    lblEstado.setText("Datos actualizados. Asignación calculada por prioridad e ID.");
                    lblEstado.setForeground(VERDE);
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() == null ? ex : ex.getCause();
                    lblEstado.setText("No se pudieron cargar las solicitudes.");
                    lblEstado.setForeground(new Color(252, 165, 165));
                    JOptionPane.showMessageDialog(Main.this, causa.getMessage(),
                        "Error al cargar solicitudes", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnActualizar.setEnabled(true);
                    btnNueva.setEnabled(true);
                }
            }
        }.execute();
    }

    private void mostrarDatos() {
        modelo.setRowCount(0);
        for (Solicitud s : solicitudes) {
            modelo.addRow(new Object[]{
                "#" + s.id,
                s.solicitante,
                s.descripcion,
                capitalizar(s.categoria),
                capitalizar(s.prioridad),
                capitalizar(s.estado),
                s.tecnico,
                s.tiempoRespuesta + " h",
                s.tiempoResolucion + " h"
            });
        }
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void actualizarResumen() {
        Resultado r = new Resultado(solicitudes);
        panelMetricas.removeAll();
        panelMetricas.add(tarjeta("TOTAL", String.valueOf(solicitudes.size()),
            "Solicitudes registradas", TEXTO));
        panelMetricas.add(tarjeta("ASIGNADAS", String.valueOf(r.asignadas),
            "Con técnico responsable", VERDE));
        panelMetricas.add(tarjeta("EN COLA", String.valueOf(r.enCola),
            "Pendientes de asignación", AMARILLO));
        panelMetricas.revalidate();
        panelMetricas.repaint();

        lblPrioridades.setText(String.format(
            "Críticas: %d   ·   Altas: %d   ·   Medias: %d   ·   Bajas: %d",
            r.prioridades.get("critica"), r.prioridades.get("alta"),
            r.prioridades.get("media"), r.prioridades.get("baja")
        ));

        panelTecnicos.removeAll();
        for (Map.Entry<String, String> entrada : TECNICOS.entrySet()) {
            boolean ocupado = solicitudes.stream().anyMatch(s ->
                s.estado.equals("asignada") && s.tecnico.equals(entrada.getValue())
            );
            JPanel bloque = new JPanel();
            bloque.setBackground(TARJETA);
            bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
            bloque.setBorder(new EmptyBorder(9, 10, 9, 10));
            bloque.setAlignmentX(Component.LEFT_ALIGNMENT);
            bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

            JLabel nombre = label(entrada.getValue(), 10, TEXTO, true);
            JLabel categoria = label(capitalizar(entrada.getKey()), 9, SECUNDARIO, false);
            JLabel disponibilidad = label(ocupado ? "● Ocupado" : "● Disponible",
                9, ocupado ? new Color(252, 165, 165) : VERDE, true);
            nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
            categoria.setAlignmentX(Component.LEFT_ALIGNMENT);
            disponibilidad.setAlignmentX(Component.LEFT_ALIGNMENT);
            bloque.add(nombre);
            bloque.add(Box.createVerticalStrut(2));
            bloque.add(categoria);
            bloque.add(Box.createVerticalStrut(4));
            bloque.add(disponibilidad);
            panelTecnicos.add(bloque);
            panelTecnicos.add(Box.createVerticalStrut(7));
        }
        panelTecnicos.revalidate();
        panelTecnicos.repaint();
    }

    private void nuevaSolicitud() {
        JTextField solicitante = new JTextField();
        JComboBox<String> categoria = new JComboBox<>(new String[]{"hardware", "software", "red"});
        JComboBox<String> prioridad = new JComboBox<>(new String[]{"critica", "alta", "media", "baja"});
        JTextArea descripcion = new JTextArea(4, 24);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);

        JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));
        formulario.add(new JLabel("Solicitante"));
        formulario.add(solicitante);
        formulario.add(new JLabel("Categoría"));
        formulario.add(categoria);
        formulario.add(new JLabel("Prioridad"));
        formulario.add(prioridad);
        formulario.add(new JLabel("Descripción"));
        formulario.add(new JScrollPane(descripcion));

        int opcion = JOptionPane.showConfirmDialog(this, formulario,
            "Registrar nueva solicitud", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        String nombre = solicitante.getText().trim();
        String detalle = descripcion.getText().trim();
        if (nombre.isEmpty() || detalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe el solicitante y la descripción.",
                "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nombre.contains(",") || detalle.contains(",") ||
            nombre.contains("\n") || detalle.contains("\n")) {
            JOptionPane.showMessageDialog(this,
                "Por ahora no uses comas ni saltos de línea en esos campos del CSV.",
                "Caracteres no permitidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Path archivo = buscarCSV();
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            int nuevoId = 1;
            for (int i = 1; i < lineas.size(); i++) {
                if (lineas.get(i).isBlank()) continue;
                String[] partes = lineas.get(i).split(",", -1);
                if (partes.length > 0) nuevoId = Math.max(nuevoId, Integer.parseInt(partes[0].trim()) + 1);
            }
            String fila = nuevoId + "," + nombre + "," + categoria.getSelectedItem() + "," +
                prioridad.getSelectedItem() + ",abierta," + detalle + System.lineSeparator();
            Files.writeString(archivo, fila, StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND);
            actualizarDatos();
            JOptionPane.showMessageDialog(this, "La solicitud #" + nuevoId + " se registró correctamente.",
                "Solicitud registrada", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "No se pudo guardar la solicitud", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class RendererPrioridad extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(new Color(81, 64, 120));
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(SUPERFICIE);
                String prioridad = String.valueOf(table.getValueAt(row, 4)).toLowerCase();
                switch (prioridad) {
                    case "crítica":
                    case "critica":
                        c.setForeground(new Color(252, 165, 165));
                        break;
                    case "alta":
                        c.setForeground(new Color(253, 186, 116));
                        break;
                    case "media":
                        c.setForeground(new Color(253, 230, 138));
                        break;
                    default:
                        c.setForeground(new Color(167, 243, 208));
                        break;
                }
            }
            setHorizontalAlignment(column == 2 || column == 1 || column == 6
                ? SwingConstants.LEFT : SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, 7, 0, 7));
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new Main().setVisible(true);
        });
    }
}
