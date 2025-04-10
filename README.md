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

## Estructura del Proyecto
