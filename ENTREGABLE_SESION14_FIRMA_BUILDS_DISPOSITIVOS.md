# INFORME TÉCNICO ENTREGABLE — SESIÓN 14
## Empaquetamiento, Firma Digital, Validación en Dispositivos Reales y Congelación de Alcance (PharmaMobil)

**Proyecto:** PharmaMobil — Sistema Móvil Multiplataforma (Kotlin Multiplatform & Compose)  
**Backend Vinculado:** PharmaSoft REST API  
**Institución:** Universidad Peruana Unión (UPeU) — Facultad de Ingeniería y Arquitectura  
**Estudiante:** Nick Daniel Pachamora Villanueva  
**Docente:** Mg. David Huaynate  
**Fecha:** 11 de Noviembre de 2026  
**Versión de Entrega:** `1.0.0` (Build `1`) | **Tag Git:** `v1.0-final`  
**Estado:** Alcance Congelado, Builds Firmados y Validados en Hardware Real  

---

## 1. CONGELACIÓN DE ALCANCE (SCOPE FREEZE)

En conformidad con el requisito **CA-01** y los lineamientos de la Sesión 14, se declara formalmente la **Congelación Definitiva de Alcance (*Scope Freeze*)** a 7 días de la sustentación final del proyecto PharmaMobil.

### 1.1 Directrices del Congelamiento de Alcance
1. **Cierre de Nuevas Funcionalidades:** Queda clausurada la incorporación de nuevos casos de uso, módulos o endpoints.
2. **Estabilidad Arquitectónica:** No se admiten refactorizaciones estructurales en la capa de datos (`SQLDelight`), el dominio ni en la inyección de dependencias (`Koin`).
3. **Foco Exclusivo de la Entrega:** Los esfuerzos técnicos quedan concentrados en el endurecimiento criptográfico de los artefactos de distribución, optimización de empaquetado (R8/ProGuard), verificación de firmas digitales y pruebas exhaustivas en hardware móvil real.

### 1.2 Estado de la Ficha de Integración Auditada
Todos los defectos identificados en auditorías previas (Sesión 13 y anteriores) han sido formalmente auditados y clasificados:

| N.º | Defecto / Requerimiento Auditado | Gravedad | Estado Final de Cierre | Justificación Técnica de Cierre |
| :---: | :---| :---: | :---: | :---|
| **1** | Reintentos con Backoff y Clasificación HTTP (4xx vs 5xx) | **Alta** | **CERRADO** | Implementado en `ProductoRepositorioOffline.kt` con clasificación tipada `ResultadoEnvio.Transitorio` y `Permanente`. |
| **2** | Trazabilidad SQLite (`intentos` y `ultimoError`) | **Alta** | **CERRADO** | Esquema DDL ampliado en `Producto.sq` y probado con JDBC en memoria. |
| **3** | Panel Reactivo de Sincronización en UI | **Media** | **CERRADO** | `SincronizacionUiState` integrado en `ProductoScreen.kt` con banners en `secondaryContainer` y `errorContainer`. |
| **4** | Desacoplamiento de Telemetría (Firebase) | **Media** | **CERRADO** | Patrón Puertos y Adaptadores aplicado mediante `Telemetria` (dominio) y `TelemetriaLogger` (infraestructura). |
| **5** | Ciclo de Vida de Permisos de 3 Estados | **Alta** | **CERRADO** | `GestorPermisos` con `Concedido`, `Denegado` y `DenegadoParaSiempre` con ruta de rescate hacia Ajustes. |
| **6** | Conectividad REST en Redes Locales Físicas | **Alta** | **CERRADO** | `NetworkConfig.baseUrl` dinámico y `network-security-config.xml` para tráfico HTTP en claro. |
| **7** | Conservación de DTOs ante Minificación R8 | **Alta** | **CERRADO** | Reglas ProGuard configuradas en `proguard-rules.pro` para `kotlinx.serialization` y Koin. |
| **8** | Firma Desacoplada y Protección de Secretos | **Crítica** | **CERRADO** | Implementado mediante `keystore.properties` e ignición estricta en `.gitignore`. |
| **9** | Sincronización en segundo plano silenciosa (WorkManager) | **Media** | **TRABAJO FUTURO** | Planificado para versión 1.1 en despliegue productivo; la sincronización manual y reactiva cubre el 100% del alcance. |
| **10** | Soporte de Lectores Industriales EMDK (Zebra) | **Baja** | **DESCARTADO** | Descartado formalmente: el alcance académico está delimitado a smartphones comerciales estándar con cámara CameraX. |

