This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your iOS app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

## Persistencia offline-first

El proyecto **PharmaMobil** implementa una arquitectura **Offline-First** basada en **SQLDelight** para la persistencia local en SQLite.

### Características Principales:
1. **Fuente Única de Verdad (Single Source of Truth):**
   La UI observa la base de datos SQLite local vía `Flow<List<Producto>>`. Las vistas responden automáticamente a las emisiones locales.
2. **Operatividad Offline de Lectura y Escritura:**
   En ausencia de conexión, la aplicación abre y despliega los productos guardados. Los nuevos registros se insertan localmente asignando un ID negativo e indicando la marca `pendiente = 1`.
3. **Sincronización Bidireccional Ordenada:**
   - **Fase 1 (Subida de Pendientes):** Se leen las filas con `pendiente = 1` y se envían vía `POST` al servidor PharmaSoft. Al recibir el ID asignado por el backend, se actualiza la fila local con la marca `pendiente = 0`.
   - **Fase 2 (Bajada y Reemplazo Remoto):** Se consultan los productos de PharmaSoft (`GET`) y se ejecuta un reemplazo transaccional de los registros sincronizados (`pendiente = 0`), protegiendo cualquier registro pendiente de subida.
4. **Resiliencia & Tolerancia a Fallos:**
   Si la red no está disponible, los errores se capturan suavemente y la UI informa el estado mediante la notificación: *"Sin conexión: mostrando datos guardados"*.

### Guía de Configuración & Archivos Relevantes:
- **Esquema SQLDelight:** `shared/src/commonMain/sqldelight/pe/edu/upeu/pharmamobil/db/Producto.sq`
- **Drivers SQL:** `AndroidSqliteDriver` (Android) y `NativeSqliteDriver` (iOS).
- **Repositorio Offline:** `ProductoRepositorioOffline.kt`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
