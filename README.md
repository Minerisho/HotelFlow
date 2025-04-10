# Hotel-Flow Backend

Backend API para un sistema básico de gestión hotelera, desarrollado con Java y Spring Boot. Permite administrar usuarios, habitaciones, reservas y procesos de check-in/check-out.

## Características Principales

* **Gestión de Usuarios:** CRUD completo, login y listado filtrado por rol (Huésped).
* **Gestión de Habitaciones:** CRUD completo, búsqueda por diversos criterios (estado, tipo, capacidad, tarifa), y cambio de estado individual.
* **Gestión de Reservas:** Creación, consulta (por ID, por usuario, todas), cancelación y confirmación de reservas. Validación de disponibilidad y cálculo de costos básicos.
* **Gestión de Check-in/Check-out:** Registro de entradas y salidas asociadas a reservas, con actualización automática del estado de la habitación.
* **Autenticación Básica:** Endpoints de login para validar credenciales.
* **Configuración CORS:** Habilitado para permitir peticiones desde un frontend en `http://localhost:3000`.
* **Manejo Global de Excepciones:** Respuestas de error estandarizadas para la API.

## Tecnologías Utilizadas

* **Java:** Versión 24 (según `pom.xml`)
* **Spring Boot:** Versión 3.4.3
    * Spring Web: Para la creación de APIs RESTful.
    * Spring Data JPA: Para la interacción con la base de datos vía ORM.
    * Spring Security: Para configuración de seguridad (actualmente permisiva) y CORS.
* **Hibernate:** Implementación JPA utilizada por Spring Data.
* **MySQL:** Sistema de gestión de base de datos relacional.
* **Maven:** Herramienta de gestión de dependencias y construcción del proyecto.
* **Lombok:** Para reducir código boilerplate en modelos y DTOs.
* **JJWT (JSON Web Tokens):** Dependencia incluida, aunque el uso explícito de tokens JWT no se observa directamente en los controladores actuales.
* **Jakarta Validation API:** Para validaciones en DTOs (ej. `ReservaRequestDTO`).

## Prerrequisitos

* **JDK 24** (o una versión compatible configurada en `pom.xml`).
* **Maven** 3.x o superior.
* **Servidor MySQL** en ejecución.

## Configuración

1.  **Clonar el repositorio:**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd HotelFlow_back
    ```
2.  **Base de Datos:**
    * Asegúrate de que tu servidor MySQL esté corriendo.
    * Crea una base de datos llamada `HotelFlowDB`.
        ```sql
        CREATE DATABASE HotelFlowDB;
        ```
    * Configura la conexión en `src/main/resources/application.properties`. Modifica las siguientes propiedades si es necesario:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/HotelFlowDB?serverTimezone=UTC
        spring.datasource.username=root # Cambia por tu usuario de MySQL
        spring.datasource.password=H*2Bs8PZ # Cambia por tu contraseña de MySQL
        ```
    * **Nota de Seguridad:** La configuración por defecto incluye credenciales de base de datos directamente en el archivo de propiedades. Para entornos de producción, considera usar variables de entorno, Spring Cloud Config, Vault u otros métodos seguros para gestionar secretos.

3.  **Construir el proyecto:**
    ```bash
    mvn clean package
    ```
    Opcionalmente, si solo quieres instalar las dependencias:
    ```bash
    mvn clean install
    ```

## Ejecución

Puedes ejecutar la aplicación de las siguientes maneras:

1.  **Usando el plugin de Maven:**
    ```bash
    mvn spring-boot:run
    ```
2.  **Ejecutando el archivo JAR:**
    ```bash
    java -jar target/hotel_flow-0.0.1-SNAPSHOT.jar
    ```

La aplicación se iniciará por defecto en el puerto `8094`. Puedes acceder a ella en `http://localhost:8094`.

```
HotelFlow_back/
├── src/
│   ├── main/
│   │   ├── java/entornos/hotelflow/hotel_flow/
│   │   │   ├── HotelFlowApplication.java   # Punto de entrada Spring Boot
│   │   │   ├── config/                     # Clases de configuración (Security, CORS, Exceptions)
│   │   │   ├── controlador/                # Controladores REST (API Endpoints)
│   │   │   ├── modelos/                    # Entidades JPA y DTOs
│   │   │   ├── repositorio/                # Interfaces de Repositorio JPA y algunas interfaces de servicio
│   │   │   └── servicio/                   # Implementaciones e interfaces de lógica de negocio (Servicios)
│   │   └── resources/
│   │       └── application.properties      # Configuración de la aplicación
│   └── test/                               # Clases de prueba
├── pom.xml                                 # Configuración de Maven (dependencias, build)
└── README.md                               # Esta documentación
```
## API Endpoints

