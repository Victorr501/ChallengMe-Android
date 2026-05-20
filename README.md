# ChallengMe! — Android

Red social de retos diarios generados por IA. Los usuarios reciben un reto cada día, suben evidencia fotográfica, acumulan puntos y compiten en un ranking global.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09.00-4285F4?logo=jetpackcompose&logoColor=white)
![Android API](https://img.shields.io/badge/Android-API%2024%2B-3DDC84?logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-AGP%208.12.3-3DDC84?logo=androidstudio&logoColor=white)
![Licencia](https://img.shields.io/badge/Licencia-Uso%20Restringido-red)

---

## Índice

1. [Descripción](#descripción)
2. [Arquitectura y estructura de carpetas](#arquitectura-y-estructura-de-carpetas)
3. [Requisitos previos](#requisitos-previos)
4. [Clonar y abrir en Android Studio](#clonar-y-abrir-en-android-studio)
5. [Configuración](#configuración)
6. [Ejecutar la aplicación](#ejecutar-la-aplicación)
7. [Generar el APK](#generar-el-apk)
8. [Licencia](#licencia)

---

## Descripción

ChallengMe! es una aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose**. Implementa el cliente móvil de la plataforma ChallengMe!, que se conecta a una API REST (ASP.NET Core sobre Azure) para gestionar la autenticación de usuarios y el flujo de retos diarios.

**Estado actual:** MVP con autenticación completa. Las pantallas de reto, historial y ranking son placeholders pendientes de implementación.

**Funcionalidades implementadas:**

- Registro, inicio de sesión y recuperación de contraseña
- Autenticación con JWT almacenado de forma segura (AES-256-GCM)
- Inyección automática del token en cada petición HTTP
- Cierre de sesión automático ante respuestas 401
- Navegación entre flujo de autenticación y aplicación principal
- Estructura base de cuatro pestañas: Dashboard, Reto, Historial y Ranking

---

## Arquitectura y estructura de carpetas

El proyecto sigue **MVVM + Clean Architecture** dividido en tres capas dentro del paquete `com.example.challengme`:

```
app/src/main/
├── java/com/example/challengme/
│   │
│   ├── ChallengMeAPP.kt                    ← Application class (inicializa AuthManager)
│   ├── MainActivity.kt                      ← Punto de entrada; monta MainNavGraph
│   │
│   ├── data/                                ── CAPA DE DATOS ──
│   │   ├── local/
│   │   │   └── AuthManager.kt              ← Tokens JWT con EncryptedSharedPreferences
│   │   ├── remote/
│   │   │   ├── api/
│   │   │   │   ├── ApiClient.kt            ← Cliente OkHttp con inyección automática de JWT
│   │   │   │   └── ApiError.kt             ← Clase sellada de errores de API
│   │   │   └── dto/
│   │   │       ├── API/constant/
│   │   │       │   ├── ApiConfig.kt        ← URL base, timeouts y configuración global
│   │   │       │   └── EndpointConstant.kt ← Rutas de la API (/auth/login-email, etc.)
│   │   │       ├── auth/
│   │   │       │   ├── request/            ← DTOs de petición: LoginRequest,
│   │   │       │   │                           RegisterRequest, RecuperarPasswordRequest
│   │   │       │   └── shipment/           ← DTOs de respuesta: AuthShipment (token JWT)
│   │   │       └── routes/constant/
│   │   │           └── AuthRoutes.kt       ← Constantes de rutas de navegación auth
│   │   └── services/
│   │       └── AuthService.kt              ← Orquestación: loginEmail, register,
│   │                                           recuperarPassword, logout
│   │
│   ├── res/                                 ── CAPA DE PRESENTACIÓN (recursos Compose) ──
│   │   ├── layout/
│   │   │   └── MainLayout.kt              ← Shell principal: TopBar + BottomNavigation
│   │   │                                      (Dashboard · Reto · Historial · Ranking)
│   │   ├── navigation/
│   │   │   ├── AuthNavGraph.kt            ← Grafo de navegación del flujo auth
│   │   │   └── MainNavGraph.kt            ← Grafo raíz: decide entre auth y app
│   │   └── values/
│   │       ├── Color.kt                   ← Paleta: Primary #3B82F6, Dark #1D4ED8,
│   │       │                                  CyanAccent #06B6D4, colores de estado
│   │       ├── Theme.kt                   ← Tema Material3 + tokens de diseño:
│   │       │                                  ChallengMeSpacing (4–48dp),
│   │       │                                  ChallengMeShapes (6–9999dp)
│   │       └── Type.kt                    ← Tipografía: Inter (cuerpo) + Syne (display)
│   │                                          vía Google Fonts
│   │
│   └── ui/                                  ── PANTALLAS ──
│       ├── auth/
│       │   ├── WelcomeScreen.kt           ← Pantalla de bienvenida con acceso a login/registro
│       │   ├── LoginScreen.kt             ← Formulario email + contraseña con gestión de errores
│       │   ├── RegisterScreen.kt          ← Formulario con validación de contraseñas
│       │   └── RecuperarPasswordScreen.kt ← Recuperación por email (4 estados de UI)
│       ├── dashboard/
│       │   └── DashboardScreen.kt         ← Placeholder
│       ├── reto/
│       │   └── RetoScreen.kt              ← Placeholder
│       ├── historial/
│       │   └── HistorialScreen.kt         ← Placeholder
│       └── ranking/
│           └── RankingScreen.kt           ← Placeholder
│
└── res/                                     ── RECURSOS ANDROID ──
    ├── drawable/      ← Vectores del launcher (fondo y foreground)
    ├── mipmap-*/      ← Icono de la app en densidades hdpi → xxxhdpi (.webp)
    ├── values/        ← strings.xml (app_name: "ChallengMe"), font_certs.xml
    └── xml/           ← data_extraction_rules.xml, backup_rules.xml
```

### Dependencias principales

| Librería | Versión |
|---|---|
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 8.12.3 |
| Jetpack Compose BOM | 2024.09.00 |
| Navigation Compose | 2.7.7 |
| Material3 | (vía BOM) |
| OkHttp | 4.12.0 |
| Gson | 2.10.1 |
| Security Crypto | 1.1.0-alpha06 |
| Kotlinx Coroutines Android | 1.7.3 |
| Core KTX | 1.10.1 |
| Lifecycle Runtime KTX | 2.6.1 |
| Activity Compose | 1.8.0 |

### Endpoints de la API registrados

```
POST  /auth/login-email
POST  /auth/registro
POST  /auth/refresh
POST  /auth/logout
POST  /auth/recuperar-password
GET   /leaderboard
POST  /challenges/{id}/evidence
```

---

## Requisitos previos

| Requisito | Versión mínima |
|---|---|
| Android Studio | Compatible con AGP 8.12.3 (se recomienda la versión estable más reciente) |
| JDK | 11 |
| Android SDK | API 24 (Android 7.0 Nougat) |
| Gradle Wrapper | 8.13 (descargado automáticamente) |
| Dispositivo / Emulador | Android 7.0+ (API 24+) |

> Android Studio gestiona automáticamente el JDK incluido y descarga el Gradle Wrapper al abrir el proyecto. No es necesario instalar Gradle manualmente.

---

## Clonar y abrir en Android Studio

### 1. Clonar el repositorio

```bash
git clone https://github.com/<usuario>/ChallengMe-Android.git
```

### 2. Abrir en Android Studio

1. Abre Android Studio.
2. Selecciona **File → Open…**
3. Navega hasta la carpeta `ChallengMe-Android` y pulsa **OK**.
4. Espera a que el proyecto sincronice Gradle por primera vez (descarga dependencias automáticamente).

### 3. Verificar la sincronización

Comprueba que la barra inferior no muestra errores de Gradle. Si aparece el aviso **"Gradle files have changed"**, pulsa **Sync Now**.

---

## Configuración

### URL de la API

La URL base de la API está definida directamente en el código fuente. No requiere ningún archivo `.env` ni variable de entorno:

```
Archivo: app/src/main/java/com/example/challengme/data/remote/dto/API/constant/ApiConfig.kt
```

```kotlin
BASE_URL = "https://api-challengeme-ddcpawg6ama0cncn.spaincentral-01.azurewebsites.net/api"
TIMEOUT_SECONDS = 30L
```

Si necesitas apuntar a un entorno distinto (local, staging), modifica `BASE_URL` en ese archivo.

### SDK de Android (`local.properties`)

Android Studio genera `local.properties` automáticamente al abrir el proyecto con la ruta a tu SDK local. **Este archivo está en `.gitignore` y no debe versionarse.**

### Permisos declarados

La aplicación únicamente requiere el permiso de Internet, declarado en `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Ejecutar la aplicación

### En emulador

1. En Android Studio, abre el **Device Manager** (`View → Tool Windows → Device Manager`).
2. Crea un AVD con API 24 o superior si no tienes ninguno.
3. Inicia el emulador pulsando el botón ▶.
4. Selecciona el emulador en la barra superior de Android Studio y pulsa **Run 'app'** (Shift+F10).

### En dispositivo físico

1. En tu dispositivo Android, activa **Opciones de desarrollador** y habilita **Depuración USB**.
2. Conecta el dispositivo al equipo mediante USB.
3. Acepta el aviso de autorización de depuración que aparece en el dispositivo.
4. Selecciona el dispositivo en la barra superior de Android Studio y pulsa **Run 'app'** (Shift+F10).

### Desde línea de comandos

```bash
# Windows (PowerShell)
.\gradlew.bat installDebug

# macOS / Linux
./gradlew installDebug
```

> El dispositivo o emulador debe estar conectado y reconocido por ADB antes de ejecutar este comando.

---

## Generar el APK

### APK de depuración (distribución directa, sin firmar)

```bash
# Windows (PowerShell)
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

El archivo resultante se encuentra en:

```
app/build/outputs/apk/debug/app-debug.apk
```

### APK de release (firmado, para distribución)

1. En Android Studio, selecciona **Build → Generate Signed Bundle / APK…**
2. Elige **APK**.
3. Selecciona o crea un **Key Store** (archivo `.jks`) con su alias y contraseñas.
4. Elige la variante **release**.
5. Pulsa **Create**.

El APK firmado se genera en:

```
app/release/app-release.apk
```

> Para publicar en Google Play se requiere un **Android App Bundle** (`.aab`). En el mismo asistente selecciona **Android App Bundle** en el paso 2.

---

## Licencia

```
Copyright (c) 2026 Víctor Rubín

LICENCIA DE USO RESTRINGIDO — CHALLENGEME!

El código fuente de este repositorio se publica públicamente con fines
de transparencia y aprendizaje únicamente.

QUEDA EXPRESAMENTE PROHIBIDO, sin autorización escrita previa del autor:

1. Usar este código, total o parcialmente, para ofrecer cualquier
   servicio comercial o de acceso público.
2. Distribuir, sublicenciar o vender versiones originales o modificadas
   de este código con fines comerciales.
3. Desplegar este código en entornos de producción, ya sea de forma
   directa o como parte de otro producto o servicio.
4. Usar el nombre "ChallengMe!", su logotipo o cualquier elemento de
   identidad visual asociado sin autorización expresa del autor.
5. Realizar contribuciones al repositorio. Este repositorio no acepta
   contribuciones externas.
6. Crear forks del repositorio con fines distintos al estudio personal y local.

USO PERMITIDO:
- Visualización del código con fines educativos o de aprendizaje.
- Uso en entorno local para fines de estudio personal y no comercial.

Para solicitar una licencia comercial: vrubinr501@gmail.com

Ley aplicable: legislación española (Real Decreto Legislativo 1/1996)
y normativa europea aplicable. Jurisdicción: tribunales de Madrid, España.
```

Ver el archivo [LICENSE](LICENSE) para el texto completo en inglés y español.
