# Productos Granja

## IMPORTANTE

En **PowerShell**, no ejecutes:

```powershell
run.bat
```

Debes ejecutar:

```powershell
.\run.bat
```

Proyecto Java con interfaz JavaFX usando FXML para gestionar productos agricolas.

## Abrir en VS Code

1. Abre la carpeta `productos` en VS Code.
2. Instala la extension `Extension Pack for Java`.
3. Si quieres abrir el FXML visualmente, instala `SceneBuilder`.

## Requisitos

- JDK 25 instalado
- JavaFX SDK instalado en cualquier ruta del equipo

Opciones para que el proyecto encuentre JavaFX:

1. Definir la variable de entorno `JAVAFX_LIB`
2. O copiar el SDK dentro del proyecto en la carpeta `javafx-sdk\lib`

Ejemplo de ruta valida para `JAVAFX_LIB`:

```text
C:\ruta\hacia\javafx-sdk-25\lib
```

Si no configuras eso, los scripts mostraran un mensaje indicando que falta JavaFX.

## Compilar

```bat
.\build.bat
```

## Ejecutar

```powershell
.\run.bat
```

VS Code tambien puede ejecutar la configuracion `Ejecutar Productos`, que ya incluye los argumentos necesarios de JavaFX.

## Estructura importante

- `src/model`: modelo de datos
- `src/service`: logica de inventario
- `src/controller`: controlador JavaFX
- `src/view/GestionProductos.fxml`: interfaz FXML
- `src/view/Main.java`: arranque de la aplicacion

## GitHub

El archivo `.gitignore` ya excluye los archivos generados por Eclipse y la carpeta `bin`, para que el repositorio suba limpio.
