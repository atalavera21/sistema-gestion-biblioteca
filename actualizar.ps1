# =============================================================================
# actualizar.ps1 - Biblioteca SENATI
#
# Trae los ultimos cambios desde GitHub y reconstruye el contenedor Docker.
# Usalo cada vez que haya cambios de codigo (xhtml, css, java).
#
# NO toca la base de datos: los datos persisten en el volumen db_data.
# Si en algun momento hay cambios en el seed o esquema, te aviso para
# correr el flujo completo manualmente.
#
# Uso:
#   .\actualizar.ps1
# =============================================================================

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "=== Actualizando Biblioteca SENATI ===" -ForegroundColor Cyan
Write-Host ""

# --- 1. Verificar requisitos ---
if (-not (Test-Path "docker-compose.yml")) {
    Write-Host "ERROR: No se encontro docker-compose.yml." -ForegroundColor Red
    Write-Host "Ejecuta este script desde la raiz del proyecto." -ForegroundColor Red
    exit 1
}

if (-not (Test-Path ".env")) {
    Write-Host "ERROR: No se encontro el archivo .env." -ForegroundColor Red
    Write-Host "Copia .env.example como .env y define DB_USER, DB_PASSWORD, DB_ROOT_PASSWORD." -ForegroundColor Red
    exit 1
}

# --- 2. Traer cambios de GitHub ---
Write-Host "[1/3] git pull..." -ForegroundColor Yellow
git pull
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: git pull fallo. Revisa tu conexion o el estado del repo." -ForegroundColor Red
    exit 1
}

# --- 3. Reconstruir y levantar contenedores ---
Write-Host ""
Write-Host "[2/3] docker compose up -d --build..." -ForegroundColor Yellow
Write-Host "(esto puede tomar varios minutos la primera vez)" -ForegroundColor DarkGray
docker compose up -d --build
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: docker compose fallo." -ForegroundColor Red
    exit 1
}

# --- 4. Esperar a que WildFly despliegue ---
Write-Host ""
Write-Host "[3/3] Esperando que WildFly termine de desplegar..." -ForegroundColor Yellow

$timeout = 180   # 3 minutos
$elapsed = 0
$listo = $false

while ($elapsed -lt $timeout) {
    Start-Sleep -Seconds 5
    $elapsed += 5

    $log = docker compose logs app 2>$null
    if ($log -match 'Deployed "ROOT.war"' -or $log -match 'WFLYSRV0010') {
        $listo = $true
        break
    }

    Write-Host "  ...esperando ($elapsed s)" -ForegroundColor DarkGray
}

Write-Host ""
if ($listo) {
    Write-Host "=== Listo ===" -ForegroundColor Green
    Write-Host "Aplicacion disponible en: http://localhost/" -ForegroundColor Green
    Write-Host ""
    Write-Host "Credenciales:" -ForegroundColor DarkGray
    Write-Host "  Admin:      ADM-001 / admin123" -ForegroundColor DarkGray
    Write-Host "  Estudiante: EST-001 / password" -ForegroundColor DarkGray
} else {
    Write-Host "AVISO: Pasaron $timeout segundos sin detectar 'Deployed ROOT.war'." -ForegroundColor Yellow
    Write-Host "Revisa los logs manualmente con: docker compose logs app" -ForegroundColor Yellow
}

Write-Host ""
