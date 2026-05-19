# AGENTS.md

## Proyecto

Sistema de Gestion de Biblioteca Universitaria — curso "Gestores de Administracion Web" (PDSD-644).
Subido a GitHub (repositorio publico) y desplegado en produccion con Docker (Docker Compose) sobre un VPS. El estado actual y la bitacora de avance se llevan en `PROGRESO.md` (local, no se sube a GitHub) — leerlo al iniciar.

## Flujo de documentacion

La documentacion sigue una jerarquia en cascada. Al iniciar, el agente debe leerla en este orden:

```
AGENTS.md  →  README.md  →  documentacion/
  (vos)        (humanos)      (detalle tecnico)
```

| Nivel | Archivo | Contiene |
|---|---|---|
| 1 | `AGENTS.md` | **Este archivo.** Contexto tecnico completo para el agente. Arquitectura, reglas, stack, fase actual. |
| 2 | `README.md` | Presentacion narrativa y profesional. Explica el sistema a alto nivel con diagramas. **Hace referencia a cada documento de `documentacion/` en el lugar exacto donde corresponde.** |
| 3 | `documentacion/decisiones-tecnicas.md` | 10 ADRs + 4 diagramas C4 + seccion JPQL |
| 3 | `documentacion/casos-de-uso.md` | 3 actores + 10 casos de uso + 10 reglas de negocio + 2 validadores |
| 3 | `documentacion/stack-tecnologico.md` | Versiones, dependencias Maven, configuraciones |
| 3 | `documentacion/preguntas-guia.md` | Respuestas a las 5 preguntas del PDF con codigo de ejemplo |
| 3 | `documentacion/guia-de-diseno.md` | Paleta de colores, tipografia, espaciado, layout, componentes, accesibilidad |

## Contexto del estudiante

- Viene de .NET, nuevo en Java. Necesita acompanamiento en el aprendizaje.
- Fase actual: **app completa y desplegada en produccion con Docker sobre un VPS.**
- Para el estado detallado y los pendientes, leer `PROGRESO.md` al iniciar.

## Estructura del proyecto

```
senati/
├── README.md                          ← presentacion del proyecto (corto, profesional, narrativo)
├── AGENTS.md                          ← este archivo (contexto para agentes IA)
├── PROGRESO.md                        ← bitacora de avance (solo local, no se sube a GitHub)
├── docs/
│   └── PDSD-644_TRABAJOFINAL.pdf      ← enunciado original del curso
├── documentacion/
│   ├── decisiones-tecnicas.md          ← 10 ADRs + 4 diagramas C4 en Mermaid + seccion JPQL
│   ├── casos-de-uso.md                 ← 3 actores + 10 casos de uso + 10 reglas de negocio + 2 validadores
│   ├── stack-tecnologico.md            ← versiones exactas, deps Maven, configs (persistence.xml, ehcache.xml)
│   └── preguntas-guia.md               ← respuestas a las 5 preguntas del PDF con codigo de ejemplo
└── sistema-biblioteca/                ← proyecto Maven (equivalente a un .csproj en .NET)
    ├── pom.xml                        ← = .csproj + packages.config
    ├── src/
    │   ├── main/java/com/senati/biblioteca/
    │   │   ├── modelo/                ← entidades JPA
    │   │   ├── dao/                   ← acceso a datos
    │   │   ├── servicio/              ← logica de negocio
    │   │   ├── bean/                  ← managed beans JSF
    │   │   └── rest/                  ← API REST
    │   ├── main/resources/
    │   │   ├── META-INF/persistence.xml
    │   │   ├── ehcache.xml
    │   │   └── logback.xml
    │   ├── main/webapp/WEB-INF/
    │   │   ├── web.xml
    │   │   ├── beans.xml
    │   │   └── faces-config.xml
    │   └── test/
    └── target/                        ← build output (no se commitea)
        └── sistema-gestion-biblioteca.war
```

