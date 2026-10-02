# DatosSinMVVM

Laboratorio 07 del curso **Programacion en Moviles** (Tecsup, ciclo 2026-01, docente
Benjamin Pareja): uso de la base de datos SQLite con **Room**, sin patron MVVM.

Alumno: Alejandro Aaron Alvarez Sayaverde.

## Que hay aqui

| Archivo | Para que |
|---|---|
| `User.kt` | Entidad `@Entity` con `uid`, `first_name` y `last_name`. |
| `UserDao.kt` | DAO con `getAll()`, `insert()` y, para el ejercicio 1, `getLast()` y `delete()`. |
| `UserDatabase.kt` | Clase `@Database` que expone el DAO. |
| `ScreenUser.kt` | Pantalla de la parte guiada: tres `TextField` y dos botones. |
| `ScreenUserScaffold.kt` | Ejercicio 1: la misma pantalla dentro de un `Scaffold`, con los botones movidos al `TopAppBar` y un tercero que borra el ultimo registro. |

## Nota sobre kapt

La guia indica `kotlin-kapt`, pero con AGP 9 (Kotlin integrado en el plugin de Android)
ese plugin ya no se puede aplicar: el build falla con
`The 'org.jetbrains.kotlin.kapt' plugin is not compatible with built-in Kotlin support`.
El proyecto usa **KSP** (`com.google.devtools.ksp` 2.2.10-2.0.2) con Room 2.8.5,
que es el procesador que Room recomienda hoy, y
`android.disallowKotlinSourceSets=false` en `gradle.properties` para que AGP acepte
las fuentes que KSP genera.