La API base se encuentra en `/api`.

### Autenticación (`/api/login`)

* `POST /loginclient`: Valida las credenciales (`correoElectronico`, `contrasena`). Retorna `1` si es válido, `0` si no.
* `POST /ingresar`: Valida las credenciales (`correoElectronico`, `contrasena`). Retorna los detalles del `Usuario` si es válido, o error 401 si no.

### Usuarios (`/api/usuarios`)

* `GET /list`: Obtiene la lista de todos los usuarios.
* `GET /list/{id}`: Obtiene un usuario por su ID.
* `GET /list/huespedes`: Obtiene la lista de usuarios con rol `HUESPED`.
* `POST /`: Crea un nuevo usuario.
* `PUT /`: Actualiza un usuario existente (requiere el objeto `Usuario` completo en el body, incluyendo el ID).
* `DELETE /{id}`: Elimina un usuario por su ID.

### Habitaciones (`/api/habitaciones`)

* `GET /`: Obtiene la lista de todas las habitaciones.
* `GET /{id}`: Obtiene una habitación por su ID.
* `GET /numero/{numero}`: Obtiene una habitación por su número.
* `GET /estado/{estado}`: Obtiene habitaciones por estado (e.g., `DISPONIBLE`, `OCUPADA`, `EN_LIMPIEZA`, `MANTENIMIENTO`).
* `GET /tipo/{tipo}`: Obtiene habitaciones por tipo (e.g., `INDIVIDUAL`, `DOBLE`, `MATRIMONIAL`, `SUITE`).
* `GET /capacidad/{capacidad}`: Obtiene habitaciones con capacidad mayor o igual a la especificada.
* `GET /tarifa/{tarifaMaxima}`: Obtiene habitaciones con tarifa base menor o igual a la especificada.
* `GET /filtrar?estado=...&tipo=...`: Obtiene habitaciones filtrando por estado y tipo simultáneamente.
* `POST /`: Crea una nueva habitación.
* `PUT /{id}`: Actualiza una habitación existente por su ID.
* `DELETE /{id}`: Elimina una habitación por su ID.
* `PATCH /{id}/estado`: Actualiza el estado de una habitación. Requiere un body JSON como `{"estado": "NUEVO_ESTADO"}`.

### Reservas (`/api/reservas`)

* `POST /`: Crea una nueva reserva. Requiere `ReservaRequestDTO` en el body (`idUsuario`, `idHabitacion`, `fechaEntrada`, `fechaSalida`).
* `GET /{idReserva}`: Obtiene una reserva por su ID.
* `GET /`: Obtiene la lista de todas las reservas.
* `GET /usuario/{idUsuario}`: Obtiene las reservas de un usuario específico.
* `PATCH /{idReserva}/cancelar`: Cambia el estado de una reserva a `CANCELADA`.
* `PATCH /{idReserva}/confirmar`: Cambia el estado de una reserva a `CONFIRMADA`.

### Check-in/Check-out (`/api/check`)

* `POST /in`: Registra un check-in para una reserva. Requiere `CheckInRequestDTO` en el body (`{"idReserva": ID}`). Cambia el estado de la habitación a `OCUPADA`.
* `POST /out/{idCheckLog}`: Registra un check-out para un log de check-in específico. Cambia el estado de la habitación a `EN_LIMPIEZA`.
* `GET /{idCheckLog}`: Obtiene los detalles de un log de check-in/check-out por su ID.

## Esquema Básico de Base de Datos (Conceptual)

* **Usuarios:** Almacena información de los usuarios (admin, recepcionista, huésped) y sus credenciales.
* **Habitaciones:** Define las habitaciones del hotel, sus características (tipo, capacidad, tarifa) y estado actual.
* **Reservas:** Vincula un `Usuario` con una `Habitacion` para un período específico (`fechaEntrada`, `fechaSalida`), almacena el costo total y el estado de la reserva.
* **Check_Logs:** Registra los eventos de check-in y check-out, vinculados a una `Reserva`.