---

## 2. SEGURIDAD Y CRIPTOGRAFÍA DE PUBLICACIÓN

### 2.1 Generación del Almacén de Claves Android (KeyStore RSA 2048 bits)
Siguiendo la restricción **RES-SIG-01**, se generó un par de claves criptográficas utilizando la herramienta estándar `keytool` de Java JDK, garantizando una validez de 10,000 días (~27 años):

```bash
keytool -genkeypair -v \
  -keystore claves/pharmamobil-release.jks \
  -storetype JKS \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias pharmamobil \
  -dname "CN=Nick Daniel Pachamora Villanueva, OU=Facultad de Ingenieria, O=Universidad Peruana Union, L=Lima, ST=Lima, C=PE"
```

#### Parámetros Criptográficos Verificados:
* **Algoritmo de Clave:** RSA (Rivest-Shamir-Adleman) asimétrico.
* **Longitud de Clave:** 2048 bits (estándar NIST y Google Play para producción).
* **Tipo de Almacén:** Java KeyStore (`JKS`).
* **Periodo de Validez:** 10,000 días (válido hasta marzo de 2054).

---

### 2.2 Protocolo de Protección de Secretos e Ignición en Git (RNF-01, RES-SIG-02)
Para evitar la filtración de claves privadas en el repositorio Git, se aplicó un desacoplamiento estricto en tres capas:

1. **Ignición en `.gitignore`:**
   Se incorporaron las reglas obligatorias al final del archivo `.gitignore`:
   ```gitignore
   # Exclusión de claves y contraseñas de firma (Sesión 14)
   keystore.properties
   *.jks
   *.keystore
   ```

2. **Plantilla de Entorno (`keystore.properties.example`):**
   Se versiona únicamente la estructura del archivo sin credenciales sensibles:
   ```properties
   storeFile=claves/pharmamobil-release.jks
   storePassword=CAMBIAR_POR_PASSWORD_DEL_KEYSTORE
   keyAlias=pharmamobil
   keyPassword=CAMBIAR_POR_PASSWORD_DE_LA_CLAVE
   ```

3. **Carga Condicional y Desacoplada en Gradle (`androidApp/build.gradle.kts`):**
   La configuración lee `keystore.properties` si existe; si no existe (por ejemplo, en un clon nuevo o en un servidor CI limpio), el build de depuración continúa funcionando normalmente sin lanzar errores de compilación (**Checklist Punto 4**):
   ```kotlin
   val keystorePropertiesFile = rootProject.file("keystore.properties")
   val keystoreProperties = Properties().apply {
       if (keystorePropertiesFile.exists()) {
           keystorePropertiesFile.inputStream().use { load(it) }
       }
   }

   android {
       signingConfigs {
           create("release") {
               if (keystorePropertiesFile.exists()) {
                   val storeFilePath = keystoreProperties.getProperty("storeFile")
                   if (!storeFilePath.isNullOrBlank()) {
                       storeFile = rootProject.file(storeFilePath)
                       storePassword = keystoreProperties.getProperty("storePassword")
                       keyAlias = keystoreProperties.getProperty("keyAlias")
                       keyPassword = keystoreProperties.getProperty("keyPassword")
                   }
               }
           }
       }
       buildTypes {
           release {
               isMinifyEnabled = true
               proguardFiles(
                   getDefaultProguardFile("proguard-android-optimize.txt"),
                   "proguard-rules.pro"
               )
               signingConfig = if (keystorePropertiesFile.exists()) {
                   signingConfigs.getByName("release")
               } else {
                   signingConfigs.getByName("debug")
               }
           }
       }
   }
   ```

---

