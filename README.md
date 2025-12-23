# Sistema de Gestión de Coworking - Armadillo

Este proyecto es una API REST profesional diseñada para la gestión integral de salas de coworking y reservas de espacios de trabajo. La solución utiliza un stack tecnológico moderno basado en Java 17, Spring Boot 3, PostgreSQL y Docker.

## 1. Instrucciones para ejecutar el proyecto (Paso a paso)
   Siga estos pasos para poner el sistema en marcha

**Descarga y Preparación:**

- Descargue el archivo comprimido (.zip) del proyecto y extráigalo en una carpeta local (ej. Escritorio).

**Encender el motor (Docker):**

- Asegúrese de tener instalado y abierto Docker Desktop. Debe visualizar el icono de la ballena en verde en la barra de tareas.

**Abrir la Terminal:**

- Ingrese a la carpeta que acaba de extraer.

- Haga clic derecho en un espacio vacío y seleccione "Abrir en Terminal" (o "Open in Terminal").

**Iniciar el sistema:**

- IMPORTANTE: Si es la primera vez que lo ejecuta o desea una base de datos limpia, ejecute primero:

- docker-compose down -v (Esto elimina volúmenes previos y evita conflictos de esquema).

Inicie el sistema con el comando:

- docker-compose up --build.

Espere a que el proceso termine. Sabrá que está listo cuando en Docker Desktop vea los contenedores spring_app y postgres_db en verde.

## 2. Acceso y Uso de la API

   Una vez encendido, el sistema responde en el puerto 8080. Nota: La dirección base http://localhost:8080/api/ mostrará una página de error por defecto (Whitelabel Error) porque es solo el punto de entrada al servidor.

Para ver la información real, debe ingresar a las siguientes rutas en su navegador o herramienta de pruebas:

* Gestión de Salas: http://localhost:8080/api/salas
* Gestión de Reservas: http://localhost:8080/api/reservas

## 3. Configuración de la Base de Datos

   La gestión de la persistencia es 100% automática, eliminando la necesidad de scripts manuales:

* **Motor:** Se utiliza PostgreSQL 15 contenido en Docker.
* **Esquema:** Hibernate genera automáticamente las tablas salas y reservas al iniciar la aplicación (ddl-auto: update).
* **Persistencia:** Se ha configurado un volumen de Docker para asegurar que los datos registrados no se pierdan al detener o reiniciar los contenedores.

## 4. Decisiones Técnicas Importantes Arquitectura de Capas:

Se implementó una estructura clara (Controller, Service, Repository, Model) para separar la lógica de negocio del acceso a datos, facilitando el mantenimiento.

* **Optimización de Respuesta JSON:** Se utilizó la anotación @JsonInclude(JsonInclude.Include.NON_NULL) en el modelo Sala. Esto garantiza que las respuestas sean limpias y profesionales, omitiendo campos nulos innecesarios y mostrando solo el id vinculado al momento de registrar una reserva.

* **Integridad Referencial:** Se estableció una relación @ManyToOne en la entidad Reserva para asegurar un vínculo sólido y eficiente con la entidad Sala.

## 5. Suposiciones Realizadas

- **Flujo de Usuario:** Se asume que el cliente conoce el id de la sala disponible al momento de realizar el registro de una reserva.
* **Atributo de Estado:** El campo estado en la Sala se asume como informativo ("disponible", "en mantenimiento") para que el administrador gestione la disponibilidad.
* **Estándar Temporal:** Se utiliza el formato ISO 8601 (YYYY-MM-DDTHH:mm:ss) para el manejo de fechas, garantizando compatibilidad técnica global.

## 6. Guía de Evaluación Manual (Paso a Paso)

   Para verificar que el sistema funciona correctamente, siga este orden lógico de pruebas:

**PASO 1: Creación de Salas (Muestras de Prueba)**

Utilice una herramienta como Postman para realizar tres peticiones POST a la dirección: http://localhost:8080/api/salas.

Sala A (Grande y Disponible):

JSON
```bash
{
"nombre": "Gran Salón Armadillo",
"capacidad": 50,
"ubicacion": "Piso 1 - Auditorio",
"equipamiento": "Pantalla Gigante, Sonido Envolvente",
"estado": "disponible"
}
```
Sala B (Mediana y Disponible):

JSON
```bash
{
"nombre": "Sala de Juntas B",
"capacidad": 10,
"ubicacion": "Piso 2 - Oficina 204",
"equipamiento": "Pizarra, Proyector",
"estado": "disponible"
}
```
Sala C (Pequeña en Mantenimiento):

