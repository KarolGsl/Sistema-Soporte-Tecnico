# Sistema de gestión y priorización de solicitudes de soporte técnico

Proyecto comparativo implementado en **C++17, Python 3 y Java 17**. Las tres versiones leen el mismo CSV y aplican las mismas reglas de negocio.

## Estructura
- `datos/solicitudes.csv`: conjunto de datos compartido.
- `cpp/`: implementación C++.
- `python/`: implementación Python.
- `java/`: implementación Java.
- `documentos/`: espacio para análisis, diagramas y evidencias.

## Reglas de negocio comunes
- Prioridad: `critica`, `alta`, `media`, `baja` (en ese orden).
- Tiempo máximo objetivo de primera respuesta: crítica 1 h, alta 2 h, media 8 h, baja 24 h.
- Una solicitud crítica se procesa antes que las demás.
- Se asigna el primer técnico disponible con la especialidad correspondiente; si no existe, queda en cola.
- Tiempo estimado de resolución: crítica 4 h, alta 3 h, media 2 h, baja 1 h.
- Las solicitudes se ordenan por prioridad y, en empate, por ID.
- El procesamiento concurrente calcula el tiempo estimado de cada solicitud; la asignación final se realiza de forma ordenada para conservar resultados deterministas.

## Ejecutar

### Python
```bash
cd python
python main.py
```

### C++
Requiere un compilador compatible con C++17 (por ejemplo, g++).
```bash
cd cpp
g++ -std=c++17 -pthread main.cpp -o soporte
./soporte
```
En PowerShell:
```powershell
g++ -std=c++17 -pthread main.cpp -o soporte.exe
.\soporte.exe
```

### Java
Requiere JDK 17 o posterior.
```bash
cd java
javac -d out src/Main.java
java -cp out Main
```

## Datos
El archivo CSV usa las columnas:
`id,solicitante,categoria,prioridad,estado,descripcion`

## Importante
Este repositorio es una base funcional inicial. Antes de entregar, el equipo debe ejecutar las tres versiones, registrar resultados medidos en el mismo equipo, completar pruebas automatizadas y redactar el análisis comparativo con sus propias evidencias.
