import csv
import tkinter as tk
from tkinter import ttk, messagebox
from pathlib import Path
import sys

# Permite importar main.py aunque la aplicación se ejecute desde otra carpeta.
CARPETA = Path(__file__).resolve().parent
if str(CARPETA) not in sys.path:
    sys.path.insert(0, str(CARPETA))

from main import (
    DATA,
    PRIORIDAD,
    TECNICOS,
    cargar_solicitudes,
    procesar_concurrentemente,
    asignar_tecnicos,
    generar_estadisticas,
)

FONDO = "#11111A"
SUPERFICIE = "#1B1B29"
TARJETA = "#242438"
PRIMARIO = "#8B5CF6"
TEXTO = "#F4F4F5"
SECUNDARIO = "#A1A1AA"
BORDE = "#38384D"

class AplicacionSoporte(tk.Tk):
    def __init__(self):
        super().__init__()
        self.title("Sistema de Soporte Técnico | Python")
        self.geometry("1220x760")
        self.minsize(980, 650)
        self.configure(bg=FONDO)

        self._configurar_estilos()
        self._construir_interfaz()
        self.actualizar_datos()

    def _configurar_estilos(self):
        estilo = ttk.Style(self)
        estilo.theme_use("clam")
        estilo.configure(
            "Treeview",
            background=SUPERFICIE,
            foreground=TEXTO,
            fieldbackground=SUPERFICIE,
            rowheight=34,
            borderwidth=0,
            font=("Segoe UI", 9),
        )
        estilo.configure(
            "Treeview.Heading",
            background=TARJETA,
            foreground=TEXTO,
            relief="flat",
            font=("Segoe UI", 9, "bold"),
            padding=(8, 10),
        )
        estilo.map(
            "Treeview",
            background=[("selected", "#514078")],
            foreground=[("selected", "#FFFFFF")],
        )
        estilo.map("Treeview.Heading", background=[("active", "#30304A")])

    def _construir_interfaz(self):
        encabezado = tk.Frame(self, bg=FONDO)
        encabezado.pack(fill="x", padx=26, pady=(22, 14))

        tk.Label(
            encabezado, text="SOPORTE TÉCNICO",
            bg=FONDO, fg=TEXTO, font=("Segoe UI", 20, "bold")
        ).pack(side="left")
        tk.Label(
            encabezado, text="Panel de administración · Python",
            bg=FONDO, fg=SECUNDARIO, font=("Segoe UI", 10)
        ).pack(side="left", padx=(14, 0), pady=(7, 0))

        acciones = tk.Frame(encabezado, bg=FONDO)
        acciones.pack(side="right")
        self._boton(acciones, "＋ Nueva solicitud", self.nueva_solicitud, principal=True).pack(side="left", padx=(0, 8))
        self._boton(acciones, "↻ Actualizar y asignar", self.actualizar_datos).pack(side="left")

        self.panel_metricas = tk.Frame(self, bg=FONDO)
        self.panel_metricas.pack(fill="x", padx=26, pady=(0, 16))

        contenido = tk.Frame(self, bg=FONDO)
        contenido.pack(fill="both", expand=True, padx=26, pady=(0, 20))

        izquierda = tk.Frame(contenido, bg=FONDO)
        izquierda.pack(side="left", fill="both", expand=True)

        barra = tk.Frame(izquierda, bg=FONDO)
        barra.pack(fill="x", pady=(0, 10))
        tk.Label(
            barra, text="Solicitudes de soporte",
            bg=FONDO, fg=TEXTO, font=("Segoe UI", 13, "bold")
        ).pack(side="left")
        self.lbl_total = tk.Label(barra, text="", bg=FONDO, fg=SECUNDARIO, font=("Segoe UI", 9))
        self.lbl_total.pack(side="right")

        tabla_frame = tk.Frame(izquierda, bg=SUPERFICIE, highlightbackground=BORDE, highlightthickness=1)
        tabla_frame.pack(fill="both", expand=True)

        columnas = ("id", "solicitante", "descripcion", "categoria", "prioridad", "estado", "tecnico", "respuesta", "resolucion")
        self.tabla = ttk.Treeview(tabla_frame, columns=columnas, show="headings")
        encabezados = {
            "id": ("ID", 45), "solicitante": ("Solicitante", 95),
            "descripcion": ("Descripción", 190), "categoria": ("Categoría", 85),
            "prioridad": ("Prioridad", 75), "estado": ("Estado", 75),
            "tecnico": ("Técnico", 110), "respuesta": ("Respuesta", 85),
            "resolucion": ("Resolución", 85),
        }
        for clave, (titulo, ancho) in encabezados.items():
            self.tabla.heading(clave, text=titulo)
            self.tabla.column(clave, width=ancho, minwidth=42, anchor="center" if clave not in ("descripcion", "solicitante", "tecnico") else "w")
        self.tabla.tag_configure("critica", foreground="#FCA5A5")
        self.tabla.tag_configure("alta", foreground="#FDBA74")
        self.tabla.tag_configure("media", foreground="#FDE68A")
        self.tabla.tag_configure("baja", foreground="#A7F3D0")

        scroll_y = ttk.Scrollbar(tabla_frame, orient="vertical", command=self.tabla.yview)
        scroll_x = ttk.Scrollbar(tabla_frame, orient="horizontal", command=self.tabla.xview)
        self.tabla.configure(yscrollcommand=scroll_y.set, xscrollcommand=scroll_x.set)
        self.tabla.grid(row=0, column=0, sticky="nsew")
        scroll_y.grid(row=0, column=1, sticky="ns")
        scroll_x.grid(row=1, column=0, sticky="ew")
        tabla_frame.grid_rowconfigure(0, weight=1)
        tabla_frame.grid_columnconfigure(0, weight=1)

        derecha = tk.Frame(contenido, bg=SUPERFICIE, width=230, highlightbackground=BORDE, highlightthickness=1)
        derecha.pack(side="right", fill="y", padx=(16, 0))
        derecha.pack_propagate(False)
        tk.Label(
            derecha, text="Técnicos", bg=SUPERFICIE, fg=TEXTO,
            font=("Segoe UI", 12, "bold")
        ).pack(anchor="w", padx=16, pady=(16, 4))
        tk.Label(
            derecha, text="Disponibilidad por especialidad",
            bg=SUPERFICIE, fg=SECUNDARIO, font=("Segoe UI", 8)
        ).pack(anchor="w", padx=16, pady=(0, 12))
        self.panel_tecnicos = tk.Frame(derecha, bg=SUPERFICIE)
        self.panel_tecnicos.pack(fill="x", padx=12)

        self.lbl_estado = tk.Label(
            self, text="Listo", bg=FONDO, fg=SECUNDARIO,
            font=("Segoe UI", 8), anchor="w"
        )
        self.lbl_estado.pack(fill="x", padx=28, pady=(0, 10))

    def _boton(self, padre, texto, comando, principal=False):
        color = PRIMARIO if principal else TARJETA
        boton = tk.Button(
            padre, text=texto, command=comando,
            bg=color, fg="#FFFFFF", activebackground="#7044CE",
            activeforeground="#FFFFFF", relief="flat", bd=0,
            padx=13, pady=9, cursor="hand2",
            font=("Segoe UI", 9, "bold")
        )
        return boton

    def _tarjeta_metrica(self, padre, titulo, valor, detalle, color):
        tarjeta = tk.Frame(padre, bg=SUPERFICIE, highlightbackground=BORDE, highlightthickness=1)
        tarjeta.pack(side="left", fill="both", expand=True, padx=(0, 10))
        tk.Label(tarjeta, text=titulo, bg=SUPERFICIE, fg=SECUNDARIO, font=("Segoe UI", 9)).pack(anchor="w", padx=15, pady=(12, 2))
        tk.Label(tarjeta, text=str(valor), bg=SUPERFICIE, fg=color, font=("Segoe UI", 22, "bold")).pack(anchor="w", padx=15)
        tk.Label(tarjeta, text=detalle, bg=SUPERFICIE, fg=SECUNDARIO, font=("Segoe UI", 8)).pack(anchor="w", padx=15, pady=(0, 12))

    def actualizar_datos(self):
        try:
            solicitudes = cargar_solicitudes()
            procesar_concurrentemente(solicitudes)
            asignar_tecnicos(solicitudes)
            solicitudes.sort(key=lambda s: (PRIORIDAD[s["prioridad"]], s["id"]))
            estadisticas = generar_estadisticas(solicitudes)
        except Exception as error:
            messagebox.showerror("Error al cargar", str(error))
            self.lbl_estado.config(text="No se pudieron cargar las solicitudes")
            return

        for elemento in self.tabla.get_children():
            self.tabla.delete(elemento)

        for s in solicitudes:
            self.tabla.insert(
                "", "end",
                values=(
                    f"#{s['id']}", s["solicitante"], s["descripcion"],
                    s["categoria"].capitalize(), s["prioridad"].capitalize(),
                    s["estado"].capitalize(), s["tecnico"],
                    f"{s['tiempo_respuesta_h']} h", f"{s['tiempo_estimado_h']} h"
                ),
                tags=(s["prioridad"],)
            )

        for widget in self.panel_metricas.winfo_children():
            widget.destroy()

        self._tarjeta_metrica(self.panel_metricas, "TOTAL", estadisticas["total"], "Solicitudes registradas", TEXTO)
        self._tarjeta_metrica(self.panel_metricas, "ASIGNADAS", estadisticas["asignadas"], "Con técnico responsable", "#86EFAC")
        self._tarjeta_metrica(self.panel_metricas, "EN COLA", estadisticas["en_cola"], "Pendientes de asignación", "#FCD34D")

        conteo = estadisticas["por_prioridad"]
        for widget in self.panel_tecnicos.winfo_children():
            widget.destroy()

        asignados = {s.get("tecnico") for s in solicitudes if s["estado"] == "asignada"}
        for tecnico in TECNICOS:
            ocupado = tecnico["nombre"] in asignados
            bloque = tk.Frame(self.panel_tecnicos, bg=TARJETA)
            bloque.pack(fill="x", pady=4)
            tk.Label(
                bloque, text=tecnico["nombre"], bg=TARJETA, fg=TEXTO,
                font=("Segoe UI", 9, "bold")
            ).pack(anchor="w", padx=10, pady=(8, 1))
            tk.Label(
                bloque, text=tecnico["categoria"].capitalize(),
                bg=TARJETA, fg=SECUNDARIO, font=("Segoe UI", 8)
            ).pack(anchor="w", padx=10)
            tk.Label(
                bloque, text="● Ocupado" if ocupado else "● Disponible",
                bg=TARJETA, fg="#FCA5A5" if ocupado else "#86EFAC",
                font=("Segoe UI", 8)
            ).pack(anchor="w", padx=10, pady=(2, 8))

        prioridad_texto = "  ·  ".join(
            f"{nombre.capitalize()}: {conteo.get(nombre, 0)}"
            for nombre in ("critica", "alta", "media", "baja")
        )
        self.lbl_total.config(text=f"{prioridad_texto}")
        self.lbl_estado.config(text="Datos actualizados. Asignación calculada por prioridad e ID.")

    def nueva_solicitud(self):
        ventana = tk.Toplevel(self)
        ventana.title("Nueva solicitud")
        ventana.configure(bg=FONDO)
        ventana.resizable(False, False)
        ventana.transient(self)
        ventana.grab_set()

        campos = {}
        opciones = {
            "categoria": ["hardware", "software", "red"],
            "prioridad": ["critica", "alta", "media", "baja"],
        }

        tk.Label(
            ventana, text="Registrar solicitud",
            bg=FONDO, fg=TEXTO, font=("Segoe UI", 15, "bold")
        ).pack(anchor="w", padx=22, pady=(20, 14))

        formulario = tk.Frame(ventana, bg=FONDO)
        formulario.pack(fill="both", padx=22)

        def campo_texto(nombre, etiqueta):
            tk.Label(formulario, text=etiqueta, bg=FONDO, fg=SECUNDARIO, font=("Segoe UI", 9)).pack(anchor="w", pady=(7, 3))
            entrada = tk.Entry(formulario, bg=SUPERFICIE, fg=TEXTO, insertbackground=TEXTO, relief="flat", width=38, font=("Segoe UI", 10))
            entrada.pack(fill="x", ipady=7)
            campos[nombre] = entrada

        campo_texto("solicitante", "Solicitante")

        for nombre, etiqueta in (("categoria", "Categoría"), ("prioridad", "Prioridad")):
            tk.Label(formulario, text=etiqueta, bg=FONDO, fg=SECUNDARIO, font=("Segoe UI", 9)).pack(anchor="w", pady=(9, 3))
            variable = tk.StringVar(value=opciones[nombre][0])
            combo = ttk.Combobox(formulario, textvariable=variable, values=opciones[nombre], state="readonly")
            combo.pack(fill="x", ipady=3)
            campos[nombre] = variable

        tk.Label(formulario, text="Descripción", bg=FONDO, fg=SECUNDARIO, font=("Segoe UI", 9)).pack(anchor="w", pady=(9, 3))
        descripcion = tk.Text(formulario, height=4, bg=SUPERFICIE, fg=TEXTO, insertbackground=TEXTO, relief="flat", font=("Segoe UI", 10), wrap="word")
        descripcion.pack(fill="x", pady=(0, 14))

        def guardar():
            solicitante = campos["solicitante"].get().strip()
            detalle = descripcion.get("1.0", "end").strip()
            categoria = campos["categoria"].get()
            prioridad = campos["prioridad"].get()
            if not solicitante or not detalle:
                messagebox.showwarning("Datos incompletos", "Escribe el solicitante y la descripción.", parent=ventana)
                return
            try:
                DATA.parent.mkdir(parents=True, exist_ok=True)
                with open(DATA, "r", newline="", encoding="utf-8") as archivo:
                    filas = list(csv.DictReader(archivo))
                nuevo_id = max((int(fila["id"]) for fila in filas), default=0) + 1
                with open(DATA, "a", newline="", encoding="utf-8") as archivo:
                    escritor = csv.writer(archivo)
                    escritor.writerow([nuevo_id, solicitante, categoria, prioridad, "abierta", detalle])
                ventana.destroy()
                self.actualizar_datos()
                messagebox.showinfo("Solicitud registrada", f"La solicitud #{nuevo_id} se agregó correctamente.")
            except (OSError, ValueError, KeyError) as error:
                messagebox.showerror("No se pudo guardar", str(error), parent=ventana)

        pie = tk.Frame(ventana, bg=FONDO)
        pie.pack(fill="x", padx=22, pady=(0, 20))
        self._boton(pie, "Guardar solicitud", guardar, principal=True).pack(side="right")
        tk.Button(
            pie, text="Cancelar", command=ventana.destroy,
            bg=FONDO, fg=SECUNDARIO, activebackground=FONDO,
            activeforeground=TEXTO, relief="flat", padx=12, pady=9
        ).pack(side="right", padx=(0, 8))


if __name__ == "__main__":
    app = AplicacionSoporte()
    app.mainloop()
