# FarmaStudy

App móvil del área de la salud para estudiantes de farmacología: repasa fármacos por clasificación (uso terapéutico, mecanismo de acción, estructura química, sistema orgánico), refuerza con flashcards aleatorias y mide tu avance con un quiz de 38 preguntas.

El proyecto es el resultado de la migración de la app de escritorio **AppFarmacología** (Java Swing, NetBeans) a una app nativa **Android en Kotlin + Jetpack Compose**.

**La migración está completada.** Este README es la guía de desarrollo para contribuir y mejorar la app.

---

## 1. Estado del proyecto

Las 5 fases de migración están completadas:

| Fase | Alcance | Estado |
|---|---|---|
| 1. Infraestructura | Room, seed de datos, navegación | Completada |
| 2. Auth | Login + registro con BCrypt y DataStore | Completada |
| 3. Estudio | Clasificación + estudio random | Completada |
| 4. Quiz | Intro, preguntas y resultado | Completada |
| 5. Pulido | Historial de intentos, tema de marca, icono, APK release | Completada |

Funcionalidades actuales:

- Login y registro con validaciones y contraseña cifrada (BCrypt).
- Sesión persistida con DataStore.
- Estudio por clasificación con listas expandibles.
- Estudio random con deck mixto de flashcards y preguntas.
- Quiz de opción múltiple con selector por partes (1-5) o modo completo.
- Historial de intentos por usuario.

---

## 2. Stack tecnológico

| Tecnología | Detalle |
|---|---|
| Lenguaje | Kotlin 2.2.x |
| UI | Jetpack Compose + Material 3 (dark/light/dynamic color) |
| Navegación | Navigation Compose |
| Persistencia | Room 2.7.x (SQLite) |
| Preferencias de sesión | DataStore Preferences |
| Hash de contraseñas | BCrypt (at.favre.lib) |
| DI / Build | Gradle con version catalog (`gradle/libs.versions.toml`), AGP 9.2.1, KSP |

- minSdk 24, target/compile SDK 36.
- Room en versión 2 con migración 1→2 (ver sección 6).

---

## 3. Requisitos

- Android Studio (última versión estable).
- JDK 17+ (Android Studio incluye el JDK propio `jbr-21`).
- Android SDK 36 (configurado en `local.properties` con `sdk.dir`).
- Emulador o dispositivo físico con API 24 o superior.

Nota: `local.properties` no se versiona (contiene rutas locales del SDK).

---

## 4. Setup y primeros pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/EleazarDevFS/farma-study-mobile.git
cd farma-study-mobile

# 2. Abrir en Android Studio y dejar que sincronice Gradle
#    (o compilar desde terminal)
./gradlew assembleDebug
```

Comandos útiles:

| Comando | Descripción |
|---|---|
| `./gradlew assembleDebug` | Compila el APK de debug |
| `./gradlew testDebugUnitTest` | Ejecuta los tests unitarios |
| `./gradlew assembleRelease` | Genera el APK release (unsigned por defecto) |
| `./gradlew installDebug` | Instala en un dispositivo/emulador conectado |

En Windows usa `gradlew.bat` en lugar de `./gradlew`.

La BD se crea y rellena automáticamente en el primer arranque desde los assets
(`app/src/main/assets/medications.json` y `questions.json`).

---

## 5. Estructura del proyecto

```
app/src/main/java/com/example/farmastudy/
├── MainActivity.kt              # Single Activity: tema + Scaffold + NavGraph
├── FarmaApp.kt                  # Application: instancia Room + seed en primer arranque
├── data/
│   ├── datasource/SeedData.kt   # Podado de la BD desde los JSON de assets
│   ├── domain/
│   │   ├── model/               # Models de dominio (AuthResult, User)
│   │   └── usecase/Validators.kt # Validaciones de registro
│   ├── local/
│   │   ├── FarmaDatabase.kt     # Room database + migraciones
│   │   ├── SessionStore.kt      # DataStore (usuario logueado)
│   │   ├── dao/                 # UserDao, MedicationDao, QuestionDao, QuizAttemptDao
│   │   └── entity/              # UserEntity, MedicationEntity, QuestionEntity, QuizAttemptEntity
│   └── repository/              # UserRepository, MedicationRepository, ClassificationCategory
└── ui/
    ├── AuthViewModel.kt         # Login/registro/sesión
    ├── QuizViewModel.kt         # Estado del quiz + historial
    ├── StudyViewModel.kt        # Clasificación + estudio random
    ├── components/              # Widgets compartidos (QuestionOptions)
    ├── navigation/              # Routes.kt + NavGraph.kt
    ├── screens/
    │   ├── login/               # LoginScreen
    │   ├── register/            # RegisterScreen
    │   ├── home/                # HomeScreen (menú principal)
    │   ├── classification/      # ClassificationScreen
    │   ├── study/               # StudyByCategoryScreen
    │   ├── random/              # RandomStudyScreen
    │   ├── quiz/                # QuizIntroScreen, QuizQuestionScreen, QuizResultScreen
    │   └── history/             # QuizHistoryScreen
    └── theme/                   # Color.kt, Theme.kt, Type.kt