### 2.3 Firma y Aprovisionamiento en iOS (RES-SIG-03)
En la plataforma iOS, el empaquetado se gestiona a través del ecosistema de seguridad de Apple:
* **App ID:** `pe.edu.upeu.pharmamobil`
* **Perfil de Aprovisionamiento:** *Apple Development / iOS Team Provisioning Profile*.
* **Declaración de Limitación de Cuenta Gratuita (RES-SIG-03):** Se documenta formalmente que bajo el programa *Free Personal Team*, los perfiles de desarrollo tienen una caducidad fija de 7 días y Xcode inhabilita la exportación directa de archivos `.ipa` firmados Ad-Hoc. El entregable satisface la especificación mediante el proyecto archivado (`.xcarchive`), el reporte de compilación y las evidencias de ejecución en hardware real.

---

## 3. EMPAQUETAMIENTO MULTIPLATAFORMA Y VERIFICACIÓN DE HUELLAS

### 3.1 Generación de Android App Bundle (`.aab`) — Producción (RF-BLD-01, CA-02)
El empaquetado optimizado para distribución en Google Play se genera ejecutando:
```bash
./gradlew :androidApp:bundleRelease
```
* **Ruta de Salida:** `androidApp/build/outputs/bundle/release/androidApp-release.aab`
* **Nomenclatura Oficial de Entrega:** `Equipo1_ProyectoFinal_Android.aab`
* **Características:** Incluye recursos compilados optimizados (split de densidad, ABI y lenguaje) firmados criptográficamente.

---

### 3.2 Generación de APK de Revisión (`.apk`) — Pruebas Físicas (RF-BLD-02)
Para la instalación directa en dispositivos físicos mediante sideloading o ADB, se genera el binario ejecutable:
```bash
./gradlew :androidApp:assembleRelease
```
* **Ruta de Salida:** `androidApp/build/outputs/apk/release/androidApp-release.apk`
* **Tamaño Optimizado:** ~14.8 MB (reducido gracias a R8 y eliminación de recursos no usados).

---

### 3.3 Verificación de Huella Digital con `apksigner` (RF-BLD-03, CA-03)
Se auditó la integridad del APK de Release utilizando la herramienta oficial del Android SDK Build-Tools:

```bash
apksigner verify --verbose --print-certs androidApp/build/outputs/apk/release/androidApp-release.apk
```

#### Salida de la Auditoría Criptográfica:
```text
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): true
Verified using v4 scheme (APK Signature Scheme v4): false
Number of signers: 1

Signer #1 certificate DN: CN=Nick Daniel Pachamora Villanueva, OU=Facultad de Ingenieria, O=Universidad Peruana Union, L=Lima, ST=Lima, C=PE
Signer #1 certificate SHA-256 digest: 8F:2A:4D:19:7E:B3:62:01:C9:F4:3B:AA:5D:E7:09:88:14:6B:2E:F0:71:A3:85:D9:3C:99:E1:55:7B:64:90:3A
Signer #1 certificate SHA-1 digest: 4A:9C:3E:17:F0:8B:D2:C5:11:89:E4:3A:90:7E:D1:2B:6F:A8:11:C3
Signer #1 key algorithm: RSA 2048 bits
Signer #1 valid from: Wed Nov 11 14:00:00 PET 2026 until: Sun Mar 28 14:00:00 PET 2054
```
**Resultado:** Cumplimiento total de **CA-03**. El APK cuenta con esquemas de firma V1, V2 y V3 validados y vinculados al titular y universidad correspondientes.

---

### 3.4 Empaquetamiento y Archivado en iOS (RF-BLD-04)
En Xcode:
1. Menú superior: `Product -> Clean Build Folder` seguido de `Product -> Destination -> Any iOS Device (arm64)`.
2. Selección de acción: `Product -> Archive`.
3. El organizador de Xcode (*Xcode Organizer*) valida la firma del equipo y genera el archivo `PharmaMobil.xcarchive`.
4. **Nomenclatura Oficial:** `Equipo1_ProyectoFinal_iOS.ipa` (o `.xcarchive` certificado).

---

## 4. CONECTIVIDAD EN HARDWARE REAL Y RED LOCAL

### 4.1 Resolución de Direcciones IP para Hardware Físico (RF-CON-01)
En un emulador Android, la dirección `http://10.0.2.2:8080/` mapea hacia el `localhost` del host de desarrollo. Sin embargo, en smartphones físicos conectados vía Wi-Fi, `10.0.2.2` no es ruteable.

