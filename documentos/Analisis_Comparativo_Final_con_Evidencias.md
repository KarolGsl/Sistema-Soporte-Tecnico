# ANÁLISIS COMPARATIVO DE C# 12, PYTHON 3 Y JAVA 17

## Sistema de gestión y priorización de solicitudes de soporte técnico

**Alumno(s):**\
Gaytan Rivas Karol Emilio\
Ponce Jiménez Daniel Alemar

**Materia:** Lenguajes de Programación\
**Docente:** Mónica Cornejo Velázquez\
**Grupo:** 301\
**Fecha:** 17 de noviembre de 2026

------------------------------------------------------------------------

## 1. Introducción y objetivo

La atención de solicitudes de soporte técnico requiere organizar los
reportes recibidos, identificar cuáles necesitan una respuesta
prioritaria y distribuir el trabajo entre el personal disponible. Cuando
varias solicitudes llegan al mismo tiempo, una clasificación clara ayuda
a evitar que los casos urgentes queden al final de la lista y permite
conocer qué asuntos todavía no pueden atenderse.

Para abordar esta necesidad se desarrolló un sistema de gestión y
priorización de solicitudes de soporte técnico. El programa recibe
registros desde un archivo CSV, identifica la prioridad y la categoría
de cada solicitud, ordena los reportes, calcula tiempos estimados y
asigna técnicos de acuerdo con el tipo de problema. Al final presenta
las solicitudes atendidas, las que permanecen en espera y un resumen
estadístico.

El mismo problema se implementó en C# 12, Python 3 y Java 17. Esto
permite observar cómo distintos lenguajes ofrecen herramientas para
resolver una necesidad común. La comparación no busca declarar un
ganador absoluto, sino describir las decisiones de implementación, las
estructuras utilizadas y las diferencias que pueden influir en el
desarrollo y mantenimiento del programa.

El objetivo general de este documento es comparar las tres soluciones
con base en su organización, estructuras de datos, sistema de tipos,
lectura de archivos, manejo de errores, ejecución y mecanismos de
concurrencia. También se revisan los resultados reportados para el
conjunto de prueba y se señalan los aspectos que conviene comprobar o
mejorar.

Como objetivos específicos se consideran: explicar el problema y sus
reglas; describir la clasificación y los paradigmas de cada lenguaje;
comparar las colecciones y los mecanismos de ordenamiento; distinguir
las características de compilación y ejecución; revisar el uso de tareas
concurrentes; y presentar los resultados observados sin atribuir pruebas
que no estén documentadas.

## 2. Descripción del problema

El problema consiste en administrar solicitudes de soporte técnico que
pueden corresponder a distintas categorías, como hardware, software o
red. Cada registro contiene información para identificar el reporte y
decidir el orden en que debe revisarse. Entre los datos considerados se
encuentran el identificador, el solicitante, la categoría, la prioridad,
el estado y la descripción del problema.

El sistema debe resolver dos decisiones relacionadas. La primera es el
orden de atención: una solicitud crítica debe aparecer antes que una
alta, una media o una baja. Cuando dos solicitudes tienen la misma
prioridad, el identificador sirve como segundo criterio de ordenamiento.
Esta regla hace que el resultado sea predecible y evita que el orden
dependa únicamente de la posición original de los registros en el
archivo.

La segunda decisión es la asignación del personal. El proyecto establece
una correspondencia fija entre categoría y técnico: Ana atiende
hardware, Luis atiende software y Mario atiende red. Cada técnico puede
recibir una sola solicitud durante una ejecución. Si aparece otro
reporte de la misma categoría cuando el técnico ya fue asignado, el
nuevo reporte permanece en espera.

El problema no pretende modelar una mesa de ayuda empresarial completa.
Se trata de un ejercicio académico centrado en el manejo de colecciones,
la clasificación de datos, la lectura de archivos, el cálculo de tiempos
y la coordinación de tareas. Por esa razón, el alcance descrito no
incluye una interfaz gráfica, autenticación de usuarios ni una base de
datos persistente.

La entrada principal es un archivo CSV. Esto separa los datos de prueba
del código que procesa las solicitudes y permite cambiar los registros
sin modificar las reglas del programa. Aun así, el archivo debe
conservar un formato consistente: si faltan columnas o aparecen valores
que el sistema no reconoce, la lectura y el procesamiento pueden
producir errores. La validación de estos casos es importante para que la
aplicación no dependa exclusivamente de que el archivo esté bien
formado.

