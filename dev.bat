@echo off
REM Start backend (Spring Boot :8080) and frontend (Vite :5173) in separate windows.
REM Docker infra is NOT started here - run infra.bat first.
setlocal
set "ROOT=%~dp0"

REM Dev default: let Hibernate create tables on an empty DB (override by setting it beforehand)
if not defined JPA_DDL_AUTO set "JPA_DDL_AUTO=validate"

REM --- pick a Gradle launcher ---
set "GRADLE_CMD="
if exist "%ROOT%backend\gradlew.bat" (
    set "GRADLE_CMD=gradlew.bat"
) else (
    where gradle >nul 2>nul && set "GRADLE_CMD=gradle"
)
if not defined GRADLE_CMD (
    echo [ERROR] No gradlew.bat in backend\ and no gradle on PATH.
    echo         Run once in backend\:  gradle wrapper --gradle-version 8.10
    pause
    exit /b 1
)

REM --- frontend deps + env ---
if not exist "%ROOT%frontend\.env" copy "%ROOT%frontend\.env.example" "%ROOT%frontend\.env" >nul
if not exist "%ROOT%frontend\node_modules" (
    echo Installing frontend dependencies...
    pushd "%ROOT%frontend"
    call yarn install
    popd
)

start "logistics-backend" /d "%ROOT%backend" cmd /k %GRADLE_CMD% bootRun
start "logistics-frontend" /d "%ROOT%frontend" cmd /k yarn dev

echo Started. Backend: http://localhost:8080/swagger-ui.html  Frontend: http://localhost:5173
