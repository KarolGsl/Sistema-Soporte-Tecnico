import csv
import json
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path
from collections import Counter

BASE = Path(__file__).resolve().parents[1]
DATA = BASE / "datos" / "solicitudes.csv"
PRIORIDAD = {"critica": 0, "alta": 1, "media": 2, "baja": 3}
TIEMPO_RESPUESTA = {"critica": 1, "alta": 2, "media": 8, "baja": 24}
TIEMPO_RESOLUCION = {"critica": 4, "alta": 3, "media": 2, "baja": 1}
TECNICOS = [
    {"nombre": "Técnico Ana", "categoria": "hardware"},
    {"nombre": "Técnico Luis", "categoria": "software"},
    {"nombre": "Técnico Mario", "categoria": "red"},
]

def cargar_solicitudes(ruta=DATA):
    with open(ruta, newline="", encoding="utf-8") as archivo:
        solicitudes = list(csv.DictReader(archivo))
    for s in solicitudes:
        s["id"] = int(s["id"])
        if s["prioridad"] not in PRIORIDAD:
            raise ValueError(f"Prioridad inválida en solicitud {s['id']}")
    return solicitudes

def calcular_tiempo(solicitud):
    return TIEMPO_RESOLUCION[solicitud["prioridad"]]

def procesar_concurrentemente(solicitudes):
    with ThreadPoolExecutor() as executor:
        tiempos = list(executor.map(calcular_tiempo, solicitudes))
    for solicitud, tiempo in zip(solicitudes, tiempos):
        solicitud["tiempo_estimado_h"] = tiempo

def asignar_tecnicos(solicitudes):
    disponibles = TECNICOS.copy()
    for s in sorted(solicitudes, key=lambda x: (PRIORIDAD[x["prioridad"]], x["id"])):
        tecnico = next((t for t in disponibles if t["categoria"] == s["categoria"]), None)
        if tecnico and s["estado"] == "abierta":
            s["tecnico"] = tecnico["nombre"]
            s["estado"] = "asignada"
            disponibles.remove(tecnico)
        else:
            s["tecnico"] = "En cola"

def generar_estadisticas(solicitudes):
    return {
        "total": len(solicitudes),
        "por_prioridad": dict(Counter(s["prioridad"] for s in solicitudes)),
        "asignadas": sum(s["estado"] == "asignada" for s in solicitudes),
        "en_cola": sum(s["tecnico"] == "En cola" for s in solicitudes),
    }

def main():
    solicitudes = cargar_solicitudes()
    procesar_concurrentemente(solicitudes)
    asignar_tecnicos(solicitudes)
    solicitudes.sort(key=lambda x: (PRIORIDAD[x["prioridad"]], x["id"]))
    print("=== SISTEMA DE SOPORTE TÉCNICO (PYTHON) ===")
    for s in solicitudes:
        print(f"#{s['id']} | {s['prioridad'].upper():7} | {s['categoria']:8} | {s['estado']:8} | {s['tecnico']:14} | ETA {s['tiempo_estimado_h']} h")
    print("\nESTADÍSTICAS")
    print(json.dumps(generar_estadisticas(solicitudes), ensure_ascii=False, indent=2))

if __name__ == "__main__":
    try:
        main()
    except (OSError, ValueError, KeyError) as error:
        print(f"Error: {error}")