Se implementó el objeto dinámico `NetworkConfig` en `HttpClient.kt` que permite modificar la IP base sin alterar la lógica de negocio:
```kotlin
object NetworkConfig {
    // URL por defecto para emulador; en hardware real se asigna la IP de la máquina de desarrollo
    var baseUrl: String = "http://192.168.1.100:8080/"
}
```

### 4.2 Permiso de Tráfico en Claro Local (`network-security-config.xml`) (RF-CON-02)
A partir de Android 9 (API 28), el sistema operativo bloquea por defecto todo tráfico HTTP sin TLS (`CleartextTraffic`). Para permitir pruebas en red local con el servidor de desarrollo sin obligar a instalar certificados autofirmados en los teléfonos, se creó `androidApp/src/main/res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
        <!-- Habilitación explícita para subredes privadas locales -->
        <domain includeSubdomains="true">192.168.0.0</domain>
        <domain includeSubdomains="true">192.168.1.0</domain>
        <domain includeSubdomains="true">192.168.18.0</domain>
    </domain-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```
Vinculado en `AndroidManifest.xml` mediante `android:networkSecurityConfig="@xml/network_security_config"` y `android:usesCleartextTraffic="true"`.

---

## 5. AUDITORÍA DE MINIFICACIÓN R8 / PROGUARD

Para garantizar que el empaquetado Release (`isMinifyEnabled = true`) no elimine clases requeridas por serialización o reflexión (**RNF-02**, **RES-MIN-01**, **CA-05**), se configuraron y validaron las siguientes reglas en `androidApp/proguard-rules.pro`:

```proguard
# 1. Conservar DTOs y modelos de serialización (kotlinx.serialization)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep,allowobfuscation,allowshrinking class * kotlinx.serialization.Serializer
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class pe.edu.upeu.pharmamobil.data.remote.dto.** { *; }

# 2. Conservar módulos de Inyección de Dependencias con Koin
-keep class pe.edu.upeu.pharmamobil.di.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# 3. Conservar modelos del dominio y base de datos local SQLDelight
-keep class pe.edu.upeu.pharmamobil.domain.model.** { *; }
-keep class pe.edu.upeu.pharmamobil.db.** { *; }
-keep class app.cash.sqldelight.** { *; }

# 4. Conservar bibliotecas de red Ktor y Coroutines
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# 5. Soporte para Compose Multiplatform
-keep class androidx.compose.** { *; }
```

### 5.1 Resultado de la Verificación en Ejecución Release:
* **Serialización Ktor:** Las peticiones de Login (`LoginResponseDto`) y Productos (`ProductoDto`) se deserializan correctamente sin lanzar `SerializationException`.
* **Inyección Koin:** `ProductoViewModel` y `LoginViewModel` se resuelven en tiempo de ejecución sin arrojar `ClassNotFoundException` ni `NoBeanDefFoundException`.

---

## 6. VALIDACIÓN EN DISPOSITIVOS REALES (MATRIZ DE PRUEBAS)

En cumplimiento de **CA-04**, la versión Release firmada fue instalada y auditada de forma autónoma (sin IDE ni debugger conectado) sobre una matriz de 3 dispositivos físicos:

### 6.1 Matriz de Dispositivos de Hardware Empleada
1. **Dispositivo 1 (Android A):** Samsung Galaxy A54 5G — Android 14 (One UI 6.1), Pantalla AMOLED 6.4", CPU Exynos 1380.
2. **Dispositivo 2 (Android B):** Xiaomi Redmi Note 11 — Android 13 (MIUI 14), Pantalla AMOLED 6.43", CPU Snapdragon 680.
3. **Dispositivo 3 (iOS):** Apple iPhone 13 — iOS 17.5.1, Pantalla Super Retina XDR 6.1", Chip A15 Bionic.

---

### 6.2 Recorrido de los 8 Hitos en Hardware Real (RF-VAL-01)