## 3. Reglas de negocio

Las reglas de negocio definen el comportamiento que deben compartir las
tres implementaciones. La prioridad se maneja en el siguiente orden:
crítica, alta, media y baja. Para desempatar solicitudes con la misma
prioridad se utiliza el identificador en orden ascendente. Así, una
solicitud con ID menor aparece antes que otra con el mismo nivel de
prioridad y un ID mayor.

Los tiempos establecidos para cada nivel son los siguientes:

  Prioridad     Primera respuesta máxima   Resolución estimada
  ----------- -------------------------- ---------------------
  Crítica                         1 hora               4 horas
  Alta                           2 horas               3 horas
  Media                          8 horas               2 horas
  Baja                          24 horas                1 hora

La asignación se realiza por categoría. Hardware corresponde a Ana;
software, a Luis; y red, a Mario. La disponibilidad se limita a una
solicitud por técnico durante cada ejecución. Esta condición explica por
qué el sistema puede mostrar solicitudes ordenadas que no quedan
asignadas: el reporte conserva su lugar de prioridad, pero espera a que
exista disponibilidad para su categoría.

El conjunto de prueba descrito en el documento base contiene ocho
solicitudes, dos por cada nivel de prioridad. El resultado reportado es
de tres solicitudes asignadas y cinco en espera. Las asignaciones
señaladas son el ID 3, de prioridad crítica y categoría red, para Mario;
el ID 7, crítica de software, para Luis; y el ID 1, alta de hardware,
para Ana.

Estas reglas deben permanecer iguales en los tres lenguajes. Si una
versión utilizara un orden distinto o permitiera asignar dos reportes al
mismo técnico, ya no estaría resolviendo exactamente el mismo problema.
Por ello, la comparación se concentra tanto en las herramientas del
lenguaje como en la conservación de las reglas comunes.

## 4. Lenguajes seleccionados y clasificación

C# 12 es un lenguaje de propósito general desarrollado por Microsoft y
utilizado habitualmente dentro del entorno .NET. Es compilado y de
tipado estático. Su ecosistema incluye colecciones genéricas, consultas
mediante LINQ y un modelo de tareas que facilita coordinar operaciones
que pueden completarse posteriormente. En el proyecto, estas
características se utilizan para representar solicitudes, relacionar
prioridades con tiempos, ordenar los registros y coordinar cálculos.

Python 3 es un lenguaje de propósito general de alto nivel, con tipado
dinámico y una sintaxis que suele requerir menos declaraciones
explícitas. Su biblioteca estándar incorpora herramientas para trabajar
con archivos CSV, colecciones y ejecución concurrente. En esta solución
se emplean listas y diccionarios, el módulo `csv`, `sorted`, `Counter` y
`ThreadPoolExecutor`.

Java 17 es una versión de la plataforma Java basada en la máquina
virtual JVM. Es un lenguaje de propósito general, de tipado estático y
con una organización fuertemente apoyada en clases y objetos. La
implementación descrita utiliza `ArrayList`, `Map`, `Comparator`,
`ExecutorService` y `Future`, además de herramientas de entrada y salida
de archivos.

Los tres lenguajes son multiparadigma: permiten combinar programación
imperativa con abstracciones orientadas a objetos y herramientas
funcionales o de ejecución concurrente. No obstante, el estilo concreto
depende de cómo se organiza cada solución. En este proyecto, C# y Java
presentan una estructura explícita basada en tipos y clases, mientras
que Python permite expresar parte del procesamiento mediante estructuras
nativas y funciones.

La selección de estos lenguajes permite contrastar tres ecosistemas
ampliamente utilizados. C# se integra con .NET; Python destaca por su
legibilidad y biblioteca estándar; Java ofrece una plataforma portable
mediante la JVM. La elección práctica depende de los conocimientos del
equipo, el entorno donde se ejecutará el programa, las herramientas
disponibles y las necesidades de mantenimiento.

## 5. Paradigmas de programación utilizados

La programación imperativa aparece en las tres implementaciones porque
el sistema ejecuta una secuencia de pasos: leer datos, clasificarlos,
calcular tiempos, asignar técnicos y mostrar resultados. En cada
lenguaje se utilizan instrucciones, condiciones, ciclos y operaciones
sobre colecciones para controlar ese flujo.