app/src/main/assets/             # medications.json (67 fármacos), questions.json (38 preguntas)
app/src/test/                    # Tests unitarios (JVM)
app/src/androidTest/             # Tests instrumentados
```

Patrón de arquitectura aplicado:

- **MVVM**: cada pantalla principal tiene su ViewModel (`AndroidViewModel`) que accede a
  los DAOs vía `(application as FarmaApp).database`. No hay framework de DI; los
  repositorios se instancian en los ViewModels.
- **Navegación por rutas**: las rutas se centralizan en `Routes.kt`; los destinos se
  registran en `NavGraph.kt` con `composable(...)`.
- **Estado en pantalla**: las pantallas reciben `state` + callbacks, siguiendo el patrón
  sin estado en los componentes.

---

## 6. Base de datos (Room)

La BD actual está en la **versión 2** con la tabla `quiz_attempts`.

Reglas imprescindibles:

1. Cualquier cambio de esquema (nueva entidad, columna, índice) debe **subir la versión**
   en `@Database(...)`.
2. Toda subida de versión debe incluir una **migración** en `FarmaDatabase.kt`
   (objeto `Migration`) registrada con `addMigrations(...)`. Si no se hace, Room valida el
   esquema y la app crashea al arrancar (lección aprendida: el índice único de `users`
   se agregó sin migración y rompió instalaciones existentes).
3. Las migraciones deben ser no destructivas cuando haya datos que conservar.

Las tablas actuales: `users`, `medications`, `questions`, `quiz_attempts`.

---

## 7. Flujo de trabajo con GitFlow

El repositorio usa **GitFlow**, con dos ramas principales:

| Rama | Uso |
|---|---|
| `main` | Producción / versiones estables. Solo se mergea desde `release` o `develop` ya validado. |
| `dev` | Integración continua. Todo el trabajo en curso se mergea aquí. |

### 7.1 Ramas de trabajo

- `feature/<nombre>` — nuevas funcionalidades (ej. `feature/quiz-timer`).
- `fix/<nombre>` — corrección de bugs (ej. `fix/login-crash`).
- `docs/<nombre>` — documentación.
- `refactor/<nombre>` — mejoras de código sin cambio de comportamiento.
- `release/<version>` — preparación de una versión (opcional para esta app).

### 7.2 Flujo de una feature

```text
1. Update de dev:      git checkout dev && git pull
2. Rama de trabajo:    git checkout -b feature/<nombre> dev
3. Commits cortos:     git add <archivos> && git commit -m "tipo: asunto"
4. Integrar dev:       git checkout dev && git pull && git checkout feature/<nombre>
                       git rebase dev
5. Subir la rama:      git push -u origin feature/<nombre>
6. Pull request:       feature/<nombre> -> dev, con descripción del cambio
7. Revisión y merge:   revisar diff, corregir comentarios (commits de fix),
                       squash o merge a dev (preferir merge para conservar historial)
```

### 7.3 Convenciones de commits

Mensajes cortos en español/inglés con prefijo de tipo (style: Conventional Commits):

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `refact:` | Refactor sin cambio de comportamiento |
| `test:` | Tests |
| `docs:` | Documentación |
| `build:` | Dependencias / configuración de build |
| `style:` | Tema visual / recursos |

Reglas:

- Commits **atómicos y cortos**: un commit = un cambio lógico. Evita commits con muchos
  archivos no relacionados.
- El asunto no debe superar ~50 caracteres; usa el cuerpo para el detalle necesario.
- No subir secretos ni `local.properties`.

---

## 8. Cómo añadir una nueva pantalla o feature

Sigue el patrón existente:

1. **Ruta**: añade la constante y el helper en `ui/navigation/Routes.kt`
   (usa `{argumentos}` si la pantalla recibe parámetros).
2. **Capa de datos** (si aplica): entity + DAO en `data/local/` y, si hace falta,
   repo en `data/repository/`. Recuerda las reglas de migración de la sección 6.
3. **ViewModel**: en `ui/`, extiende `AndroidViewModel`, obtén los DAOs con
   `(application as FarmaApp).database.<dao>()` y expón un `StateFlow<UiState>`.
4. **Pantalla**: crea `ui/screens/<feature>/<Nombre>Screen.kt` con el patrón
   `state + callbacks`; usa componentes reutilizables de `ui/components/`.
5. **Navegación**: registra el destino en `NavGraph.kt` (con `navArgument` si tiene
   argumentos) y enlaza los callbacks.
6. **Tests**: si hay lógica pura (helpers, cálculo de scores, decks), extrae funciones
   top-level y añade un test en `app/src/test/`.
7. **Verificación local**: `./gradlew testDebugUnitTest assembleDebug` antes del PR.

---

## 9. Testing

- **Unit tests (JVM)**: en `app/src/test/java/com/example/farmastudy/`. Cubren
  validadores, construcción del deck random y formateo del historial.
  Ejecutar con `./gradlew testDebugUnitTest`.
- **Instrumented**: en `app/src/androidTest/` (actualmente solo el placeholder de
  contexto). Plan útil: tests de Room (DAOs) y de navegación Compose.

Mantén la suite en verde antes de abrir o mergear un PR.

---

## 10. Próximas mejoras sugeridas

Idea board de partida para continuar iterando:

- Pantalla de detalle de fármaco (más contenido por medicamento).
- Selección de categoría en el estudio random.
- Feedback de respuestas correctas/incorrectas por pregunta en el historial.
- Tests instrumentados de Room y Compose.
- Firma del APK release (se necesita un keystore propio).
- Exportación de resultados o vista de progreso por parte del quiz.
- Tema personalizado con la identidad visual de la app original (JTattoo).

---

## 11. Referencia de la migración (resumen)

- **Origen**: AppFarmacología (Java Swing, NetBeans, JTattoo).
- **Destino**: FarmaStudy (Kotlin, Jetpack Compose, Material 3).
- Los datos se migraron de archivos `.txt` a `assets/*.json` y se cargan en Room al
  primer arranque.
- Las 38 preguntas del quiz (5 partes) se modelaron en una única tabla `questions`
  con `quiz_part`; la pantalla del quiz es reutilizable por índice y parte.
- El login original en texto plano se sustituyó por hash BCrypt.