- `docs/` = materiales del curso (PDF, anuncios, etc.)
- `documentacion/` = documentacion propia del proyecto.
- `sistema-biblioteca/` = codigo fuente. Todos los comandos Maven se ejecutan desde esta carpeta.

## Stack obligatorio (definido por el curso)

| Capa | Tecnologia |
|---|---|
| UI | JSF (Facelets) |
| Persistencia | JPA / Hibernate |
| Inyeccion | CDI (`@Named`, `@SessionScoped`) |
| BD | MySQL — minimo 3 tablas: `libros`, `usuarios`, `prestamos` |
| REST | JAX-RS para consulta de disponibilidad |
| Autenticacion | integracion LDAP |

## Stack completo (detallado en `documentacion/stack-tecnologico.md`)

- **Java 21 LTS** (Eclipse Temurin) — se usa 21 por compatibilidad con WildFly 35
- **WildFly 35** (servidor de aplicaciones Jakarta EE 10)
- **Maven 3.9.15** (build tool)
- **JSF 4.0 + Facelets** (presentacion) + PrimeFaces 13+ (tema arya)
- **CDI 4.0 + Weld 5.x** (inyeccion de dependencias, scopes)
- **JPA 3.1 + Hibernate 6.2** (persistencia, cache 2do nivel con EhCache 3.10)
- **MySQL 8.4** (base de datos)
- **JAX-RS 3.1 + RESTEasy** (API REST)
- **Apache PDFBox 3.0** (reportes PDF — reemplaza a JasperReports)
- **Apache POI 5.3** (reportes Excel)
- **jBCrypt 0.4** (hashing de contrasenas)
- **JUnit 5 + Mockito 5** (testing)

## Arquitectura del sistema

Arquitectura **monolitica modular en 4 capas**, desplegada como un solo `.war` sobre WildFly:

```
Presentacion (JSF + Facelets XHTML + Managed Beans)
    ↓ @Inject
Negocio (CDI: LibroService, PrestamoService, AuthService, NotificacionService, Validadores)
    ↓ @Inject
Persistencia (JPA + Hibernate: entidades, DAOs, JPQL, cache 2do nivel)
    ↓ JDBC
Datos (MySQL: libros, usuarios, prestamos, ldap_users)

API REST (JAX-RS) → inyecta los mismos servicios CDI que usa JSF. Consumo externo.
```

### Capa de Presentacion (JSF)

- Vistas XHTML con Facelets. Managed Beans con `@Named` y `@SessionScoped`.
- 2 validadores JSF custom: `MaxLibrosValidator` (maximo 3 libros sin devolver) y `CodigoUniversitarioValidator` (formato de codigo).
- Convertidor de fechas `dd/MM/yyyy`.

### Capa de Negocio (CDI)

- Servicios `@ApplicationScoped` inyectados via `@Inject`.
- `AuthService` es una **interfaz** con implementacion `LdapAuthService` (simulada). Disenada para reemplazo futuro por LDAP real via `@Alternative`.
- `NotificacionService` con `@Schedule` para recordatorios automaticos.

### Capa de Persistencia (JPA + Hibernate)

- Entidades: `Libro`, `Usuario`, `Prestamo` + tabla auxiliar `LdapUser` (fuera de JPA).
- Relaciones: `Libro 1→* Prestamo` (`@OneToMany`), `Usuario 1→* Prestamo` (`@OneToMany`).
- `Prestamo.libro` y `Prestamo.usuario` con `@ManyToOne` + `@JoinColumn`.
- Cache de segundo nivel con EhCache (entidad `Libro` anotada `@Cacheable`).
- Fetch: `Libro.prestamos` LAZY, `Prestamo.libro` y `Prestamo.usuario` EAGER.
- Consultas JPQL via `EntityManager` y `TypedQuery<T>`, parametros `:param`.

### API REST (JAX-RS)

