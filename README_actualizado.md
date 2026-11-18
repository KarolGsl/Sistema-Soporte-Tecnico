# Sistema de gestión y priorización de solicitudes de soporte técnico

Proyecto académico que implementa un sistema para organizar y priorizar solicitudes de soporte técnico. El análisis comparativo documenta las soluciones desarrolladas en **C# 12, Python 3 y Java 17**.

## Objetivo

Organizar solicitudes de soporte técnico según su prioridad, calcular tiempos de atención y asignar solicitudes a técnicos disponibles, manteniendo reglas de funcionamiento equivalentes para comparar los lenguajes.

## Estructura del proyecto

```text
Sistema-Soporte-Tecnico/
├── csharp/                 # Implementación en C#
├── python/                 # Implementación en Python
├── java/                   # Implementación en Java
├── datos/
│   └── solicitudes.csv     # Datos de prueba
├── documentos/
│   ├── Analisis_Comparativo_Final_con_Evidencias.pdf
│   └── Analisis_Comparativo_Final_con_Evidencias.md
├── evidencias/             # Capturas de ejecución y pruebas
└── README.md
```

> Nota: la carpeta de C# debe llamarse `csharp/`. Si en tu copia local todavía aparece como `cpp/`, renómbrala únicamente después de confirmar que contiene la implementación convertida a C#.

## Reglas de funcionamiento

- Se manejan cuatro niveles de prioridad: crítica, alta, media y baja.
- Las solicitudes se ordenan por prioridad; en caso de empate, se utiliza el ID en orden ascendente.
- La asignación por categoría es:
  - Hardware: Ana.
  - Software: Luis.
  - Red: Mario.
- Cada técnico puede atender una solicitud a la vez; las demás permanecen en espera.
- Tiempo máximo de primera respuesta:
  - Crítica: 1 hora.
  - Alta: 2 horas.
  - Media: 8 horas.
  - Baja: 24 horas.
- Tiempo estimado de resolución:
  - Crítica: 4 horas.
  - Alta: 3 horas.
  - Media: 2 horas.
  - Baja: 1 hora.

## Datos de prueba

El archivo `datos/solicitudes.csv` contiene ocho solicitudes, dos por cada nivel de prioridad.

Columnas:

```text
id,solicitante,categoria,prioridad,estado,descripcion
```

## Resultados esperados

Con el conjunto de datos de prueba se esperan:

- 8 solicitudes en total.
- 3 solicitudes asignadas.
- 5 solicitudes en espera.
- 2 solicitudes por cada nivel de prioridad.

## Ejecución

### Python

Se requiere Python 3. Desde la carpeta raíz:

```powershell
python python/main.py
```

Para ejecutar las pruebas automatizadas:

```powershell
python -m unittest discover -s python -v
```

### C#

Se requiere el SDK de .NET. Abre la solución o el proyecto de C# ubicado en `csharp/` con Visual Studio o Visual Studio Code. Si contiene un archivo `.csproj`, ejecuta desde esa carpeta:

```powershell
dotnet run
```

### Java

Se requiere JDK 17 o posterior. Desde la carpeta raíz:

```powershell
cd java
javac -d out src/Main.java
java -cp out Main
```

## Documentación y evidencias

En `documentos/` se encuentra el análisis comparativo final en PDF y Markdown.

En `evidencias/` se incluyen capturas de ejecución y pruebas. Las pruebas automatizadas documentadas corresponden a Python; no se deben interpretar como una suite equivalente para los tres lenguajes.

## Tecnologías

- C# 12
- Python 3
- Java 17
- CSV
- Git y GitHub
