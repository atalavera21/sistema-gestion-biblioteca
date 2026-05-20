# =============================================================================
# reset-bd.ps1 - Biblioteca SENATI
#
# Reinicia las tablas TRANSACCIONALES a estado limpio:
#   - prestamos    -> vaciado
#   - devoluciones -> vaciado
#   - valoraciones -> vaciado
#
# Mantiene intactos: categorias, libros, usuarios.
# Util para probar el flujo desde cero (solicitar prestamos, devolver, etc).
#
# No reconstruye el contenedor: solo limpia tablas, es instantaneo.
#
# Uso:
#   .\reset-bd.ps1
# =============================================================================

$ErrorActionPreference = "Stop"

if (-not (Test-Path "docker-compose.yml")) {
    Write-Host "ERROR: ejecuta este script desde la raiz del proyecto." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Reseteando tablas transaccionales..." -ForegroundColor Yellow

docker cp docker\reset-produccion.sql biblioteca-db:/tmp/reset.sql | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: no se pudo copiar el archivo SQL al contenedor." -ForegroundColor Red
    Write-Host "Verifica que el contenedor 'biblioteca-db' este corriendo (docker compose ps)." -ForegroundColor Red
    exit 1
}

docker compose exec -T db bash -c 'MYSQL_PWD="$MYSQL_PASSWORD" mysql --default-character-set=utf8mb4 -u biblioteca biblioteca_db < /tmp/reset.sql'
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: el reset SQL fallo." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Listo ===" -ForegroundColor Green
Write-Host "Prestamos, devoluciones y valoraciones vaciados." -ForegroundColor Green
Write-Host "Categorias, libros y usuarios intactos." -ForegroundColor Green
Write-Host ""