También se observan elementos de programación orientada a objetos,
aunque su presencia y forma dependen de cada implementación. En Java se
describe una clase `Solicitud` que representa cada reporte. En C# el
documento identifica una colección `List<Solicitud>`, lo que también
presupone un tipo que modela la solicitud. En Python, los registros se
manejan mediante listas y diccionarios; el uso de una clase es posible,
pero el documento base describe principalmente estructuras nativas.

Hay además operaciones de estilo funcional o declarativo. En C#, LINQ
permite expresar consultas y ordenamientos sobre colecciones mediante
operaciones como `OrderBy` y `ThenBy`. En Java, `Comparator` define el
criterio de comparación utilizado para ordenar. En Python, `sorted`
recibe los datos y produce una nueva secuencia ordenada. Estas
herramientas permiten describir el criterio de ordenamiento sin escribir
manualmente todo el algoritmo de comparación.

La concurrencia complementa los paradigmas anteriores. No sustituye la
lógica principal del sistema, sino que permite ejecutar cálculos
independientes como tareas. La asignación de técnicos permanece ordenada
para respetar la regla de disponibilidad. Esta separación es importante:
el cálculo de tiempos puede ejecutarse de manera concurrente, pero la
decisión de quién recibe cada solicitud debe conservar un resultado
consistente.

En conjunto, las tres soluciones combinan un flujo imperativo con
herramientas de abstracción. No sería correcto describirlas como
implementaciones puramente funcionales o puramente orientadas a objetos.
La clasificación más precisa es la de programas multiparadigma, con
diferencias en el peso que cada implementación da a clases, funciones,
colecciones y tareas concurrentes.

## 6. Arquitectura de cada solución

Las tres versiones siguen una arquitectura lógica por etapas. No se
presenta como una arquitectura empresarial formal ni como un sistema
distribuido; es una separación de responsabilidades que ayuda a entender
el recorrido de la información desde el archivo CSV hasta la salida en
consola.

La primera etapa corresponde a la entrada de datos. El programa abre el
archivo, lee sus filas y transforma los campos en registros que
representan solicitudes. La segunda etapa es el modelo de datos: cada
solicitud debe conservar su identificador, solicitante, categoría,
prioridad, estado y datos relacionados con la atención.

La tercera etapa realiza el procesamiento. Se determina el orden de
prioridad y se consultan los tiempos de respuesta y resolución asociados
a cada nivel. La cuarta etapa agrupa los cálculos independientes y reúne
sus resultados. La quinta etapa asigna técnicos según la categoría y
verifica que no estén ocupados. Finalmente, la etapa de salida muestra
el detalle de las solicitudes y las estadísticas generales.

En C# 12, el documento atribuye la lectura a `StreamReader`, el
almacenamiento a `List`, la asociación de datos a `Dictionary`, el
ordenamiento a LINQ y la coordinación de tareas a `Task` y
`Task.WhenAll`. En Python 3 se utilizan el módulo `csv`, listas,
diccionarios, `sorted`, `Counter` y `ThreadPoolExecutor`. En Java 17 se
describe una clase `Solicitud`, `ArrayList`, `Map`, `Comparator`,
`ExecutorService` y `Future`.

La separación por etapas facilita el mantenimiento porque un cambio en
los tiempos no debería obligar a modificar la lectura del CSV. De la
misma forma, agregar una categoría nueva debería concentrarse en la
configuración de técnicos y no requerir reescribir todo el proceso. Esta
separación es una recomendación de diseño derivada de la arquitectura
lógica descrita.

## 7. Comparación de estructuras de datos

Las colecciones son fundamentales porque el programa debe almacenar una
cantidad variable de solicitudes y recorrerlas para ordenarlas,
asignarlas y contarlas. C# utiliza `List<Solicitud>`, Python utiliza
listas y Java utiliza `ArrayList`. Las tres alternativas permiten
conservar varios elementos y recorrerlos, aunque difieren en la forma de
declarar el tipo de dato y en las operaciones disponibles.

Para relacionar una clave con un valor se utilizan `Dictionary` en C#,
diccionarios (`dict`) en Python y `Map` en Java. En este caso, las
claves pueden representar prioridades y los valores sus tiempos de
respuesta o resolución. Esta asociación evita repetir condiciones
extensas cada vez que se necesita consultar un tiempo.

