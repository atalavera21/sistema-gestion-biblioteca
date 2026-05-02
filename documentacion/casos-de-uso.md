# Casos de Uso — Sistema de Gestion de Biblioteca Universitaria

## 1. Actores del Sistema

### Administrador

Representa al personal bibliotecario (5 empleados administrativos). Gestiona el sistema de forma interna.

**Responsabilidades:**
- Alta, edicion y baja de libros en el catalogo
- Configuracion de reglas de prestamo (duracion, limite de libros por usuario)
- Generacion de reportes automatizados (PDF/Excel)
- Gestion de notificaciones y penalizaciones
- Monitoreo de prestamos activos y vencidos

**Acceso:** Requiere autenticacion LDAP con rol `ADMIN`.

---

### Estudiante

Usuario final de la biblioteca (~5,000 estudiantes). Interactua con el sistema para consultar y solicitar libros.

**Capacidades:**
- Navegar el catalogo de libros **sin autenticacion**
- Buscar libros por titulo, autor o categoria
- Ver detalle de un libro (disponibilidad, stock, datos)
- Solicitar prestamos (requiere login)
- Consultar su historial de prestamos
- Consultar su direccion de entrega registrada

**Acceso:** Navegacion publica para catalogo. Autenticacion LDAP requerida para prestamos e historial.

---

### Consumidor Externo

Sistema o aplicacion de terceros que consulta disponibilidad via API REST.

**Capacidades:**
- Consultar disponibilidad de un libro por ID
- Buscar libros con filtros (titulo, autor)

**Acceso:** Publico. Sin autenticacion. Solo lectura.

---

## 2. Casos de Uso

### UC-01: Autenticacion de Usuario

| Aspecto | Detalle |
|---|---|
| **Actor** | Estudiante, Administrador |
| **Precondicion** | El usuario no tiene una sesion activa |
| **Flujo principal** | 1. El usuario ingresa `username` y `password` en la pantalla de login. 2. El sistema valida que el username pertenezca al dominio institucional (`@senati.pe`). 3. El sistema consulta `AuthService.autenticar(username, password)`. 4. `LdapAuthService` busca el usuario en `ldap_users`. 5. Verifica que la contraseña coincida y el estado sea `ACTIVO`. 6. El sistema crea la sesion y redirige al catalogo. |
| **Flujo alternativo** | 4a. Usuario no encontrado en `ldap_users` → error "Credenciales invalidas". 4b. Contraseña incorrecta → error "Credenciales invalidas". 4c. Estado `INACTIVO` → error "Usuario inactivo. Contacte al administrador". |
| **Postcondicion** | Sesion CDI activa (`@SessionScoped`). El `AuthBean` almacena el `codigoUniversitario` autenticado. |

---

### UC-02: Navegar Catalogo de Libros

| Aspecto | Detalle |
|---|---|
| **Actor** | Estudiante (sin autenticacion), Administrador |
| **Precondicion** | Ninguna |
| **Flujo principal** | 1. El usuario accede a la pagina de catalogo. 2. El sistema carga la lista de libros desde `LibroService` con paginacion. 3. Para cada libro, el sistema calcula `isDisponible()` en tiempo real. 4. El usuario puede filtrar por categoria, titulo o autor. 5. El sistema aplica los filtros via JPQL y recarga la vista. |
| **Flujo alternativo** | 4a. Sin resultados → mensaje "No se encontraron libros con los criterios indicados". |
| **Postcondicion** | El usuario visualiza el catalogo con disponibilidad actualizada. |
| **JPQL** | `SELECT l FROM Libro l WHERE l.categoria = :categoria` / `WHERE LOWER(l.titulo) LIKE :termino` |

---

### UC-03: Ver Detalle de Libro

| Aspecto | Detalle |
|---|---|
| **Actor** | Estudiante, Administrador |
| **Precondicion** | El libro existe en el catalogo |
| **Flujo principal** | 1. El usuario selecciona un libro del catalogo. 2. El sistema carga los datos completos: titulo, autor, categoria, stock total, disponibilidad calculada, portada (si existe). 3. El sistema muestra el detalle en una vista dedicada. |
| **Postcondicion** | El usuario ve la ficha completa del libro. Si esta autenticado, ve el boton "Solicitar Prestamo". |

---

### UC-04: Gestionar Libros (CRUD)

| Aspecto | Detalle |
|---|---|
| **Actor** | Administrador |
| **Precondicion** | Autenticado con rol `ADMIN` |
| **Flujo principal - Crear** | 1. El administrador accede a "Nuevo Libro". 2. Ingresa: titulo, autor, categoria, stock total. 3. El sistema valida campos obligatorios y formato ISBN (validador custom). 4. El sistema persiste el libro via JPA. 5. Mensaje de confirmacion. |
| **Flujo principal - Editar** | 1. El administrador selecciona un libro del catalogo. 2. Modifica los campos necesarios. 3. El sistema valida y actualiza via JPA. |
| **Flujo principal - Eliminar** | 1. El administrador selecciona un libro. 2. El sistema verifica que no tenga prestamos activos. 3. Si no tiene, elimina el registro. 4. Si tiene prestamos activos, muestra error: "No se puede eliminar. El libro tiene prestamos pendientes." |
| **Validaciones** | ISBN: formato valido (validador JSF custom). Stock total: valor numerico mayor a 0. Titulo y autor: obligatorios, no vacios. |
| **Postcondicion** | Libro creado, editado o eliminado en la base de datos. |

