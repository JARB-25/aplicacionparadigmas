@echo off
setlocal

if "%JAVAFX_LIB%"=="" if exist "%~dp0javafx-sdk\lib\javafx.controls.jar" set "JAVAFX_LIB=%~dp0javafx-sdk\lib"
if "%JAVAFX_LIB%"=="" set "JAVAFX_LIB=C:\ruta\hacia\javafx-sdk-25\lib"

if not exist "%JAVAFX_LIB%\javafx.controls.jar" (
    echo No se encontro JavaFX en la ruta configurada:
    echo %JAVAFX_LIB%
    echo.
    echo Opciones:
    echo 1. Define la variable de entorno JAVAFX_LIB con la ruta ...\javafx-sdk-25\lib
    echo 2. O copia tu SDK dentro de la carpeta del proyecto como: javafx-sdk\lib
    exit /b 1
)

if not exist "bin" mkdir bin

javac -cp "%JAVAFX_LIB%\*" -d bin ^
 src\model\Producto.java ^
 src\service\ProductoService.java ^
 src\controller\ProductoController.java ^
 src\view\Main.java

if errorlevel 1 (
    echo.
    echo La compilacion fallo.
    exit /b 1
)

if not exist "bin\view" mkdir "bin\view"
copy /Y "src\view\GestionProductos.fxml" "bin\view\GestionProductos.fxml" >nul

echo.
echo Compilacion completada correctamente.
