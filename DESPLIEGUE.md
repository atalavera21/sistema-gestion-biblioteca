# Guía de Despliegue con Docker — Biblioteca SENATI

Guía para poner el sistema en producción usando **Docker** en un VPS.

> Tiempo estimado: ~30-45 min (más el primer build, que es lento).

---

## Cómo funciona Docker aquí

Se prepararon 3 archivos. Vale la pena entenderlos:

| Archivo | Qué hace |
|---|---|
| `Dockerfile` | La "receta" de la imagen de tu app: compila el `.war` con Maven y lo despliega dentro de WildFly, ya con el datasource configurado. |
| `docker/datasource.cli` | El script que, durante el build, le enseña a WildFly cómo conectarse a MySQL. |
| `docker-compose.yml` | Orquesta **dos contenedores** — la app (`app`) y la base de datos (`db`) — y los conecta en una red privada. |

Con **un comando** (`docker compose up -d --build`) Docker:
1. Compila tu proyecto.
2. Construye la imagen de la app.
3. Levanta MySQL.
4. Levanta WildFly con la app, conectada a MySQL.

---

## Roadmap

| Fase | Dónde | Qué |
|---|---|---|
| 0 | — | Probar local primero (recomendado) |
| 1 | Hetzner | Crear el VPS |
| 2 | Tu laptop | Conectarte por SSH |
| 3 | VPS | Instalar Docker |
| 4 | VPS | Traer el proyecto |
| 5 | VPS | `docker compose up -d --build` |
| 6 | VPS | Cargar datos iniciales |
| 7 | VPS | Abrir el puerto y verificar |
| 8 | VPS | Reset para demos |

---

## Fase 0 — Probar local primero (recomendado)

Antes del VPS, prueba TODO en tu laptop. Si funciona aquí, funciona en el VPS
(esa es la gracia de Docker).

1. Instala **Docker Desktop** para Windows (docker.com/products/docker-desktop).
2. En la carpeta del proyecto, en PowerShell:
   ```
   docker compose up -d --build
   ```
   El primer build tarda varios minutos (descarga Maven, WildFly, MySQL).
3. Cuando termine, abre: `http://localhost:8080/sistema-gestion-biblioteca/`
4. Carga los datos iniciales (ver Fase 6, usando los comandos de PowerShell).
5. Para apagarlo: `docker compose down`

Si esto funciona, continúa con el VPS confiado.

---

## Fase 1 — Crear el VPS

1. Cuenta en **Hetzner Cloud** (console.hetzner.cloud).
2. Crear servidor:
   - Imagen: **Ubuntu 24.04**
   - Tipo: **CX22** (2 vCPU, 4 GB RAM)
   - Ubicación: **Ashburn, VA (EE.UU.)**
   - Acceso: contraseña root o clave SSH.
3. Anota la **IP pública**.

---

## Fase 2 — Conectarte

Desde **PowerShell**:
```
ssh root@TU_IP
```

---

## Fase 3 — Instalar Docker en el VPS

Ya conectado al servidor:
```
apt update && apt upgrade -y
curl -fsSL https://get.docker.com | sh
```

Verifica:
```
docker --version
docker compose version
```

---

## Fase 4 — Traer el proyecto

```
apt install -y git
cd /opt
git clone TU_REPOSITORIO_GIT biblioteca
cd biblioteca
```

> Si aún no subiste el proyecto a GitHub, hazlo primero (avísame si necesitas ayuda).

---

## Fase 5 — Levantar todo

```
docker compose up -d --build
```

Esto compila y levanta los dos contenedores. El primer build tarda ~5-10 min.

Verifica que estén corriendo:
```
docker compose ps
```

Espera ~1 minuto a que WildFly termine de desplegar la app. Revisa el log:
```
docker compose logs app
```
Busca una línea como `Deployed "sistema-gestion-biblioteca.war"`.

---

## Fase 6 — Cargar datos iniciales

Cuando la app ya desplegó, las tablas existen (las crea Hibernate). Ahora carga
el catálogo y los usuarios, y deja el sistema limpio:

**En el VPS (Linux):**
```
docker compose exec -T db mysql -u biblioteca -pREDACTED biblioteca_db < sistema-biblioteca/src/main/resources/seed-biblioteca.sql
docker compose exec -T db mysql -u biblioteca -pREDACTED biblioteca_db < docker/reset-produccion.sql
```

**Si lo pruebas local en Windows (PowerShell)** usa `Get-Content` en vez de `<`:
```
Get-Content sistema-biblioteca\src\main\resources\seed-biblioteca.sql | docker compose exec -T db mysql -u biblioteca -pREDACTED biblioteca_db
Get-Content docker\reset-produccion.sql | docker compose exec -T db mysql -u biblioteca -pREDACTED biblioteca_db
```

El seed carga categorías, libros y usuarios; el reset borra los préstamos de
ejemplo, dejando el sistema limpio.

---

## Fase 7 — Abrir el puerto y verificar

En el VPS:
```
ufw allow OpenSSH
ufw allow 8080
ufw --force enable
```

> En Hetzner, revisa además que el **Firewall del panel** permita el puerto 8080.

Abre en tu navegador:
```
http://TU_IP:8080/sistema-gestion-biblioteca/
```

Credenciales: admin `ADM-001 / admin123`, estudiantes `EST-001..EST-005 / password`.

---

## Fase 8 — Reset para tus demos

Antes de cada presentación, para tener datos limpios:
```
docker compose exec -T db mysql -u biblioteca -pREDACTED biblioteca_db < docker/reset-produccion.sql
```

---

## Actualizar la app más adelante

Cuando hagas mejoras:
```
cd /opt/biblioteca
git pull
docker compose up -d --build
```

Docker reconstruye solo lo que cambió. La base de datos NO se pierde (vive en un
volumen aparte).

---

## Comandos Docker útiles (para aprender)

| Comando | Qué hace |
|---|---|
| `docker compose ps` | Ver los contenedores y su estado |
| `docker compose logs app` | Ver los logs de la app |
| `docker compose logs -f app` | Ver los logs en vivo |
| `docker compose restart app` | Reiniciar solo la app |
| `docker compose down` | Apagar todo (los datos se conservan) |
| `docker compose up -d` | Encender todo |
| `docker compose up -d --build` | Encender reconstruyendo la imagen |
| `docker compose exec db bash` | Entrar al contenedor de MySQL |

> `docker compose down -v` borra TAMBIÉN la base de datos (el `-v` elimina el
> volumen). Úsalo solo si quieres empezar de cero por completo.

---

## Opcional — verse más profesional (después)

- **Dominio propio** (~$10/año) apuntando a la IP del VPS.
- **HTTPS**: un contenedor extra de Nginx + Let's Encrypt delante de la app,
  para `https://tudominio.com` sin el `:8080`.

Lo vemos cuando quieras.

---

## Checklist antes de presentar

- [ ] `http://TU_IP:8080/sistema-gestion-biblioteca/` responde
- [ ] Puedo iniciar sesión como admin y como estudiante
- [ ] Corrí el reset → sin préstamos ni devoluciones
- [ ] Tengo la versión local de respaldo (`scripts/start.ps1`) por si falla
      el internet del salón

---

## Notas

- Las contraseñas de la base de datos (`REDACTED`, `REDACTED`) están en
  `docker-compose.yml` y `docker/datasource.cli`. Solo se usan entre contenedores
  en una red privada. Si quieres cambiarlas, hazlo en **ambos** archivos.
- El paso más delicado es la Fase 5 (el build). Si falla, copia el error y lo
  resolvemos — por eso conviene hacer la Fase 0 (probar local) primero.
