\# Diagramas del sistema



\## 1. Arquitectura general



```mermaid

flowchart TD

&#x20;   A\[Archivo CSV] --> B\[Lectura de solicitudes]

&#x20;   B --> C\[Almacenamiento de datos]

&#x20;   C --> D\[Ordenamiento por prioridad e ID]

&#x20;   D --> E\[Calculo concurrente de tiempos]

&#x20;   E --> F\[Asignacion de tecnicos]

&#x20;   F --> G{Tecnico disponible}

&#x20;   G -->|Si| H\[Solicitud asignada]

&#x20;   G -->|No| I\[Solicitud en espera]

&#x20;   H --> J\[Estadisticas]

&#x20;   I --> J

&#x20;   J --> K\[Resultados en terminal]