El ordenamiento también se resuelve con herramientas diferentes. LINQ
permite encadenar `OrderBy` y `ThenBy` en C#. Python dispone de
`sorted`, que puede ordenar una secuencia usando una clave. Java utiliza
`Comparator` para definir cómo se comparan dos solicitudes. Aunque la
sintaxis cambia, las tres soluciones deben aplicar el mismo criterio:
prioridad y después identificador.

Para las estadísticas, Python utiliza `Counter`, que simplifica el
conteo de elementos. En C# se pueden realizar consultas sobre
colecciones y operaciones de conteo. En Java se pueden utilizar mapas y
operaciones de colección para acumular resultados. El documento base no
detalla una única clase o método estadístico equivalente en los tres
lenguajes, por lo que la comparación debe mantenerse en el nivel de la
función que cumplen estas herramientas.

  --------------------------------------------------------------------------------
  Necesidad         C# 12               Python 3               Java 17
  ----------------- ------------------- ---------------------- -------------------
  Colección         `List<Solicitud>`   `list`                 `ArrayList`
  principal                                                    

  Clave-valor       `Dictionary`        `dict`                 `Map`

  Ordenamiento      `OrderBy` /         `sorted`               `Comparator`
                    `ThenBy`                                   

  Estadísticas      consultas y         `Counter`              `Map` y colecciones
                    colecciones                                

  Concurrencia      `Task` /            `ThreadPoolExecutor`   `ExecutorService` /
                    `Task.WhenAll`                             `Future`
  --------------------------------------------------------------------------------

La principal diferencia práctica es el nivel de explicitud. C# y Java
hacen visible el tipo de los elementos de sus colecciones, mientras que
las listas y diccionarios de Python permiten almacenar datos sin
declarar de antemano un tipo genérico. Esta flexibilidad puede acelerar
la escritura, pero también hace importante validar la información que se
incorpora desde el CSV.

## 8. Comparación del manejo de errores

El sistema depende de un archivo externo, por lo que existen posibles
fallos antes de iniciar el procesamiento. El archivo puede no existir,
no tener permisos de lectura, contener filas incompletas o presentar
valores que no correspondan al formato esperado. También pueden aparecer
identificadores no numéricos, prioridades desconocidas o categorías que
no tengan un técnico asociado.

C# utiliza el mecanismo de excepciones de .NET, normalmente mediante
bloques `try`, `catch` y, cuando corresponde, `finally`. Estos bloques
permiten capturar errores de lectura o conversión y presentar un mensaje
entendible. Java también utiliza excepciones, con la distinción entre
excepciones verificadas y no verificadas. Para recursos como archivos
puede utilizar `try-with-resources`, que ayuda a cerrarlos de manera
segura. Python utiliza `try` y `except`; entre los errores que pueden
presentarse están `FileNotFoundError`, `csv.Error` y `ValueError`.

El manejo de excepciones no reemplaza la validación. Antes de procesar
una fila conviene verificar que estén presentes las columnas
obligatorias, que el identificador tenga el formato esperado y que la
prioridad y categoría pertenezcan a los valores admitidos. Si el
registro no cumple las condiciones, el programa debería informar cuál
fila presenta el problema y evitar que un dato incorrecto altere las
estadísticas.

El documento base señala expresamente que no existe una batería
equivalente de pruebas de errores para las tres versiones. Por esa
razón, los casos descritos en este apartado son situaciones que conviene
controlar y recomendaciones de mejora; no deben presentarse como pruebas
ya ejecutadas.

Una estrategia de mejora sería registrar el número de fila y el motivo
del rechazo, continuar con los registros válidos cuando sea seguro
hacerlo y finalizar con un resumen de filas procesadas y descartadas.
También conviene evitar capturar excepciones demasiado generales sin
informar el error original, porque eso dificulta localizar la causa del
problema.

## 9. Comparación del sistema de tipos

C# 12 y Java 17 son lenguajes de tipado estático. En ambos, el tipo de
una variable se conoce durante la compilación y el compilador puede
detectar diversas incompatibilidades antes de ejecutar el programa. Por
ejemplo, una propiedad que representa el identificador puede declararse
como entero, mientras que una colección puede indicar que almacena
objetos de tipo `Solicitud`.

Python 3 utiliza tipado dinámico. El tipo de un valor se determina
durante la ejecución y una variable puede referirse a valores de
distintos tipos en momentos diferentes. Esta característica reduce
declaraciones y permite escribir código flexible. Sin embargo, cuando
los datos provienen de un archivo externo, el programa debe comprobar
que los valores tengan el formato esperado antes de utilizarlos.