- Dentro del monolito, comparte servicios CDI via `@Inject`.
- Endpoints: `GET /api/libros/{id}/disponibilidad`, `GET /api/libros?titulo=&autor=`.
- Publica, sin autenticacion. Solo lectura. Consumo externo.

### Base de Datos (MySQL)

- **Dominio:** `libros`, `usuarios`, `prestamos` — gestionados por JPA/Hibernate.
- **Autenticacion:** `ldap_users` (username, password, estado) — accedido por JDBC directo.
- La tabla `usuarios` NO tiene campo `password`. Separacion de responsabilidades (SRP).

### Autenticacion (LDAP simulado)

- Interfaz `AuthService` con metodo `autenticar(username, password)`.
- Implementacion `LdapAuthService`: valida contra `ldap_users` (BCrypt + estado ACTIVO).
- Flujo: login → validar dominio `@senati.pe` → `AuthService.autenticar()` → `ldap_users`.
- Disenada para reemplazo futuro: implementar `RealLdapAuthService` con JNDI y cambiar una anotacion CDI.

## Reglas de negocio clave

| Regla | Descripcion |
|---|---|
| RB-01 | Maximo 3 libros en estado ACTIVO o VENCIDO por estudiante (validador `MaxLibrosValidator`) |
| RB-02 | Solo prestar si `isDisponible()` retorna `true` |
| RB-03 | Disponibilidad derivada: `stockTotal - COUNT(prestamos ACTIVOS o VENCIDOS)`. No se persiste. |
| RB-04 | Duracion estandar de prestamo: 14 dias |
| RB-05 | Devolucion con retraso → estado `PENALIZADO` |
| RB-06 | Estudiante penalizado no puede solicitar nuevos prestamos |
| RB-07 | No eliminar libro con prestamos activos/vencidos |

## Actores

- **Administrador:** CRUD libros, configurar reglas, reportes PDF/Excel, registrar devoluciones.
- **Estudiante:** Navegar catalogo (sin login), solicitar prestamos (con login), historial, notificaciones.
- **Consumidor externo:** API REST para consulta de disponibilidad.

## JPQL (uso en el sistema)

Todas las consultas usan JPQL via `EntityManager` sobre entidades, no SQL nativo sobre tablas:

- Reportes: libros mas prestados, libros por categoria, autores mas populares, prestamos en mora.
- Funcionales: disponibilidad (subquery JPQL), busqueda con filtros dinamicos, historial por usuario, prestamos por rango de fechas.
- `JOIN FETCH` para evitar N+1 en historial de prestamos.
- `BETWEEN`, `GROUP BY`, `ORDER BY`, parametros `:param`.
- Ver seccion JPQL en `documentacion/decisiones-tecnicas.md`.

## Entregables — estado actual

| Entregable | Estado |
|---|---|
| README.md | Completado |
| Decisiones tecnicas (10 ADRs + 4 diagramas C4 + JPQL) | Completado |
| Casos de uso (10 UCs + reglas + validadores) | Completado |
| Stack tecnologico (versiones + deps Maven) | Completado |
| Preguntas guia (5 preguntas del PDF) | Completado |
| Codigo | Completado |
| Despliegue (Docker Compose en VPS) | Completado |

## Fase actual y siguientes pasos

**Fase actual:** App completa, dockerizada y desplegada en produccion (Docker Compose sobre un VPS Ubuntu 24.04).

**Estado y pendientes:** se llevan en `PROGRESO.md` (bitacora local, gitignored). El agente DEBE leer ese archivo al iniciar para saber en que se esta trabajando y desde donde retomar.

## Nota importante

El estudiante prefiere documentacion clara y profesional. Las decisiones se toman en conjunto. Si algo no esta documentado o hay dudas, preguntar antes de asumir. El README debe ser corto y narrativo. Los documentos tecnicos pueden ser extensos y detallados.

Los archivos `.md` usan Mermaid para diagramas (renderiza nativo en GitHub). Si se detectan errores de sintaxis Mermaid, corregirlos.
