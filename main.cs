
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

class Program
{
    static string ResolverRutaCSV()
    {
        string[] rutas =
        {
            "datos/solicitudes.csv",
            "../datos/solicitudes.csv",
            "../../datos/solicitudes.csv",
            "../../../datos/solicitudes.csv"
        };

        foreach (string ruta in rutas)
        {
            if (File.Exists(ruta))
            {
                return ruta;
            }
        }

        throw new Exception(
            "No se encontro datos/solicitudes.csv. " +
            "Verifica la carpeta del proyecto."
        );
    }

    class Solicitud
    {
        public int Id { get; set; }
        public string Solicitante { get; set; } = "";
        public string Categoria { get; set; } = "";
        public string Prioridad { get; set; } = "";
        public string Estado { get; set; } = "";
        public string Descripcion { get; set; } = "";
        public string Tecnico { get; set; } = "En cola";
        public int TiempoEstimado { get; set; } = 0;
        public int TiempoRespuesta { get; set; } = 0;
    }

    static Dictionary<string, int> PRIORIDAD = new()
    {
        { "critica", 0 },
        { "alta", 1 },
        { "media", 2 },
        { "baja", 3 }
    };

    static Dictionary<string, int> TIEMPO_RESOLUCION = new()
    {
        { "critica", 4 },
        { "alta", 3 },
        { "media", 2 },
        { "baja", 1 }
    };

    static Dictionary<string, int> TIEMPO_RESPUESTA = new()
    {
        { "critica", 1 },
        { "alta", 2 },
        { "media", 8 },
        { "baja", 24 }
    };

    static int CalcularTiempo(string prioridad)
    {
        return TIEMPO_RESOLUCION[prioridad];
    }

    static List<Solicitud> CargarSolicitudes(string ruta)
    {
        if (!File.Exists(ruta))
        {
            throw new Exception("No se pudo abrir el archivo CSV: " + ruta);
        }

        List<Solicitud> solicitudes = new();

        string[] lineas = File.ReadAllLines(ruta);

        // Omitir encabezado
        foreach (string linea in lineas.Skip(1))
        {
            if (string.IsNullOrWhiteSpace(linea))
                continue;

            string[] campos = linea.Split(',');

            if (campos.Length != 6)
            {
                throw new Exception("Registro CSV invalido");
            }

            Solicitud s = new Solicitud
            {
                Id = int.Parse(campos[0]),
                Solicitante = campos[1],
                Categoria = campos[2],
                Prioridad = campos[3].Trim().ToLower(),
                Estado = campos[4],
                Descripcion = campos[5]
            };

            if (!PRIORIDAD.ContainsKey(s.Prioridad))
            {
                throw new Exception(
                    "Prioridad invalida en solicitud " + campos[0]
                );
            }

            s.TiempoRespuesta = TIEMPO_RESPUESTA[s.Prioridad];

            solicitudes.Add(s);
        }

        return solicitudes;
    }

    static async Task ProcesarConcurrentemente(
        List<Solicitud> solicitudes)
    {
        List<Task<int>> tareas = new();

        foreach (var s in solicitudes)
        {
            tareas.Add(Task.Run(() =>
                CalcularTiempo(s.Prioridad)
            ));
        }

        for (int i = 0; i < solicitudes.Count; i++)
        {
            solicitudes[i].TiempoEstimado = await tareas[i];
        }
    }

    static void AsignarTecnicos(List<Solicitud> solicitudes)
    {
        Dictionary<string, string> tecnicos = new()
        {
            { "hardware", "Tecnico Ana" },
            { "software", "Tecnico Luis" },
            { "red", "Tecnico Mario" }
        };

        var ordenadas = solicitudes
            .OrderBy(s => PRIORIDAD[s.Prioridad])
            .ThenBy(s => s.Id)
            .ToList();

        solicitudes.Clear();
        solicitudes.AddRange(ordenadas);

        foreach (var s in solicitudes)
        {
            if (tecnicos.ContainsKey(s.Categoria) &&
                s.Estado == "abierta")
            {
                s.Tecnico = tecnicos[s.Categoria];
                s.Estado = "asignada";

                tecnicos.Remove(s.Categoria);
            }
            else
            {
                s.Tecnico = "En cola";
            }
        }
    }

    static void GenerarEstadisticas(List<Solicitud> solicitudes)
    {
        int asignadas = 0;
        int enCola = 0;

        Dictionary<string, int> conteo = new();

        foreach (var s in solicitudes)
        {
            if (!conteo.ContainsKey(s.Prioridad))
            {
                conteo[s.Prioridad] = 0;
            }

            conteo[s.Prioridad]++;

            if (s.Estado == "asignada")
            {
                asignadas++;
            }
            else
            {
                enCola++;
            }
        }

        Console.WriteLine("\n ESTADISTICAS");
        Console.WriteLine("Total: " + solicitudes.Count);
        Console.WriteLine("Asignadas: " + asignadas);
        Console.WriteLine("En cola: " + enCola);

        Console.WriteLine("\nPor prioridad:");

        foreach (var e in conteo.OrderBy(e => PRIORIDAD[e.Key]))
        {
            Console.WriteLine(e.Key + ": " + e.Value);
        }
    }

    static async Task Main(string[] args)
    {
        try
        {
            List<Solicitud> solicitudes =
                CargarSolicitudes(ResolverRutaCSV());

            await ProcesarConcurrentemente(solicitudes);

            AsignarTecnicos(solicitudes);

            Console.WriteLine(
                " SISTEMA DE SOPORTE TECNICO - C#\n"
            );

            foreach (var s in solicitudes)
            {
                Console.WriteLine("Solicitud #" + s.Id);
                Console.WriteLine("Solicitante: " + s.Solicitante);
                Console.WriteLine("Categoria: " + s.Categoria);
                Console.WriteLine("Prioridad: " + s.Prioridad);
                Console.WriteLine("Estado: " + s.Estado);
                Console.WriteLine("Tecnico: " + s.Tecnico);

                Console.WriteLine(
                    "Primera respuesta maxima: " +
                    s.TiempoRespuesta + " horas"
                );

                Console.WriteLine(
                    "Tiempo estimado de resolucion: " +
                    s.TiempoEstimado + " horas"
                );

                Console.WriteLine(
                    "----------------------------------------"
                );
            }

            GenerarEstadisticas(solicitudes);
        }
        catch (Exception e)
        {
            Console.WriteLine("Error: " + e.Message);
        }
    }
}
