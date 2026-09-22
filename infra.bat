@echo off
REM Start/stop dev infra (PostgreSQL, Redis, Kafka) via Docker.
REM   infra.bat        -> start
REM   infra.bat down   -> stop (data kept)
setlocal
cd /d "%~dp0"

docker info >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Docker is not running. Start Docker Desktop first.
    pause
    exit /b 1
)

if /i "%~1"=="down" (
    docker compose down
    goto :eof
)

docker compose up -d
docker compose ps
