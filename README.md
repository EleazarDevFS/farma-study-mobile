# FarmaStudy

App del área de la salud para estudiantes de farmacología: repaso por clasificación, flashcards random y quiz de 38 preguntas. Migrada de AppFarmacología (Java Swing) a Android Kotlin + Jetpack Compose. **Migración completada.**

## Stack y requisitos

Kotlin 2.2 · Jetpack Compose + Material 3 · Navigation Compose · Room 2.7 · DataStore · BCrypt · AGP 9.2.1.

Requisitos: Android Studio, JDK 17+, SDK 36. `local.properties` (sdk.dir) no se versiona.

## Build y tests

```bash
./gradlew assembleDebug      # compilar debug
./gradlew testDebugUnitTest  # tests unitarios
./gradlew assembleRelease    # APK release (unsigned)
./gradlew installDebug       # instalar en dispositivo
```

La BD se crea y rellena desde `assets/*.json` en el primer arranque.

## Estructura

```
app/src/main/java/com/example/farmastudy/
├── MainActivity.kt · FarmaApp.kt
├── data/
│   ├── datasource/SeedData.kt
│   ├── domain/            # models, Validators
│   ├── local/             # FarmaDatabase, SessionStore, dao/, entity/
│   └── repository/        # UserRepository, MedicationRepository
└── ui/
    ├── AuthViewModel · QuizViewModel · StudyViewModel
    ├── components/        # QuestionOptions
    ├── navigation/        # Routes.kt, NavGraph.kt
    ├── screens/           # login, register, home, classification,
    │                      # study, random, quiz, history
    └── theme/
```

Patrón MVVM: los ViewModels acceden a los DAOs vía `(application as FarmaApp).database`. Las pantallas reciben `state` + callbacks. Rutas centralizadas en `Routes.kt` y registradas en `NavGraph.kt`.

## Regla clave de Room

Al cambiar el esquema (entidades): subir la versión en `@Database(...)` y crear una `Migration` nueva en `FarmaDatabase.kt` registrada con `addMigrations(...)`. Sin migración, Room crashea al validar (lección del índice de `users`).

## Flujo de trabajo (GitFlow)

Ramas: `main` (estable) y `dev` (integración). Trabajo en `feature/<nombre>`, `fix/<nombre>`, `docs/<nombre>`, `refactor/<nombre>` desde `dev`.

```text
git checkout -b feature/<nombre> dev
# commits cortos y atómicos
git commit -m "tipo: asunto"        # ver tabla abajo
git checkout dev && git pull
git checkout feature/<nombre> && git rebase dev
git push -u origin feature/<nombre> # y abrir PR -> dev
```

Convención de commits (cortos, uno por cambio lógico):

`feat:` · `fix:` · `refact:` · `test:` · `docs:` · `build:` · `style:`

No subir secretos ni `local.properties`.

## Añadir una pantalla o feature

1. Ruta en `Routes.kt` (+ `{argumentos}` si aplica) y destino en `NavGraph.kt`.
2. Datos: entity + DAO en `data/local/` (con migración si hay cambio de esquema).
3. ViewModel en `ui/` exponiendo `StateFlow<UiState>`.
4. Pantalla en `ui/screens/<feature>/` con patrón `state + callbacks`.
5. Test unitario si hay lógica pura; verificar con `testDebugUnitTest assembleDebug`.

## Testing

Unit tests (JVM) en `app/src/test/` (validadores, deck random, historial). Instrumented en `app/src/androidTest/` (pendiente ampliar con tests de Room/Compose). Mantener la suite en verde antes de mergear.

## Mejoras sugeridas

Detalle de fármaco · categoría en estudio random · tests instrumentados · firma del APK · vista de progreso por parte del quiz · tema inspirado en la app original.