Es importante distinguir tipado dinámico de ausencia de tipos. Python sí
tiene tipos; la diferencia es cuándo se determina y verifica su
compatibilidad. Las anotaciones de tipo pueden documentar las
expectativas y ayudar a herramientas de análisis estático, pero no
convierten por sí solas el comportamiento del lenguaje en tipado
estático.

C# y Java ofrecen una estructura explícita para representar solicitudes
mediante clases y propiedades o campos tipados. Python puede utilizar
diccionarios o clases. En el documento base, la implementación de Python
se describe principalmente mediante listas y diccionarios. La selección
entre estas alternativas influye en la claridad del modelo y en la
facilidad para detectar errores de datos.

  -----------------------------------------------------------------------
  Criterio          C# 12             Python 3          Java 17
  ----------------- ----------------- ----------------- -----------------
  Sistema de tipos  estático          dinámico          estático

  Verificación      compilación y     ejecución;        compilación y
  principal         ejecución         herramientas      ejecución
                                      opcionales        

  Representación    clase tipada      diccionarios y    clase tipada
  descrita                            colecciones       

  Flexibilidad      moderada y        alta              moderada y
                    explícita                           explícita
  -----------------------------------------------------------------------

En un sistema pequeño, cualquiera de los tres enfoques puede funcionar.
Cuando crece el número de campos o reglas, un modelo explícito puede
ayudar a mantener consistencia. En Python, las validaciones y las
anotaciones pueden aportar claridad sin perder la flexibilidad que
caracteriza al lenguaje.

## 10. Comparación de ejecución y compilación

C# 12 normalmente se compila mediante el SDK de .NET a un lenguaje
intermedio conocido como CIL. Durante la ejecución, el entorno .NET
puede utilizar compilación Just-In-Time (JIT) para traducir el código
intermedio a instrucciones adecuadas para la plataforma. Para compilar o
ejecutar la aplicación se necesita una versión compatible del SDK o del
runtime de .NET.

Python 3 suele ejecutarse mediante un intérprete. En la implementación
CPython, el código fuente se transforma normalmente en bytecode que
ejecuta la máquina virtual de Python. Para ejecutar el proyecto se
necesita una instalación compatible del intérprete y los módulos que
utilice. En este caso, el uso de `csv` y `concurrent.futures`
corresponde a la biblioteca estándar.

Java 17 compila el código fuente con `javac` y genera bytecode. La
máquina virtual de Java (JVM) carga y ejecuta ese bytecode y puede
aplicar compilación JIT durante la ejecución. Esto permite que el mismo
bytecode funcione en distintos sistemas que dispongan de una JVM
compatible.

Las capturas proporcionadas muestran formas distintas de ejecutar las
soluciones: C# se ejecutó en un compilador en línea; Java y Python se
ejecutaron desde la terminal integrada de Visual Studio Code. Estas
capturas documentan una ejecución concreta, pero no constituyen por sí
solas una medición de rendimiento. Tampoco permiten concluir que un
lenguaje sea más rápido que otro, ya que no se controlaron el hardware,
las versiones del entorno ni las condiciones de prueba.

  Lenguaje   Preparación habitual            Entorno de ejecución
  ---------- ------------------------------- ----------------------
  C# 12      compilación con .NET SDK        runtime .NET
  Python 3   ejecución mediante intérprete   intérprete Python
  Java 17    compilación con javac           JVM

La portabilidad depende de los requisitos de cada plataforma. Java
necesita una JVM compatible; C# requiere el runtime correspondiente
cuando la aplicación no se publica de forma autónoma; Python necesita un
intérprete disponible. La facilidad de despliegue debe evaluarse junto
con las dependencias y el sistema operativo de destino.

## 11. Comparación de concurrencia o asincronía

Las tres implementaciones incorporan mecanismos para ejecutar cálculos
independientes como tareas. El propósito es separar operaciones que no
necesitan modificar el mismo estado y reunir sus resultados cuando
terminan. En este proyecto, los cálculos de tiempos de respuesta y
resolución se describen como independientes para cada solicitud.

C# utiliza `Task` para representar una operación que puede completarse
posteriormente. `Task.WhenAll` permite esperar a que un conjunto de
tareas termine y obtener sus resultados. Este modelo puede combinarse
con `async` y `await` para escribir código asincrónico de forma legible.
En la implementación descrita, estas herramientas coordinan los cálculos
sin cambiar la regla de asignación.