---

### UC-05: Solicitar Prestamo de Libro

| Aspecto | Detalle |
|---|---|
| **Actor** | Estudiante (autenticado) |
| **Precondicion** | El estudiante tiene sesion activa. El libro existe y esta disponible. |
| **Flujo principal** | 1. El estudiante ve el detalle de un libro y presiona "Solicitar Prestamo". 2. El sistema verifica disponibilidad en tiempo real (`isDisponible()`). 3. El sistema valida que el estudiante no exceda el limite de libros prestados (validador `MaxLibrosValidator`: maximo 3 libros sin devolver). 4. El sistema valida que el estudiante no tenga penalizaciones activas. 5. El sistema crea un nuevo `Prestamo` con estado `ACTIVO`. 6. Asigna fecha actual como `fechaPrestamo` y calcula `fechaDevolucionEstimada` (+14 dias). 7. El sistema persiste el prestamo via JPA. 8. Mensaje de confirmacion: "Prestamo registrado. Fecha limite de devolucion: dd/MM/yyyy". |
| **Flujo alternativo** | 2a. Libro no disponible → error "El libro no tiene stock disponible en este momento". 3a. Excede limite de 3 libros → error "Has alcanzado el limite de 3 libros sin devolver". 4a. Penalizacion activa → error "No puedes solicitar prestamos hasta regularizar tu situacion". |
| **Postcondicion** | Prestamo registrado en BD. Stock disponible se reduce efectivamente (por calculo en tiempo real). |
| **Regla de negocio** | El prestamo se registra automaticamente. No requiere aprobacion manual. |

---

### UC-06: Registrar Devolucion de Libro

| Aspecto | Detalle |
|---|---|
| **Actor** | Administrador |
| **Precondicion** | El prestamo existe y esta en estado `ACTIVO` o `VENCIDO` |
| **Flujo principal** | 1. El administrador busca el prestamo por ID o por codigo del estudiante. 2. El sistema muestra los datos del prestamo y el libro asociado. 3. El administrador confirma la devolucion. 4. El sistema asigna `fechaDevolucionReal = now()`. 5. Si `fechaDevolucionReal > fechaDevolucionEstimada`, el estado cambia a `PENALIZADO`. Si no, cambia a `DEVUELTO`. 6. El sistema persiste los cambios via JPA. 7. Mensaje de confirmacion. |
| **Flujo alternativo** | 5a. Devuelto con retraso → se notifica al estudiante sobre la penalizacion. |
| **Postcondicion** | Prestamo finalizado. El libro vuelve a estar disponible (el calculo de disponibilidad lo refleja automaticamente). |

---

### UC-07: Consultar Historial de Prestamos

| Aspecto | Detalle |
|---|---|
| **Actor** | Estudiante (autenticado), Administrador |
| **Precondicion** | Sesion activa |
| **Flujo principal** | 1. El usuario accede a "Mi Historial" (estudiante ve solo lo propio; administrador puede buscar por estudiante). 2. El sistema consulta `PrestamoService` con JPQL filtrando por `usuario.codigoUniversitario`. 3. El sistema muestra tabla con: titulo del libro, fecha de prestamo, fecha de devolucion estimada, fecha de devolucion real, estado. 4. Los prestamos activos o vencidos aparecen al inicio con indicador visual. |
| **JPQL** | `SELECT p FROM Prestamo p WHERE p.usuario.codigoUniversitario = :codigo ORDER BY p.fechaPrestamo DESC` |
| **Postcondicion** | El usuario visualiza su historial completo. |

---

### UC-08: Generar Reportes

| Aspecto | Detalle |
|---|---|
| **Actor** | Administrador |
| **Precondicion** | Autenticado con rol `ADMIN` |
| **Flujo principal** | 1. El administrador accede al panel de reportes. 2. Selecciona el tipo de reporte: libros mas prestados, libros por categoria, autores mas populares, prestamos vencidos. 3. Selecciona el formato de salida: PDF o Excel. 4. Selecciona el periodo (ultimo mes, ultimo trimestre, año actual, personalizado). 5. El sistema ejecuta la consulta JPQL correspondiente. 6. El sistema genera el archivo PDF o Excel con los resultados. 7. El administrador descarga el archivo. |
| **Reportes disponibles** | Libros mas prestados (top 10). Libros por categoria (agrupado y contado). Autores mas populares (top 10). Prestamos activos/vencidos. Prestamos con penalizacion. |
| **Margen de error** | Inferior al 2%, garantizado por consultas JPQL directas sobre la base de datos transaccional. |
| **Postcondicion** | Archivo PDF/Excel generado y descargado. |

---

