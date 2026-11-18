# Sistema de gestión y priorización de solicitudes de soporte técnico

Este proyecto consiste en un sistema que permite organizar y priorizar solicitudes de soporte técnico de acuerdo con su nivel de urgencia. Se desarrollaron tres versiones del programa utilizando C++17, Python 3 y Java, con el propósito de aplicar las mismas reglas y comparar su funcionamiento.

## Estructura del proyecto

- `datos/solicitudes.csv`: archivo que contiene las solicitudes utilizadas para las pruebas.
- `cpp/`: código fuente de la versión en C++.
- `python/`: código fuente y pruebas de la versión en Python.
- `java/`: código fuente de la versión en Java.
- `documentos/`: análisis comparativo, manual técnico y diagramas del sistema.
- `evidencias/`: archivos que muestran los resultados de las pruebas y ejecuciones.

## Reglas de funcionamiento

El sistema utiliza cuatro niveles de prioridad: crítica, alta, media y baja. Las solicitudes se organizan de mayor a menor prioridad y, cuando tienen el mismo nivel, se toma en cuenta su ID para establecer el orden.

Cada categoría cuenta con un técnico responsable:

- Hardware: Técnico Ana.
- Software: Técnico Luis.
- Red: Técnico Mario.

Cada técnico puede atender una solicitud a la vez. Cuando llega otra solicitud de la misma categoría, esta permanece en la cola de espera.

Los tiempos establecidos para cada nivel de prioridad son los siguientes:

| Prioridad | Tiempo máximo de primera respuesta | Tiempo estimado de resolución |
| --------- | ---------------------------------: | ----------------------------: |
| Crítica   |                             1 hora |                       4 horas |
| Alta      |                            2 horas |                       3 horas |
| Media     |                            8 horas |                       2 horas |
| Baja      |                           24 horas |                        1 hora |

El programa también utiliza procesamiento concurrente para calcular los tiempos de las solicitudes.

## Cómo ejecutar el proyecto

Antes de ejecutar cualquiera de las versiones, es necesario contar con el entorno correspondiente instalado.

### Python

Se necesita Python 3.

Desde la carpeta principal del proyecto, ejecutar:

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

En Windows PowerShell:

```powershell
g++ -std=c++17 -pthread main.cpp -o soporte.exe
.\soporte.exe
```

### Java

Se necesita JDK 17 o una versión posterior.

Desde la carpeta principal:

```bash
cd java
javac -d out src/Main.java
java -cp out Main
```

## Archivo de datos

Las solicitudes se almacenan en `datos/solicitudes.csv`.

El archivo contiene las siguientes columnas:

`id,solicitante,categoria,prioridad,estado,descripcion`

Para las pruebas se utilizaron ocho solicitudes, dos por cada nivel de prioridad.

## Resultados esperados

Al ejecutar el sistema con los datos de prueba, se espera obtener:

- 8 solicitudes registradas.
- 3 solicitudes asignadas a técnicos.
- 5 solicitudes en cola de espera.
- 2 solicitudes por cada nivel de prioridad.

## Documentación y evidencias

Dentro de la carpeta `documentos/` se encuentran el análisis comparativo, el manual técnico y los diagramas utilizados para explicar el funcionamiento y la estructura del sistema.

La carpeta `evidencias/` contiene los archivos generados durante las pruebas y ejecuciones.

En Python se ejecutaron cinco pruebas automatizadas, todas con resultados satisfactorios. La versión en Java también se compiló y ejecutó correctamente. En C++ se comprobó la lógica de priorización y asignación mediante una versión de prueba con los datos integrados; queda pendiente verificar la lectura del archivo CSV en el código original.



Gaytan Rivas Karol Emilio

Ponce Jimenez Daniel Alemar
