# Sistema de Gestión de Operaciones Financieras y Procesos BPMN

Solución backend desarrollada en **Spring Boot** e integrada con el motor de flujos **Camunda BPM** y **PostgreSQL**, diseñada para automatizar, orquestar y auditar el ciclo de vida de operaciones financieras (créditos y aprobaciones multinivel).

---

## 🚀 Tecnologías Utilizadas

* **Java 21**
* **Spring Boot** (Web, Data JPA)
* **Camunda BPM Engine** (Orquestación de procesos BPMN)
* **PostgreSQL** (Base de datos relacional)
* **Gradle** (Gestor de dependencias)
* **Springdoc OpenAPI / Swagger** (Documentación interactiva de la API)

---

## 📋 Prerrequisitos para la Ejecución

Asegúrate de contar con los siguientes elementos instalados en tu entorno de desarrollo:
1. **JDK 21** configurado en tu IDE (IntelliJ IDEA recomendado).
2. **PostgreSQL** activo y en ejecución localmente.
3. Una base de datos creada en PostgreSQL (por ejemplo, nombrada según la configuración de tu proyecto).

---

## ⚙️ Configuración del Proyecto

Configura las credenciales de tu base de datos en el archivo `src/main/resources/application.yml` (o `application.properties`):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tu_base_de_datos
    username: tu_usuario
    password: tu_contraseña
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## ▶️ Cómo Ejecutar la Aplicación
Puedes iniciar el proyecto ejecutando la siguiente tarea de Gradle desde tu terminal o consola:

```bash
Bash
./gradlew bootRun
```

---

## 🏗️ Decisiones Arquitectónicas y Patrones de Diseño
La solución implementa patrones de diseño clave para resolver desafíos reales del dominio financiero y mantener la extensibilidad sin contaminar el núcleo del negocio:

### 1. Patrón State (Estado)
Problema que resuelve: Controlar el ciclo de vida de una solicitud financiera y sus transiciones válidas (PENDIENTE, APROBADA, RECHAZADA, CANCELADA) sin acoplar la lógica con condicionales complejos.

Por qué se seleccionó: Permite que el motor de Camunda y la persistencia relacional en PostgreSQL mantengan sincronización estricta sobre el estado actual de cada operación.

Alternativas consideradas: Máquinas de estados caseras mediante enums y bloques if/else, descartadas por su alta complejidad de mantenimiento y falta de visibilidad del flujo.

### 2. Patrón Adapter / Service Delegate
   Problema que resuelve: Desacoplar el motor BPMN de la capa de servicios y persistencia en Spring Boot.

Por qué se seleccionó: Las clases que implementan JavaDelegate funcionan como adaptadores transparentes que traducen las variables del proceso Camunda a ejecuciones de servicios JPA.

Alternativas consideradas: Lógica de negocio embebida en scripts dentro del diagrama BPMN, descartada por dificultar las pruebas unitarias y el control de excepciones.

### 3. Patrón Observer / Domain Events (Observador / Eventos de Dominio)
   Problema que resuelve: Registrar y auditar cada cambio de estado, tarea completada e hito del proceso financiero sin acoplar los módulos principales al sistema de bitácora.

Por qué se seleccionó: El motor de Camunda (HistoryService) funciona bajo este principio, escuchando y registrando eventos de manera asíncrona.

Alternativas consideradas: Triggers manuales en base de datos, descartados por menor portabilidad y falta de trazabilidad en la capa de aplicación.

### 4. Patrón Strategy (Estrategia) / Enrutamiento Dinámico
   Problema que resuelve: Determinar dinámicamente la ruta de aprobación (automática, supervisor, gerencia o riesgos) en función del monto y el nivel de riesgo.

Por qué se seleccionó: Las compuertas exclusivas/paralelas y los delegados de evaluación encapsulan las reglas de negocio en estrategias independientes según los criterios de la operación.

Alternativas consideradas: Estructuras de decisión monolíticas en los controladores REST, descartadas por violar el principio de Responsabilidad Única y Abierto/Cerrado (SOLID).

---

## 📚 Documentación de la API (Swagger UI)
El proyecto cuenta con documentación interactiva integrada mediante OpenAPI. Para probar todos los endpoints de forma visual sin necesidad de herramientas externas, ingresa a la siguiente URL en tu navegador:

👉 http://localhost:8080/swagger-ui/index.html

---

## 🔌 Endpoints Principales

La API REST cumple con todos los requerimientos:

### Gestión de Solicitudes:

#### POST /api/operaciones/iniciar - Crea una nueva solicitud e inicia la instancia en Camunda.

#### GET /api/operaciones - Consulta todas las solicitudes y sus estados actuales.

#### GET /api/operaciones/{id} - Consulta una solicitud específica por su ID.

#### PUT /api/operaciones/{id}/cancelar - Cancela una solicitud activa y suspende su proceso.

#### PUT /api/operaciones/{id}/reenviar - Modifica y reenvía una solicitud corregida.

### Gestión de Tareas (Motor Camunda):

#### GET /api/tareas/pendientes - Lista todas las tareas humanas pendientes de aprobación.

#### POST /api/tareas/{taskId}/completar - Aprueba o rechaza una tarea específica mediante parámetros (?aprobado=true/false).

### Auditoría e Instancias:

#### GET /api/operaciones/historial/auditoria - Consulta el historial de instancias de procesos finalizados.

#### GET /api/operaciones/instancias/activas - Consulta el estado de las instancias de proceso en ejecución.

---