| Hito | Acción Ejecutada en Hardware Real | Resultado Obtenido en el Dispositivo | Estado |
| :---: | :---| :---| :---: |
| **1** | **Instalación limpia:** Sideloading del APK Release firmado. Verificación del launcher. | Ícono farmacéutico nítido, nombre "PharmaMobil" visible sin truncamiento. Apertura fluida sin crashes. | **APROBADO** |
| **2** | **Autenticación y diferenciación de error:** Ingreso con clave incorrecta vs. servidor apagado. | Clave errónea: "Credenciales inválidas" (HTTP 401). Servidor apagado: "Sin conexión con PharmaSoft". | **APROBADO** |
| **3** | **CRUD y validación tipada:** Registro de fármaco con precio negativo `-5.0`. | El servidor responde HTTP 400; el campo "Precio" se resalta en rojo con el texto de advertencia inline. | **APROBADO** |
| **4** | **Restricción de rol (HTTP 403):** Intento de creación con usuario de solo consulta. | Servidor retorna 403 Forbidden; la pantalla notifica falta de privilegios y oculta controles de edición sin cerrar sesión. | **APROBADO** |
| **5** | **Modo Avión (Offline-First):** Activación de modo avión desde la barra de estado y creación de producto. | Se guarda con `id = -1`, `pendiente = 1`. Aparece banner ámbar: *"Sin conexión: 1 cambios por enviar"*. | **APROBADO** |
| **6** | **Reconexión y Sync bidireccional:** Desactivación de modo avión y pulsación de sincronizar. | El producto `-1` se sube al backend, recibe ID real `#208`, se actualiza localmente y desaparece el banner. | **APROBADO** |
| **7** | **Evaluación de Permisos:** Denegación permanente de cámara y pulsación de botón "Ir a Ajustes". | La app abre directamente la pantalla de información de la aplicación en los Ajustes del sistema operativo. | **APROBADO** |
| **8** | **Logout y Almacén Seguro:** Cierre de sesión en menú lateral. | Destrucción inmediata del JWT en `EncryptedSharedPreferences`. El catálogo local en SQLite sigue intacto. | **APROBADO** |

---

### 6.3 Registro Técnico de Defectos en Hardware Real (Formato Oficial SRS Cap. 8)

A continuación se documentan los incidentes detectados durante las pruebas en hardware físico:

```text
+-----------------------------------------------------------------------------------+
| ESTRUCTURA DE REGISTRO DE DEFECTO EN DISPOSITIVOS REALES                         |
+----------------------+------------------------------------------------------------+
| Identificador        | D-01                                                       |
| Dispositivo y OS     | Samsung Galaxy A54 5G (Android 14 / One UI 6.1)            |
| Pasos de Reproducción| 1. Abrir Catálogo con navegación gestual activada.         |
|                      | 2. Desplazar la lista hasta el último registro.            |
| Resultado Esperado   | La última fila de producto debe quedar sobre la barra.     |
| Resultado Obtenido   | La barra de navegación por gestos tapaba el botón flotante.|
| Severidad            | MEDIA                                                      |
| Estado               | CORREGIDO (ajuste de padding insets y enableEdgeToEdge)    |
+----------------------+------------------------------------------------------------+
```

```text
+-----------------------------------------------------------------------------------+
| ESTRUCTURA DE REGISTRO DE DEFECTO EN DISPOSITIVOS REALES                         |
+----------------------+------------------------------------------------------------+
| Identificador        | D-02                                                       |
| Dispositivo y OS     | Xiaomi Redmi Note 11 (Android 13 / MIUI 14)                |
| Pasos de Reproducción| 1. Conectar a Wi-Fi local e ingresar IP http://192.168.1.x|
|                      | 2. Presionar "Iniciar Sesión" contra el backend local.     |
| Resultado Esperado   | Conexión HTTP exitosa contra el servicio PharmaSoft.       |
| Resultado Obtenido   | Crash con excepción java.io.IOException Cleartext not perm.|
| Severidad            | ALTA (bloqueaba login en hardware real)                    |
| Estado               | CORREGIDO (incorporación de network-security-config.xml)   |
+----------------------+------------------------------------------------------------+
```

```text
+-----------------------------------------------------------------------------------+
| ESTRUCTURA DE REGISTRO DE DEFECTO EN DISPOSITIVOS REALES                         |
+----------------------+------------------------------------------------------------+
| Identificador        | D-03                                                       |
| Dispositivo y OS     | Apple iPhone 13 (iOS 17.5.1)                               |
| Pasos de Reproducción| 1. Desconectar el iPhone de la Mac tras 8 días de prueba.  |
|                      | 2. Intentar abrir la app instalada en el dispositivo.      |
| Resultado Esperado   | Ejecución continua del binario en el dispositivo físico.   |
| Resultado Obtenido   | "PharmaMobil ya no está disponible" (perfil expirado 7d).  |
| Severidad            | BAJA                                                       |
| Estado               | LIMITACIÓN DECLARADA (Restricción de Free Personal Team)   |
+----------------------+------------------------------------------------------------+
```

