# Sistema de Gestión de Operaciones Financieras y Procesos BPMN

Solución backend desarrollada en **Spring Boot** e integrada con el motor de flujos **Camunda BPM** y **PostgreSQL**, diseñada para automatizar, orquestar y auditar el ciclo de vida de operaciones financieras (créditos y aprobaciones multinivel).

---

## Tecnologías Utilizadas

* **Java 21**
* **Spring Boot** (Web, Data JPA)
* **Camunda BPM Engine** (Orquestación de procesos BPMN)
* **PostgreSQL** (Base de datos relacional)
* **Gradle** (Gestor de dependencias)
* **Springdoc OpenAPI / Swagger** (Documentación interactiva de la API)
* **JUnit 5** (Pruebas automatizadas)

---

## Prerrequisitos para la Ejecución

Asegúrate de contar con los siguientes elementos instalados en tu entorno de desarrollo:
1. **JDK 21** configurado en tu IDE (IntelliJ IDEA recomendado).
2. **PostgreSQL** activo y en ejecución localmente.
3. Una base de datos creada en PostgreSQL (por ejemplo, nombrada según la configuración de tu proyecto).

---

## Configuración del Proyecto

Configura las credenciales de tu base de datos en el archivo `application.properties`:

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

## ️Cómo Ejecutar la Aplicación
Puedes iniciar el proyecto ejecutando la siguiente tarea de Gradle desde tu terminal o consola:

```bash
Bash
./gradlew bootRun
```

---

## Diagrama de Arquitectura

La solución se estructura bajo un enfoque de **Monolito Modular**, asegurando bajo acoplamiento y una clara separación de responsabilidades:

* **Capa Cliente / API REST:** Controladores de Spring Boot expuestos para la interacción con interfaces externas y documentados de forma interactiva con Swagger.
* **Capa de Negocio y Orquestación:** El motor de Camunda BPM procesa las reglas del negocio, evaluando compuertas exclusivas, compuertas paralelas de aprobación y temporizadores (timers) de escalamiento.
* **Capa de Persistencia:** Spring Data JPA conectado a PostgreSQL, garantizando la persistencia transaccional y el aislamiento de datos mediante bloqueo optimista.

---

## Diagrama BPMN

El flujo completo del proceso de negocio está modelado bajo la notación BPMN 2.0. Puedes visualizar la imagen del diagrama exportada en el repositorio dentro de la carpeta de documentación:
👉 **[Ver Diagrama BPMN en Docs](./docs/diagrama-bpmn.png)**

---

## Patrones de Diseño Utilizados

* **State (Estado):** Controla el ciclo de vida de la solicitud (`PENDIENTE`, `APROBADA`, `RECHAZADA`, `CANCELADA`) sincronizado con el motor de procesos.
* **Adapter / Service Delegate:** Desacopla el motor Camunda de la lógica de negocio mediante clases que implementan `JavaDelegate`.
* **Observer / Domain Events:** Permite la auditoría y seguimiento del historial de instancias y cambios de estado de forma asíncrona mediante el motor histórico de Camunda (`HistoryService`).
* **Strategy (Enrutamiento Dinámico):** Encapsula las reglas para determinar rutas de aprobación (automática, supervisor, gerencia o riesgos) según montos y criterios de la solicitud.

---

## Documentación de los Endpoints (API REST)

El proyecto cuenta con contratos limpios y respuestas HTTP estandarizadas. La documentación interactiva (OpenAPI / Swagger) está disponible al ejecutar la aplicación en:
**[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Endpoints principales:
* `POST /api/operaciones/iniciar` - Crea solicitud e inicia instancia en Camunda.
* `GET /api/operaciones` - Consulta todas las solicitudes.
* `GET /api/operaciones/{id}` - Consulta solicitud por ID.
* `PUT /api/operaciones/{id}/cancelar` - Cancela solicitud activa.
* `PUT /api/operaciones/{id}/reenviar` - Modifica y reenvía solicitud corregida.
* `GET /api/tareas/pendientes` - Lista tareas humanas pendientes.
* `POST /api/tareas/{taskId}/completar` - Aprueba o rechaza tarea (`?aprobado=true/false`).
* `GET /api/operaciones/historial/auditoria` - Historial y auditoría de procesos.
* `GET /api/operaciones/instancias/activas` - Estado de instancias en ejecución.

---

## Scripts de Base de Datos y Migraciones

La aplicación está configurada mediante Hibernate (`spring.jpa.hibernate.ddl-auto=update`), lo que permite que las tablas, esquemas relacionales y restricciones se creen y actualicen de forma automática e integrada en PostgreSQL al iniciar el sistema.

---

## Pruebas Automatizadas

El proyecto incluye pruebas unitarias y de integración desarrolladas con **JUnit 5**. Para verificar que todas las pruebas pasen de forma exitosa, ejecuta en tu terminal:
```
bash
./gradlew test

```

---

## Guía para Reproducir los Escenarios de Negocio
Para probar el flujo completo de la aplicación:

Inicia la aplicación ejecutando:

```
Bash
./gradlew bootRun
```

Crea una nueva solicitud de operación mediante un cliente HTTP (o Swagger) haciendo un POST a /api/operaciones/iniciar.

Consulta las tareas humanas que quedaron pendientes en el motor con un GET a /api/tareas/pendientes.

Completa la aprobación de la tarea enviando un POST a /api/tareas/{taskId}/completar?aprobado=true.

## Decisiones Arquitectónicas y Limitaciones Conocidas
#### Elección del Monolito Modular: 
Se seleccionó por encima de una arquitectura de microservicios distribuida para garantizar la atomicidad transaccional ACID estricta entre el motor Camunda y la base de datos relacional PostgreSQL, reduciendo la complejidad de red innecesaria para este dominio.

#### Control de Concurrencia: 
Se implementó bloqueo optimista (@Version) para mitigar condiciones de carrera cuando múltiples aprobadores intentan interactuar simultáneamente con la misma tarea.
