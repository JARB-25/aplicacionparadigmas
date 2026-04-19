
@echo off
setlocal

if "%JAVAFX_LIB%"=="" if exist "%~dp0javafx-sdk\lib\javafx.controls.jar" set "JAVAFX_LIB=%~dp0javafx-sdk\lib"
if "%JAVAFX_LIB%"=="" set "JAVAFX_LIB=C:\ruta\hacia\javafx-sdk-25\lib"

call "%~dp0build.bat"
if errorlevel 1 exit /b 1

echo Iniciando interfaz...
start "Productos Granja" javaw --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -cp "bin" view.Main
