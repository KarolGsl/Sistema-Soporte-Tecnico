# Sistema de gestión y priorización de solicitudes de soporte técnico

Proyecto desarrollado en C++17, Python 3 y Java 17. Las tres versiones utilizan el mismo archivo CSV y siguen las mismas reglas para organizar y asignar solicitudes de soporte técnico.

## Estructura del proyecto

- `datos/solicitudes.csv`: contiene las solicitudes de prueba.
- `cpp/`: implementación en C++.
- `python/`: implementación en Python.
- `java/`: implementación en Java.
- `documentos/`: contiene el análisis comparativo.
- `evidencias/`: contiene los resultados de las pruebas realizadas.

## Reglas de funcionamiento

- Las prioridades son crítica, alta, media y baja.
- Las solicitudes se ordenan de mayor a menor prioridad.
- Si dos solicitudes tienen la misma prioridad, se utiliza su ID para definir el orden.
- Cada categoría tiene un técnico asignado:
  - Hardware: Técnico Ana.
  - Software: Técnico Luis.
  - Red: Técnico Mario.
- Cada técnico puede atender una solicitud a la vez. Las demás permanecen en espera.
- El tiempo máximo de primera respuesta depende de la prioridad:
  - Crítica: 1 hora.
  - Alta: 2 horas.
  - Media: 8 horas.
  - Baja: 24 horas.
- El tiempo estimado de resolución es:
  - Crítica: 4 horas.
  - Alta: 3 horas.
  - Media: 2 horas.
  - Baja: 1 hora.

El procesamiento concurrente se utiliza para calcular los tiempos de las solicitudes. La asignación se realiza de manera ordenada para conservar los resultados esperados.

## Cómo ejecutar el proyecto

### Python

Se necesita tener Python 3 instalado.

Desde la carpeta principal:

```bash
python python/main.py
```

Para ejecutar las pruebas:

```bash
python -m unittest discover -s python -v
```

### C++

Se necesita un compilador compatible con C++17, como g++.

Desde la carpeta principal:

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

Se necesita JDK 17 o posterior.

Desde la carpeta principal:

```bash
cd java
javac -d out src/Main.java
java -cp out Main
```

## Archivo de datos

El sistema utiliza `datos/solicitudes.csv`.

Las columnas del archivo son:

`id,solicitante,categoria,prioridad,estado,descripcion`

El conjunto de prueba contiene ocho solicitudes, dos por cada nivel de prioridad.

## Resultados esperados

Con los datos incluidos, se espera obtener:

- 8 solicitudes en total.
- 3 solicitudes asignadas.
- 5 solicitudes en espera.
- 2 solicitudes por cada nivel de prioridad.

## Documentación y pruebas

En la carpeta `documentos/` se encuentra el análisis comparativo de las tres implementaciones.

En `evidencias/` se encuentran los resultados de las pruebas automatizadas realizadas en Python.

Las versiones de C++ y Java todavía requieren una compilación y ejecución final para comprobar su funcionamiento en el entorno local.