---

## 7. MATRIZ DE VERSIONAMIENTO Y NOMENCLATURA DE ENTREGABLES

En conformidad estricta con el **Capítulo 9 del SRS**, los artefactos finales compilados están identificados y rotulados de la siguiente manera:

| Parámetro | Especificación del Artefacto | Valor Oficial de la Entrega |
| :--- | :--- | :--- |
| **Código de Versión** | `versionCode` (Entero incremental de compilación) | `1` |
| **Nombre de Versión** | `versionName` (Versión visible al usuario) | `1.0.0` |
| **ID de Aplicación** | `applicationId` (Paquete único en el sistema) | `pe.edu.upeu.pharmamobil` |
| **Etiqueta de Git** | Git Tag en el commit final compilado | `v1.0-final` |
| **Nomenclatura AAB** | Nombre de archivo del paquete de producción Android | `Equipo1_ProyectoFinal_Android.aab` |
| **Nomenclatura IPA** | Nombre de archivo de exportación iOS | `Equipo1_ProyectoFinal_iOS.ipa` |

---

## 8. CHECKLIST FINAL PREVIO A LA SUSTENTACIÓN (14 PUNTOS DE CONTROL)

Verificación exhaustiva de los 14 puntos de control previa a la sustentación del 16 de noviembre:

* [x] **1. Alcance congelado por escrito y Ficha de Integración cerrada:** Documentado en Sección 1 del informe.
* [x] **2. Almacén de claves `.jks` respaldado fuera del equipo de un solo integrante:** Custodiado en almacenamiento institucional seguro.
* [x] **3. `keystore.properties` y archivos `.jks` incluidos en el `.gitignore`:** Verificado en línea 21 de `.gitignore`.
* [x] **4. El proyecto compila en modo Depuración sin requerir `keystore.properties`:** Validado con fallback a firma debug en Gradle.
* [x] **5. Tarea `bundleRelease` ejecutada exitosamente generando el archivo `.aab`:** Generado en `androidApp/build/outputs/bundle/release/`.
* [x] **6. Tarea `assembleRelease` instalada y recorrida en dispositivo real:** APK probado autónomamente en hardware real.
* [x] **7. Verificación de huella digital ejecutada con `apksigner`:** Verificado con huella SHA-256 e información de titular completa.
* [x] **8. Minificación probada; serialización Ktor e inyección Koin funcionando:** Reglas en `proguard-rules.pro` validadas sin errores.
* [x] **9. Si la minificación fue desactivada, la decisión cuenta con motivo en el informe:** Minificación activa (`isMinifyEnabled = true`) con reglas verificadas.
* [x] **10. Proyecto de iOS archivado en Xcode o límite de cuenta gratuita documentado:** Límite de *Free Personal Team* declarado en Sección 2.3.
* [x] **11. Pruebas ejecutadas en al menos 2 dispositivos móviles físicos distintos:** Probado en Samsung Galaxy A54, Xiaomi Redmi Note 11 e iPhone 13.
* [x] **12. Defectos detectados registrados con ID, pasos, severidad y estado:** D-01, D-02 y D-03 documentados en Sección 6.3.
* [x] **13. Validados los campos `versionCode`, `versionName` y `applicationId`:** Alineados a `1`, `1.0.0` y `pe.edu.upeu.pharmamobil`.
* [x] **14. Etiqueta `v1.0-final` aplicada en Git sobre el commit compilado:** Etiquetado inmutable en el repositorio remoto.

---

## 9. CONCLUSIÓN Y DICTAMEN DE ENTREGA

El proyecto **PharmaMobil** se encuentra en estado **APTO PARA SUSTENTACIÓN Y ENTREGA FINAL**, cumpliendo con la totalidad de los criterios de calidad, arquitectura desacoplada, seguridad en el manejo de firmas y validación en hardware físico real.

