
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <algorithm>
#include <map>
#include <future>
#include <stdexcept>

using namespace std;
string resolverRutaCSV() {
    vector<string> rutas = {
        "datos/solicitudes.csv",
        "../datos/solicitudes.csv",
        "../../datos/solicitudes.csv",
        "../../../datos/solicitudes.csv"
    };

    for (const string& ruta : rutas) {
        ifstream archivo(ruta);

        if (archivo.is_open()) {
            return ruta;
        }
    }

    throw runtime_error(
        "No se encontro datos/solicitudes.csv. "
        "Verifica la carpeta del proyecto."
    );
}

struct Solicitud {
    int id;
    string solicitante;
    string categoria;
    string prioridad;
    string estado;
    string descripcion;
    string tecnico = "En cola";
    int tiempoEstimado = 0;
    int tiempoRespuesta = 0;
};

map<string, int> PRIORIDAD = {
    {"critica", 0},
    {"alta", 1},
    {"media", 2},
    {"baja", 3}
};

map<string, int> TIEMPO_RESOLUCION = {
    {"critica", 4},
    {"alta", 3},
    {"media", 2},
    {"baja", 1}
};

map<string, int> TIEMPO_RESPUESTA = {
    {"critica", 1}, {"alta", 2}, {"media", 8}, {"baja", 24}
};

int calcularTiempo(const string& prioridad) {
    return TIEMPO_RESOLUCION.at(prioridad);
}

vector<Solicitud> cargarSolicitudes(const string& ruta) {
    ifstream archivo(ruta);

    if (!archivo.is_open()) {
        throw runtime_error("No se pudo abrir el archivo CSV: " + ruta);
    }

    vector<Solicitud> solicitudes;
    string linea;

    getline(archivo, linea); // Omitir encabezado

    while (getline(archivo, linea)) {
        stringstream ss(linea);
        vector<string> campos;
        string campo;

        while (getline(ss, campo, ',')) {
            campos.push_back(campo);
        }

        if (campos.size() != 6) {
            throw runtime_error("Registro CSV invalido");
        }

        Solicitud s;

        s.id = stoi(campos[0]);
        s.solicitante = campos[1];
        s.categoria = campos[2];
        s.prioridad = campos[3];
        s.estado = campos[4];
        s.descripcion = campos[5];

        if (PRIORIDAD.find(s.prioridad) == PRIORIDAD.end()) {
            throw runtime_error("Prioridad invalida en solicitud " + campos[0]);
        }

        s.tiempoRespuesta = TIEMPO_RESPUESTA.at(s.prioridad);
        solicitudes.push_back(s);
    }

    return solicitudes;
}

void procesarConcurrentemente(vector<Solicitud>& solicitudes) {
    vector<future<int>> tareas;

    for (const auto& s : solicitudes) {
        tareas.push_back(async(launch::async, [prioridad = s.prioridad]() {
            return calcularTiempo(prioridad);
        }));
    }

    for (size_t i = 0; i < solicitudes.size(); i++) {
        solicitudes[i].tiempoEstimado = tareas[i].get();
    }
}

void asignarTecnicos(vector<Solicitud>& solicitudes) {
    map<string, string> tecnicos = {
        {"hardware", "Tecnico Ana"},
        {"software", "Tecnico Luis"},
        {"red", "Tecnico Mario"}
    };

    sort(solicitudes.begin(), solicitudes.end(),
        [](const Solicitud& a, const Solicitud& b) {
            if (PRIORIDAD.at(a.prioridad) != PRIORIDAD.at(b.prioridad)) {
                return PRIORIDAD.at(a.prioridad) < PRIORIDAD.at(b.prioridad);
            }
            return a.id < b.id;
        }
    );

    for (auto& s : solicitudes) {
        auto tecnico = tecnicos.find(s.categoria);

        if (tecnico != tecnicos.end() && s.estado == "abierta") {
            s.tecnico = tecnico->second;
            s.estado = "asignada";
            tecnicos.erase(tecnico);
        } else {
            s.tecnico = "En cola";
        }
    }
}

void generarEstadisticas(const vector<Solicitud>& solicitudes) {
    int asignadas = 0;
    int enCola = 0;

    map<string, int> conteo;

    for (const auto& s : solicitudes) {
        conteo[s.prioridad]++;

        if (s.estado == "asignada") {
            asignadas++;
        } else {
            enCola++;
        }
    }

    cout << "\n ESTADISTICAS \n";
    cout << "Total: " << solicitudes.size() << "\n";
    cout << "Asignadas: " << asignadas << "\n";
    cout << "En cola: " << enCola << "\n";

    cout << "\nPor prioridad:\n";

    for (const auto& e : conteo) {
        cout << e.first << ": " << e.second << "\n";
    }
}

int main() {
    try {
        vector<Solicitud> solicitudes =
            cargarSolicitudes(resolverRutaCSV());

        procesarConcurrentemente(solicitudes);
        asignarTecnicos(solicitudes);

        cout << " SISTEMA DE SOPORTE TECNICO - C++\n";

        for (const auto& s : solicitudes) {
            cout << "Solicitud #" << s.id << "\n";
            cout << "Solicitante: " << s.solicitante << "\n";
            cout << "Categoria: " << s.categoria << "\n";
            cout << "Prioridad: " << s.prioridad << "\n";
            cout << "Estado: " << s.estado << "\n";
            cout << "Tecnico: " << s.tecnico << "\n";
            cout << "Primera respuesta maxima: " << s.tiempoRespuesta << " horas\n";
            cout << "Tiempo estimado de resolucion: " << s.tiempoEstimado << " horas\n";
            cout << "----------------------------------------\n";
        }

        generarEstadisticas(solicitudes);

    } catch (const exception& e) {
        cerr << "Error: " << e.what() << "\n";
        return 1;
    }

    return 0;
}