JSON
```bash
{
"nombre": "Cabina de Enfoque",
"capacidad": 2,
"ubicacion": "Piso 3 - Coworking",
"equipamiento": "Escritorio Ergonómico",
"estado": "en mantenimiento"
}
```
**PASO 2: Realizar una Reserva**

Una vez creadas las salas, realice una petición POST a: http://localhost:8080/api/reservas. Utilizaremos el id: 1 (que corresponde al Gran Salón creado en el paso anterior).

Datos de la Reserva:

JSON
```bash
{
"nombreResponsable": "Ana Martínez",
"dniResponsable": "77889900",
"correoContacto": "ana.martinez@ejemplo.com",
"fechaHoraInicio": "2025-12-24T09:00:00",
"fechaHoraFin": "2025-12-24T12:00:00",
"proposito": "Conferencia Anual de Tecnología",
"sala": { "id": 1 }
}
```
Nota: El sistema devolverá la reserva confirmada mostrando solo el ID de la sala, cumpliendo con la optimización de respuesta JSON definida.

**PASO 3: Validación de Filtros (Búsqueda)**

Finalmente, se puede validar que los filtros funcionan ingresando estas direcciones directamente en su navegador:

* Filtrar por Capacidad (Mínimo 20 personas): 
GET http://localhost:8080/api/salas?capacidadMinima=20 (Solo debe aparecer la Sala A).

* Filtrar por Estado (En mantenimiento): 
GET http://localhost:8080/api/salas?estado=en+mantenimiento (Solo debe aparecer la Sala C).

* Ver todas las Reservas registradas: 
GET http://localhost:8080/api/reservas (Debe aparecer la reserva de Ana Martínez vinculada a la Sala 1).

# **6.1 Pruebas de Validación y Robustez (Casos de Error)**

Un sistema profesional debe prevenir datos incorrectos. El evaluador puede verificar la robustez de la API realizando estas pruebas de error:

**A. Validación de Datos Obligatorios (DNI)**

* **Acción:** Intentar registrar una reserva omitiendo el campo dniResponsable.

URL: POST http://localhost:8080/api/reservas

Cuerpo (JSON):
```bash
{
"nombreResponsable": "Juan Perez",
"correoContacto": "error@test.com",
"fechaHoraInicio": "2025-12-24T09:00:00",
"fechaHoraFin": "2025-12-24T12:00:00",
"sala": { "id": 1 }
}
```
Resultado esperado: El sistema debe rechazar la petición con un código de error (400 Bad Request), indicando que el DNI es obligatorio para la trazabilidad.

**B. Validación de Formato (Correo Inválido)**

- **Acción:** Intentar registrar una reserva con un correo electrónico que no tenga formato válido (ej. sin el @ o sin dominio).

URL: POST http://localhost:8080/api/reservas

Cuerpo (JSON):

JSON
```bash
{
"nombreResponsable": "Juan Perez",
"dniResponsable": "12345678",
"correoContacto": "correo-no-valido-com",
"fechaHoraInicio": "2025-12-24T09:00:00",
"fechaHoraFin": "2025-12-24T12:00:00",
"sala": { "id": 1 }
}
```
Resultado esperado: El sistema debe detectar que el formato no cumple con el estándar de correo electrónico y bloquear el registro.

**C. Validación de Disponibilidad (Sala ya Reservada)**

- **Acción:** Intentar realizar una segunda reserva para la misma sala en el mismo horario que ya fue ocupado en las pruebas anteriores.

URL: POST http://localhost:8080/api/reservas

Cuerpo (JSON):

JSON
```bash
{
"nombreResponsable": "Juan Perez",
"dniResponsable": "12345678",
"correoContacto": "error@test.com",
"fechaHoraInicio": "2025-12-24T09:00:00",
"fechaHoraFin": "2025-12-24T12:00:00",
"sala": { "id": 1 }
}
```
Resultado esperado: El sistema debe informar que la sala no está disponible en ese rango de tiempo, evitando el cruce de reservas.



**D. Eliminación de Sala con Reservas Activas**

- **Acción:** Intentar eliminar la Sala A (ID: 1) que ya tiene una reserva registrada a nombre de "Ana Martínez".

URL: DELETE http://localhost:8080/api/salas/1

- **Resultado esperado:** El sistema debe permitir la eliminación de la sala y, automáticamente, eliminar todas las reservas vinculadas a ella para evitar "datos huérfanos" en la base de datos.

- **Verificación:** consulta GET http://localhost:8080/api/reservas, la reserva de Ana Martínez ya no debería existir.

