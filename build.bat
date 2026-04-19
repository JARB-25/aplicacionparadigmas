@echo off
setlocal

if "%JAVAFX_LIB%"=="" set "JAVAFX_LIB=C:\ruta\hacia\javafx-sdk-25\lib"

if not exist "%JAVAFX_LIB%\javafx.controls.jar" (
    echo No se encontro JavaFX en la ruta configurada:
    echo %JAVAFX_LIB%
    echo.
    echo Configura la variable JAVAFX_LIB dentro de este archivo o en tu entorno.
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