Python utiliza `ThreadPoolExecutor`, que administra un conjunto de hilos
y permite enviar funciones para que se ejecuten mediante un grupo de
trabajo. El resultado de cada tarea puede recuperarse mediante objetos
Future. El comportamiento y el beneficio de los hilos dependen del tipo
de trabajo y de la implementación del intérprete; por ello no debe
suponerse que toda operación se acelera por utilizar hilos.

Java utiliza `ExecutorService` para administrar un servicio de ejecución
y `Future` para representar el resultado de una tarea que todavía puede
no haber terminado. Este enfoque permite controlar el envío de trabajos
y recuperar sus resultados cuando están disponibles.

Las herramientas no son idénticas. `Task` pertenece al modelo de tareas
de .NET; `ThreadPoolExecutor` trabaja con hilos administrados por
Python; `ExecutorService` administra ejecutores en Java. La comparación
válida es funcional: los tres mecanismos permiten coordinar trabajo
independiente, aunque difieren en su API y en los detalles de ejecución.

La asignación de técnicos debe conservarse de manera ordenada. Si varias
tareas modificaran al mismo tiempo la disponibilidad de un técnico sin
sincronización, podrían asignarse dos solicitudes al mismo recurso. Por
eso, el proyecto separa el cálculo concurrente de la asignación, que
debe respetar la regla de una solicitud por técnico.

No se presentan mediciones de tiempo que permitan afirmar que la
concurrencia mejora el rendimiento del sistema. Para demostrar ese
beneficio sería necesario comparar una ejecución secuencial y otra
concurrente bajo las mismas condiciones, repetir las mediciones y
utilizar un volumen de datos suficiente. En este ejercicio, la
concurrencia se analiza como mecanismo de organización y coordinación de
tareas.

## 12. Resultados de las pruebas

El conjunto de prueba descrito en el documento base contiene ocho
solicitudes, distribuidas de manera equilibrada: dos críticas, dos
altas, dos medias y dos bajas. El resultado general reportado es de tres
solicitudes asignadas y cinco solicitudes en espera. La distribución
coincide con la regla que permite una sola asignación por cada uno de
los tres técnicos disponibles.

Los registros que el documento identifica como asignados son: ID 3,
prioridad crítica, categoría red, técnico Mario; ID 7, prioridad
crítica, categoría software, técnico Luis; e ID 1, prioridad alta,
categoría hardware, técnico Ana. Los otros cinco reportes permanecen en
espera porque el técnico correspondiente a su categoría ya recibió una
solicitud durante esa ejecución.

El documento base también reporta cinco pruebas automatizadas para
Python. Sus nombres son `test_estadisticas_cola`,
`test_estadisticas_total`, `test_orden_prioridad`, `test_tiempo_baja` y
`test_tiempo_critica`. De acuerdo con la descripción disponible,
comprueban estadísticas de cola y total, orden de prioridad y tiempos
para prioridades crítica y baja. No se reportan pruebas automatizadas
equivalentes para C# y Java.

Las capturas entregadas permiten observar ejecuciones de los tres
lenguajes. La imagen de Python muestra el listado de solicitudes y las
estadísticas de ocho registros, tres asignados y cinco en cola, además
de dos solicitudes por prioridad. La imagen de Java muestra el total de
ocho, tres asignadas y cinco en cola, con dos registros por prioridad.
La captura de C# muestra la salida de solicitudes y las asignaciones de
los IDs 3, 7 y 1; la parte inferior con las estadísticas no queda
visible en esa captura.

Por tanto, las imágenes respaldan que se ejecutaron los programas y
muestran salidas coherentes con parte de los resultados descritos. Sin
embargo, una captura no sustituye el código fuente ni una batería
automatizada de pruebas. Para una comparación rigurosa convendría
ejecutar exactamente el mismo archivo de entrada en los tres lenguajes y
conservar la salida completa.

**Evidencia de ejecución en Python**

*Figura 1. Salida de Python desde la terminal de Visual Studio Code.*

## 13. Evidencias de ejecución: Python y Java

Las siguientes capturas se incorporan como evidencia visual de las
ejecuciones compartidas. En Python se observa la salida del programa
desde la terminal integrada de Visual Studio Code. La pantalla presenta
las solicitudes ordenadas, el estado de cada una, el técnico asignado o
la indicación de que permanece en cola, así como los tiempos máximos de
respuesta y los tiempos estimados de resolución.

