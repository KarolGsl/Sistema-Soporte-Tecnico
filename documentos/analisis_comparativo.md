# Análisis comparativo de C++, Python y Java

## 1. Introducción

En este proyecto desarrollamos un sistema para gestionar solicitudes de soporte técnico. La idea principal es organizar los reportes de acuerdo con su prioridad y asignarlos a los técnicos disponibles, tomando en cuenta el tipo de problema.

El mismo sistema se realizó utilizando tres lenguajes de programación: C++, Python y Java. Esto nos permite comparar cómo se resuelve un mismo problema con diferentes herramientas y estructuras de cada lenguaje.

## 2. Problema que se busca resolver

En un área de soporte técnico pueden recibirse varias solicitudes al mismo tiempo. Si no existe un orden para atenderlas, los problemas más urgentes podrían quedarse esperando mientras se resuelven otros de menor importancia.

Por esta razón, el sistema organiza las solicitudes según su prioridad y utiliza reglas para asignarlas a los técnicos correspondientes.

## 3. Objetivo general

Desarrollar un sistema de gestión de solicitudes de soporte técnico en C++, Python y Java, aplicando estructuras de datos, algoritmos de ordenamiento y procesamiento concurrente.

### Objetivos específicos

- Registrar y leer solicitudes desde un archivo CSV.
- Clasificar los reportes por categoría y prioridad.
- Ordenar las solicitudes para atender primero las más urgentes.
- Asignar técnicos de acuerdo con el tipo de problema.
- Calcular tiempos estimados de atención y resolución.
- Mostrar estadísticas generales de las solicitudes.

## 4. Reglas de funcionamiento

El sistema utiliza cuatro niveles de prioridad:

| Prioridad | Tiempo máximo de primera respuesta | Tiempo estimado de resolución |
|---|---:|---:|
| Crítica | 1 hora | 4 horas |
| Alta | 2 horas | 3 horas |
| Media | 8 horas | 2 horas |
| Baja | 24 horas | 1 hora |

Las solicitudes se ordenan primero por prioridad y después por número de identificación, de menor a mayor.

La asignación de técnicos se realiza de acuerdo con la categoría:

- Hardware: Técnico Ana.
- Software: Técnico Luis.
- Red: Técnico Mario.

Cada técnico puede recibir una solicitud a la vez. Si ya tiene una asignada, las demás solicitudes de esa categoría permanecen en espera.

## 5. Datos utilizados

Para realizar las pruebas se utiliza un archivo llamado `solicitudes.csv`, que contiene ocho solicitudes de ejemplo.

Los datos incluyen el identificador, nombre del solicitante, categoría, prioridad, estado y descripción del problema.

Se incluyeron dos solicitudes por cada nivel de prioridad para comprobar que el ordenamiento funciona correctamente.

## 6. Funcionamiento general del sistema

Los tres programas siguen un proceso similar:

1. Leer las solicitudes del archivo CSV.
2. Revisar la prioridad de cada solicitud.
3. Ordenar los reportes según las reglas establecidas.
4. Calcular los tiempos correspondientes.
5. Procesar los cálculos de tiempo mediante tareas concurrentes.
6. Asignar los técnicos disponibles.
7. Mostrar las solicitudes asignadas y las que permanecen en espera.
8. Presentar estadísticas generales.

## 7. Implementación en C++

La versión de C++ utiliza estructuras como `vector` para almacenar las solicitudes y `map` para relacionar las prioridades con sus tiempos correspondientes.

Para ordenar los reportes se utiliza `sort`, tomando en cuenta la prioridad y el identificador de cada solicitud.

También se utiliza `async` y `future` para realizar los cálculos de tiempo mediante tareas concurrentes.

Una característica importante de esta implementación es el uso de tipos de datos y estructuras que permiten controlar de manera directa cómo se almacena y procesa la información.

## 8. Implementación en Python

La versión de Python utiliza diccionarios para representar las solicitudes y listas para organizar los registros.

El archivo CSV se procesa mediante el módulo `csv`, mientras que `Counter` permite obtener las estadísticas de prioridades.

Para el procesamiento concurrente se utiliza `ThreadPoolExecutor`, que permite ejecutar los cálculos de tiempo en diferentes tareas.

Esta implementación tiene una estructura más sencilla de leer y modificar, ya que Python ofrece herramientas que reducen la cantidad de código necesario para realizar ciertas operaciones.

## 9. Implementación en Java

La versión de Java utiliza `ArrayList` para almacenar las solicitudes y `Map` para relacionar las prioridades con sus respectivos tiempos.

El ordenamiento se realiza mediante `Comparator`, considerando primero la prioridad y después el identificador.