### UC-09: Consultar Disponibilidad via API REST

| Aspecto | Detalle |
|---|---|
| **Actor** | Consumidor Externo |
| **Precondicion** | Ninguna |
| **Endpoint 1** | `GET /api/libros/{id}/disponibilidad` → Retorna `{ "id": 1, "titulo": "...", "disponible": true, "stockDisponible": 3 }` |
| **Endpoint 2** | `GET /api/libros?titulo=java&autor=bloch` → Retorna lista de libros con disponibilidad. |
| **Flujo** | 1. El consumidor externo realiza una peticion GET al endpoint. 2. `DisponibilidadResource` inyecta `LibroService` via CDI. 3. `LibroService` consulta el libro y calcula `isDisponible()`. 4. El recurso JAX-RS serializa la respuesta en JSON. |
| **Postcondicion** | Respuesta JSON con los datos de disponibilidad. |

---

### UC-10: Gestion de Notificaciones y Penalizaciones

| Aspecto | Detalle |
|---|---|
| **Actor** | Sistema (automatico), Administrador |
| **Precondicion** | Prestamos activos en el sistema |
| **Flujo principal** | 1. Un proceso programado (`@Schedule` en `NotificacionService`) se ejecuta diariamente. 2. Identifica prestamos cuya `fechaDevolucionEstimada` esta a 3 dias. 3. Envia recordatorio por correo al estudiante. 4. Identifica prestamos cuya `fechaDevolucionEstimada` es hoy. 5. Envia alerta de vencimiento. 6. Identifica prestamos con `fechaDevolucionEstimada` ya vencida. 7. Cambia su estado de `ACTIVO` a `VENCIDO`. |
| **Regla de negocio** | Un estudiante con un libro `VENCIDO` o `PENALIZADO` sin regularizar no puede solicitar nuevos prestamos. |
| **Postcondicion** | Notificaciones enviadas. Estados actualizados. |

---

## 3. Reglas de Negocio

| Regla | Descripcion | Donde se aplica |
|---|---|---|
| **RB-01** | Un estudiante no puede tener mas de **3 libros** en estado `ACTIVO` o `VENCIDO` simultaneamente. | UC-05 (validador `MaxLibrosValidator`) |
| **RB-02** | Un libro solo puede prestarse si `isDisponible()` retorna `true` (stockTotal > prestamos activos + vencidos). | UC-05 |
| **RB-03** | La disponibilidad (`stockDisponible`) es un valor **derivado**, no persistido. Se calcula: `stockTotal - COUNT(prestamos ACTIVOS o VENCIDOS)`. | Todo el sistema |
| **RB-04** | La duracion estandar de un prestamo es de **14 dias**, configurable por el administrador. | UC-05 |
| **RB-05** | Si un libro se devuelve despues de la fecha estimada, el prestamo se marca como `PENALIZADO`. | UC-06 |
| **RB-06** | Un estudiante con penalizaciones activas no puede solicitar nuevos prestamos. | UC-05 |
| **RB-07** | Solo se puede eliminar un libro si no tiene prestamos activos o vencidos asociados. | UC-04 |
| **RB-08** | El `stockTotal` de un libro debe ser mayor a 0 al momento de crearlo. | UC-04 |
| **RB-09** | Las notificaciones se envian por correo electronico 3 dias antes del vencimiento y el mismo dia. | UC-10 |
| **RB-10** | El dominio de correo institucional para autenticacion es `@senati.pe`. | UC-01 |

---

## 4. Validadores Personalizados

### Validador 1: `MaxLibrosValidator`

| Aspecto | Detalle |
|---|---|
| **Proposito** | Validar que un estudiante no exceda el limite de 3 libros sin devolver |
| **Tipo** | `@FacesValidator` |
| **Logica** | Cuenta los prestamos en estado `ACTIVO` o `VENCIDO` del usuario autenticado. Si >= 3, rechaza la solicitud. |
| **Mensaje** | "Has alcanzado el limite de 3 libros sin devolver. Devuelve al menos uno para solicitar otro." |

### Validador 2: `CodigoUniversitarioValidator`

| Aspecto | Detalle |
|---|---|
| **Proposito** | Validar el formato del codigo universitario en formularios de registro |
| **Tipo** | `@FacesValidator` |
| **Logica** | Verifica que el codigo coincida con el patron institucional (ej: `U2023XXXXX`). |
| **Mensaje** | "El codigo universitario no tiene un formato valido. Ejemplo: U202312345." |

---

## 5. Servicio de Notificaciones Programadas

| Notificacion | ¿Cuando? | ¿A quien? | Medio |
|---|---|---|---|
| Recordatorio de devolucion | 3 dias antes de `fechaDevolucionEstimada` | Estudiante | Correo electronico |
| Alerta de vencimiento | El mismo dia de `fechaDevolucionEstimada` | Estudiante | Correo electronico |
| Penalizacion aplicada | Al registrar devolucion con retraso | Estudiante | Correo electronico |
| Prestamo vencido sin devolver | Diariamente para prestamos con fecha vencida | Administrador | Panel de control |