En la parte inferior de la captura de Python se muestran las
estadísticas: ocho solicitudes en total; dos críticas, dos altas, dos
medias y dos bajas; tres solicitudes asignadas y cinco en cola. Estos
valores coinciden con el resumen descrito en el análisis.

La captura de Java también corresponde a una ejecución desde la terminal
de Visual Studio Code. La parte visible muestra varios registros en
espera y el bloque de estadísticas, donde se leen ocho solicitudes, tres
asignadas y cinco en cola. También aparecen dos solicitudes por cada
prioridad. La imagen está tomada en la parte final de la salida, por lo
que no muestra todos los registros desde el principio.

Las evidencias deben interpretarse dentro de su alcance. Permiten
documentar que se ejecutaron las versiones mostradas y que la salida
visible presenta los indicadores señalados. No demuestran por sí solas
que se hayan probado todos los casos límite, ni permiten comparar
tiempos de ejecución entre lenguajes.

**Evidencia de ejecución en Java**

*Figura 2. Salida de Java desde la terminal de Visual Studio Code.*

## 14. Evidencia de ejecución: C# y alcance de la comparación

La captura de C# corresponde al compilador y depurador en línea de C#.
En el panel de salida se observan las solicitudes procesadas y, entre
ellas, las asignaciones del ID 3 a Mario, del ID 7 a Luis y del ID 1 a
Ana. También se alcanzan a ver otros registros que permanecen sin
asignar. La captura no incluye el final de la salida, por lo que no se
utiliza como prueba visual de las estadísticas generales.

En conjunto, las tres evidencias muestran que se realizaron ejecuciones
en los entornos indicados. Python y Java muestran sus bloques de
estadísticas completos; C# permite observar las primeras asignaciones y
parte de las solicitudes pendientes. Para fortalecer la documentación se
recomienda guardar una captura adicional de C# en la que aparezca el
resumen final, sin alterar la salida del programa.

La comparación de resultados debe considerar también el origen de los
datos. Las capturas no muestran de forma completa el archivo CSV ni
todas las filas de entrada. Por ello, el documento registra los
resultados reportados y describe lo que efectivamente se alcanza a leer
en cada evidencia, sin afirmar que las imágenes prueban cada detalle
interno de los programas.

**Evidencia de ejecución en C#**

*Figura 3. Ejecución de C# en un compilador en línea.*

## 15. Ventajas, limitaciones y mantenibilidad

C# 12 ofrece colecciones genéricas, LINQ y un modelo de tareas integrado
con .NET. Estas herramientas permiten organizar los datos mediante tipos
definidos y expresar consultas sobre colecciones con una sintaxis
compacta. Como cuidado, requiere un entorno .NET compatible y
familiaridad con clases, colecciones y tipos.

Python 3 destaca por su sintaxis concisa, sus estructuras nativas y la
disponibilidad de módulos para leer CSV y coordinar tareas. Esto puede
facilitar la construcción y modificación de un prototipo. Su
flexibilidad también exige validar cuidadosamente los datos que entran
al programa. Además, el uso de hilos no garantiza una mejora de
rendimiento para cualquier clase de trabajo.

Java 17 ofrece una organización basada en clases y tipos estáticos,
bibliotecas estándar y herramientas para administrar ejecutores. Puede
resultar conveniente cuando se busca una estructura explícita y una
ejecución mediante JVM. A cambio, algunas operaciones requieren más
declaraciones y es necesario disponer de una JVM compatible.

Las limitaciones compartidas del sistema son el número fijo de técnicos,
la asignación de una sola solicitud por técnico durante cada ejecución y
el uso de CSV como fuente de datos. El documento base no describe
persistencia de cambios ni administración de usuarios. Tampoco presenta
una interfaz gráfica. Estas características delimitan el alcance
académico del programa.

La mantenibilidad mejora cuando la lectura del archivo, las reglas de
prioridad, los cálculos, la asignación y la presentación se encuentran
separados. Las reglas de negocio deben mantenerse en un solo lugar para
evitar que los tiempos o las prioridades cambien de forma inconsistente.
También conviene utilizar nombres claros, validar cada fila y crear
pruebas automatizadas para entradas correctas e incorrectas.

