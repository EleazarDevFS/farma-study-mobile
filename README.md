# FarmaStudy (mobile)

Migración de la app de escritorio **AppFarmacología** (Java Swing, NetBeans) a una app móvil nativa en **Kotlin + Jetpack Compose** (Android Studio).

> App de apoyo para estudiantes de farmacología: repaso por clasificación, estudio random y quiz de opción múltiple sobre fármacos.

---

## 1. Repositorios

| App | Tecnología | Ruta / Repo |
|---|---|---|
| AppFarmacología (origen) | Java Swing, NetBeans, JTattoo | `farmaapp/Farmapp/Algoritmo/AppFarmacologia` |
| FarmaStudy (destino) | Kotlin, Jetpack Compose, Material 3 | [`EleazarDevFS/farma-study-mobile`](https://github.com/EleazarDevFS/farma-study-mobile) |

Branches del repo móvil: `main` (estable), `dev` (desarrollo).

---

## 2. Análisis de la app original

App de escritorio (750x547 px) con panel principal `FarmaFrame` que intercambia paneles (`mostrarPanel()`) simulando una navegación por "pantallas".

### 2.1 Funcionalidades

| Feature | Clases Java |
|---|---|
| Login con usuarios | `Sesion.java` |
| Registro de usuario | `Registro.java` |
| Menú principal | `Inicio.java` |
| Repaso por clasificación | `MetClasificacion.java` |
| Repaso random | `MetRandom.java` |
| Quiz general (38 preguntas) | `Quiz.java`, `QuizP1..QuizP5` |
| Resultados del quiz | `Respuestas.java`, `Resp1p1.java`, `Resp1p2.java` |
| Lógica / datos | `Logic.java`, `FarmaFrame.java` |

### 2.2 Flujo de la app

```
Sesion (login) ──> Registro (crear cuenta)
      │
      v
Inicio (menú principal)
   ├── Estudio por clasificación (Uso terapéutico, Mecanismo de acción, Estructura química, Sistema orgánico)
   ├── Estudio random (elige categoría y fármaco al azar)
   └── Quiz general (38 preguntas en 5 páginas) ──> Resultados (3 pantallas)
```

### 2.3 Datos en la app original

| Archivo | Contenido | Formato |
|---|---|---|
| `Fterapeutico.txt` | 38 fármacos por uso terapéutico | `nombre|descripción` |
| `Fmecanismo.txt` | 15 fármacos por mecanismo de acción | `nombre|descripción` |
| `Fquimica.txt` | 6 fármacos por estructura química | `nombre|descripción` |
| `Forganico.txt` | 12 fármacos por sistema orgánico | `nombre|descripción` |
| `Users.txt` | Usuarios registrados | `usuario,contraseña` (CSV, sin cifrar) |
| `Logic.java` | 38 respuestas del quiz (R1..R38) | hardcodeadas |

**Nota de seguridad:** el login usa `iniciarSesion()` leyendo `Users.txt` con la contraseña en texto plano. En la migración esto se reemplaza por SQLite + cifrado.

---

## 3. Stack de la migración

| Aspecto | Original | Migración |
|---|---|---|
| Lenguaje | Java | Kotlin 2.2.x |
| UI | Swing (`.form` de NetBeans) | Jetpack Compose + Material 3 |
| Navegación | `FarmaFrame.mostrarPanel()` | Navigation Compose (rutas) |
| Persistencia | Archivos `.txt` en el directorio de trabajo | Room (SQLite) |
| Preferencias de sesión | Variables `static` | DataStore (vía shared prefs) |
| Diálogos | `JOptionPane` | Snackbars / AlertDialog / pantallas |
| Entrada de texto | `JOptionPane.showInputDialog` | `TextField` en pantalla dedicada |
| Look & feel | JTattoo (Luna/Aluminium) | Material 3 (dark/light/dynamic color) |

Min SDK 24 · Target/compile SDK 36 · Gradle con version catalog (`gradle/libs.versions.toml`) · AGP 9.2.1.

---

## 4. Arquitectura objetivo

```
app/src/main/java/com/example/farmastudy/
├── MainActivity.kt              # Single Activity, setContent + NavHost
├── data/
│   ├── local/
│   │   ├── FarmaDatabase.kt     # Room database
│   │   ├── dao/                 # UserDao, MedicationDao, QuestionDao
│   │   └── entity/              # UserEntity, MedicationEntity, QuestionEntity
│   ├── repository/              # UserRepository, MedicationRepository, QuizRepository
│   └── datasource/              # SeedData.kt (importa los .txt originales)
├── domain/
│   ├── model/                   # User, Medication, Question, Answer, QuizResult
│   └── usecase/                 # ValidateUserUseCase, RegisterUserUseCase, RandomDrugUseCase
├── ui/
│   ├── navigation/              # Routes.kt, NavGraph.kt
│   ├── theme/                   # Color.kt, Type.kt, Theme.kt (ya existe)
│   ├── screens/
│   │   ├── login/               # LoginScreen (Sesion)
│   │   ├── register/            # RegisterScreen (Registro)
│   │   ├── home/                # HomeScreen (Inicio)
│   │   ├── classification/      # ClassificationScreen + StudyByCategoryScreen (MetClasificacion)
│   │   ├── random/              # RandomStudyScreen (MetRandom)
│   │   ├── quiz/                # QuizIntroScreen, QuizQuestionScreen, QuizResultScreen
│   │   └── components/          # Widgets reutilizables (QuestionCard, OptionRow, ...)
│   └── viewmodel/               # AuthViewModel, QuizViewModel, StudyViewModel
```

---

## 5. Diseño de la BD (Room)

### Tabla `users`
| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK autoincrement | |
| `username` | TEXT UNIQUE | El app original genera: `(nombre + edad + pass[4..6]).toUpperCase()` |
| `password` | TEXT | Hash (BCrypt/KeyStore) en lugar de texto plano |
| `email` | TEXT | |
| `age` | INTEGER | |
| `gender` | TEXT | |

### Tabla `medications`
| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK | |
| `name` | TEXT | nombre del fármaco (clave) |
| `therapeutic_use` | TEXT NULL | descripción del txt de uso terapéutico |
| `mechanism` | TEXT NULL | descripción de mecanismo de acción |
| `chemical_structure` | TEXT NULL | descripción de estructura química |
| `organic_system` | TEXT NULL | descripción de sistema orgánico |

Modelo de datos migrado del `HashMap` de `Logic.java`: 4 campos descriptivos por fármaco según la clasificación, en vez de 4 mapas separados.

### Tabla `questions`
| Columna | Tipo | Notas |
|---|---|---|
| `id` | INTEGER PK | 1..38 (G1..G38 en Swing) |
| `question` | TEXT | pregunta del quiz |
| `option_a` ... `option_d` | TEXT | opciones |
| `correct_option` | TEXT | `a`..`d` (equivale a `R1..R38` de `Logic.java`) |
| `quiz_part` | INTEGER | 1..5 (QuizP1..QuizP5 originales) |

### Tabla `quiz_attempts` (nuevo, opcional)
Registrar intentos del usuario: `id`, `user_id FK`, `score`, `total`, `date`.

---

## 6. Mapeo de pantallas Swing → Compose

| Swing | Compose (ruta) | Descripción |
|---|---|---|
| `Sesion.java` | `login` | Login: usuario + contraseña |
| `Registro.java` | `register` | Registro con validaciones (nombre, edad, email, contraseña) |
| `Inicio.java` | `home` | Menú con 3 botones: clasificación, random, quiz + cerrar sesión |
| `MetClasificacion.java` | `classification` | Elegir categoría (terapéutico/mecanismo/química/orgánico) |
| `MetRandom.java` | `random_study` | Botón "mostrar fármaco random" + input de respuesta |
| `Quiz.java` | `quiz_intro` | Intro del quiz |
| `QuizP1..QuizP5` | `quiz_question` (una sola pantalla reutilizable) | 38 preguntas, 1 por vista con indicador de progreso |
| `Respuestas.java`, `Resp1p1.java`, `Resp1p2.java` | `quiz_result` | Resultado por pregunta con la respuesta correcta |
| `Logic.haceT/M/Q/O()` (JOptionPane) | `study_by_category` | Repaso por clasificación con `TextField` para escribir el fármaco |
| `Logic.rand()` | (dentro de `random_study`) | Categoría + fármaco aleatorio |

**Mejora clave de la migración:** en Swing las 38 preguntas eran 5 paneles con 7-9 preguntas cada uno. En Compose será **una sola pantalla parametrizada** (`QuizQuestionScreen(questionIndex)`) con navegación por índice.

---

## 7. Mapeo de lógica Java → Kotlin

| Java | Kotlin | Notas |
|---|---|---|
| `Logic.InicializaListas()` | `SeedData.seed(db)` | Poblar Room desde recursos (JSON/CSV en `assets/`) |
| `Logic.iniciaT/M/Q/O()` | `MedicationRepository` + seed | Los 4 mapas pasan a la tabla `medications` |
| `Logic.haceT/M/Q/O()` | `StudyViewModel` | Repaso por clasificación |
| `Logic.rand()` / `haceRan()` | `RandomDrugUseCase` | Elección aleatoria de categoría y fármaco |
| `Logic.getStrg()`, `Seleccion()` | `remember { mutableStateOf }` | Estado de la opción seleccionada |
| `Logic.valida()/validaNew()` | `TextValidator` / `regex` | Reutilizar como validación de inputs |
| `Registro.registrarUsuario()` | `UserRepository.register()` | Room + hash de contraseña |
| `Registro.iniciarSesion()` | `UserRepository.login()` | + `AuthViewModel` con sesión en DataStore |
| `Registro.checkRepeat()` | `UserDao.findByUsername()` | Evitar duplicados (UNIQUE) |
| `Registro.validPass()` | `Validators.validPass()` | Mínimo 8 caracteres, coincidencia |
| `Registro.isMailCorrect()` | `Validators.isMailCorrect()` | Regla de caracteres antes del `@` |
| `FarmaFrame.mostrarPanel(p)` | `NavController.navigate(route)` | Navegación |
| `Inicio.cleanSelect()` | estado del `QuizViewModel` | Reset de respuestas al reiniciar quiz |

---

## 8. Migración de datos (una sola vez)

1. `Fterapeutico.txt`, `Fmecanismo.txt`, `Fquimica.txt`, `Forganico.txt` se convierten a un archivo estructurado (`assets/medications.json` o SQL seed) con el formato `clave|descripción` → columnas de la tabla `medications`.
2. Las 38 preguntas y respuestas (`QuizP*.java` + `R1..R38`) se extraen a `assets/questions.json` (5 bloques de 7-9 preguntas).
3. `Users.txt` **no se migra** (contraseñas en texto plano): los usuarios existentes deben re-registrarse con el nuevo hash.
4. La app rellena la BD en el primer arranque (`RoomDatabase.Callback.onCreate` / `seedIfEmpty`).

### Preguntas del quiz (inventario)
- QuizP1: G1–G7 (7) · QuizP2: G8–G14 (7) · QuizP3: G15–G21 (7) · QuizP4: G22–G29 (8) · QuizP5: G30–G38 (9) → **38 preguntas totales**.

### Fármacos (inventario)
- Uso terapéutico: 38 · Mecanismo de acción: 15 · Estructura química: 6 · Sistema orgánico: 12.

---

## 9. Estado actual de la migración

- [x] Proyecto Android Studio creado (AGP 9.2.1, Kotlin 2.2.10, Compose BOM 2026.02.01)
- [x] Tema Material 3 (dark/light/dynamic) — `ui/theme`
- [x] `MainActivity` con Scaffold base
- [ ] Room + entidades + DAOs
- [ ] Seed de datos (medications + questions)
- [ ] Login / Registro (hash de contraseña, DataStore)
- [ ] Pantalla home con navegación
- [ ] Repaso por clasificación
- [ ] Estudio random
- [ ] Quiz (intro → preguntas → resultado)
- [ ] Iconos y recursos finales

---

## 10. Build y ejecución

```bash
# Linux / macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

Requisitos: Android Studio (última versión estable), JDK 17+, SDK 36.

---

## 11. Roadmap

1. **Fase 1 — Infraestructura:** Room, seed de datos, navegación.
2. **Fase 2 — Auth:** login + registro con cifrado.
3. **Fase 3 — Estudio:** clasificación + random.
4. **Fase 4 — Quiz:** flujo completo con resultados.
5. **Fase 5 — Pulido:** historial de intentos, tema personalizado con la identidad de la app original, APK release.