Para el procesamiento concurrente se utilizan herramientas como `ExecutorService` y `Future`, que permiten administrar las tareas y obtener sus resultados.

Java requiere declarar los tipos de datos y utilizar sus clases correspondientes, lo que hace que el código tenga una estructura más definida.

## 10. Procesamiento concurrente

En las tres implementaciones se utiliza procesamiento concurrente para calcular los tiempos estimados de las solicitudes.

Esto permite ejecutar varios cálculos como tareas independientes, en lugar de realizar cada uno de manera estrictamente secuencial.

La asignación de técnicos, por otro lado, se mantiene ordenada para respetar la regla de que cada técnico atienda una sola solicitud a la vez.

## 11. Resultados esperados

Con los ocho registros de prueba, el sistema debe mostrar los siguientes resultados:

- Total de solicitudes: 8.
- Solicitudes críticas: 2.
- Solicitudes altas: 2.
- Solicitudes medias: 2.
- Solicitudes bajas: 2.
- Solicitudes asignadas: 3.
- Solicitudes en espera: 5.

Las solicitudes que deben asignarse primero son:

| ID | Prioridad | Categoría | Técnico |
|---:|---|---|---|
| 3 | Crítica | Red | Técnico Mario |
| 7 | Crítica | Software | Técnico Luis |
| 1 | Alta | Hardware | Técnico Ana |

Las otras cinco solicitudes permanecen en espera porque los técnicos de sus respectivas categorías ya tienen una solicitud asignada.

## 12. Pruebas realizadas

En Python se desarrollaron cinco pruebas automatizadas para revisar aspectos importantes del sistema:

- Ordenamiento de solicitudes por prioridad.
- Tiempo de respuesta de una solicitud crítica.
- Tiempo de respuesta de una solicitud baja.
- Cantidad total de solicitudes.
- Cantidad de solicitudes que permanecen en espera.

Las cinco pruebas se ejecutaron correctamente.

Las versiones de C++ y Java cuentan con su implementación del sistema, pero queda pendiente realizar una compilación y ejecución final de estas versiones en el entorno local.

## 13. Comparación de los lenguajes

| Aspecto | C++ | Python | Java |
|---|---|---|---|
| Almacenamiento | `vector` | Listas y diccionarios | `ArrayList` |
| Asociación de datos | `map` | Diccionarios | `Map` |
| Ordenamiento | `sort` | `sort` | `Comparator` |
| Concurrencia | `async` y `future` | `ThreadPoolExecutor` | `ExecutorService` y `Future` |
| Lectura de archivos | `ifstream` | Módulo `csv` | `Files` |

Los tres lenguajes permiten resolver el mismo problema, aunque utilizan herramientas y formas de organizar el código diferentes.

Python necesita menos código para algunas operaciones, C++ ofrece un control más directo sobre los tipos y estructuras, mientras que Java organiza gran parte de su funcionamiento mediante clases y objetos.

## 14. Limitaciones y posibles mejoras

La versión actual cumple con el flujo principal de lectura, ordenamiento, cálculo de tiempos, asignación y estadísticas. Sin embargo, todavía existen aspectos que podrían mejorarse:

- Agregar una opción para registrar nuevas solicitudes desde el programa.
- Permitir cambiar el estado de los reportes.
- Incorporar filtros por prioridad, categoría y técnico.
- Guardar los cambios realizados en los archivos.
- Mejorar la validación de los datos del CSV.
- Agregar una interfaz gráfica para facilitar su uso.
- Realizar pruebas automatizadas en C++ y Java.
- Medir el rendimiento de las tres implementaciones utilizando los mismos datos.

También sería posible ampliar el procesamiento concurrente para trabajar con conjuntos de solicitudes más grandes.

## 15. Conclusión

Al desarrollar el mismo sistema en C++, Python y Java pudimos observar que los tres lenguajes ofrecen diferentes maneras de resolver un mismo problema.

Aunque cambian las estructuras y herramientas utilizadas, la lógica principal se mantiene: organizar las solicitudes, atender primero las de mayor prioridad y asignar los técnicos disponibles.

Este proyecto nos permitió practicar el manejo de archivos, las estructuras de datos, los algoritmos de ordenamiento y el procesamiento concurrente. Además, nos ayudó a identificar algunas diferencias entre los lenguajes y a comprender que la elección de uno depende de las necesidades del programa y de las herramientas que ofrece.

## 16. Referencias

- Documentación oficial de C++: https://en.cppreference.com/w/
- Documentación oficial de Python: https://docs.python.org/3/
- Documentación oficial de Java: https://docs.oracle.com/en/java/