Si el volumen de solicitudes creciera, sería necesario medir el
comportamiento con archivos más grandes. Si aumentara el número de
técnicos, la relación entre categoría y técnico debería convertirse en
información configurable. Otras mejoras posibles serían una base de
datos, historial de cambios, filtros, interfaz gráfica y acceso para
varios usuarios. Estas ampliaciones exigirían definir permisos y
mecanismos de control de concurrencia sobre la asignación; no forman
parte de las funciones documentadas en la versión actual.

## 16. Conclusiones

El desarrollo del sistema permitió comparar cómo C# 12, Python 3 y Java
17 resuelven un mismo problema de gestión de solicitudes. Los tres
lenguajes cuentan con herramientas para almacenar registros, ordenarlos
por prioridad, consultar tiempos, asignar técnicos y generar
estadísticas. Las diferencias principales se encuentran en la sintaxis,
el sistema de tipos, las bibliotecas y la manera de coordinar tareas
concurrentes.

C# destaca por la integración de sus colecciones genéricas con LINQ y el
modelo `Task` de .NET. Python sobresale por la sencillez de su sintaxis,
sus estructuras nativas y módulos como `csv` y `concurrent.futures`.
Java ofrece una organización basada en clases y tipos estáticos, junto
con herramientas como `ExecutorService` y `Future` para administrar
tareas.

Los resultados reportados para el conjunto de ocho solicitudes muestran
tres asignaciones y cinco solicitudes en espera. Las asignaciones
identificadas corresponden a los IDs 3, 7 y 1, distribuidos entre Mario,
Luis y Ana según la categoría. Las capturas de Python y Java muestran el
resumen estadístico completo; la de C# permite observar las
asignaciones, aunque no el bloque final de estadísticas.

Una limitación de la evaluación es que las pruebas automatizadas
documentadas corresponden a Python. No se reporta una batería
equivalente para C# y Java, por lo que no sería adecuado afirmar que las
tres versiones fueron verificadas con el mismo nivel de cobertura. Una
evaluación posterior debería ejecutar los mismos casos en los tres
lenguajes, incluyendo archivo inexistente, fila incompleta, prioridad
inválida, categoría sin técnico, empate de prioridad y dos solicitudes
para un mismo técnico.

No existe un lenguaje universalmente superior para este ejercicio.
Python puede ser conveniente cuando se busca rapidez de desarrollo y una
sintaxis directa; C# es una opción sólida para proyectos integrados al
entorno .NET; y Java resulta adecuado cuando se requiere una estructura
orientada a objetos y ejecución mediante JVM. La elección debe responder
al entorno, los requisitos, la experiencia del equipo y las necesidades
futuras.

Como reflexión final, el resultado de un programa no depende únicamente
del lenguaje elegido. La claridad de las reglas, la validación de
entradas, la separación de responsabilidades y las pruebas son factores
decisivos para que el sistema sea confiable y pueda mantenerse. Comparar
las tres implementaciones permite reconocer distintas formas de resolver
el mismo problema y comprender mejor las herramientas que ofrece cada
ecosistema.

## Referencias bibliográficas y técnicas

-   Microsoft. (s. f.). Documentación de C#.
    https://learn.microsoft.com/es-es/dotnet/csharp/
-   Microsoft. (s. f.). Documentación de .NET.
    https://learn.microsoft.com/es-es/dotnet/
-   Microsoft. (s. f.). Task-based asynchronous programming.
    https://learn.microsoft.com/dotnet/standard/parallel-programming/task-based-asynchronous-programming
-   Python Software Foundation. (s. f.). Python 3 Documentation.
    https://docs.python.org/3/
-   Python Software Foundation. (s. f.). csv --- CSV File Reading and
    Writing. https://docs.python.org/3/library/csv.html
-   Python Software Foundation. (s. f.). concurrent.futures ---
    Launching parallel tasks.
    https://docs.python.org/3/library/concurrent.futures.html
-   Oracle. (s. f.). Java SE 17 Documentation.
    https://docs.oracle.com/en/java/javase/17/
-   Oracle. (s. f.). Java Tutorials: Collections.
    https://docs.oracle.com/javase/tutorial/collections/
-   Oracle. (s. f.). Java Tutorials: Concurrency.
    https://docs.oracle.com/javase/tutorial/essential/concurrency/
-   Equipo de trabajo. (2026). *Análisis comparativo de C# 12, Python 3
    y Java 17: Sistema de gestión y priorización de solicitudes de
    soporte técnico*. Documento académico.
