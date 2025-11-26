# Proyecto Green Bunny (Android + Xano)

Aplicación móvil Android (Kotlin) que consume un backend en Xano usando Retrofit/OkHttp y Coil para imágenes. Incluye login, roles `admin` y `cliente`, catálogo, carrito, pedidos, usuarios y subida de imágenes.

## Requisitos

- Android Studio con SDK 36 (Android 15)
- JDK 11 configurado en el IDE
- Dispositivo/emulador con API 24+

## Configuración Android

- `compileSdk`: 36, `minSdk`: 24, `targetSdk`: 36 en `app/build.gradle.kts`
- Permiso de internet habilitado en `app/src/main/AndroidManifest.xml`
- `BuildConfig` genera campos para URLs de Xano y se activan con:
  - `viewBinding = true`
  - `buildConfig = true`
- Compatibilidad Java/Kotlin a 11.

## Backend (Xano)

La app consume dos bases:

- Autenticación: se usa para `auth/login`, `auth/signup`, `auth/me`.
- Tienda/Productos: se usa para productos, categorías, pedidos, envíos, relación pedido-producto y subida de imágenes.

Las URLs base se definen como `buildConfigField` en `app/build.gradle.kts` y se leen en runtime vía `BuildConfig` y `ApiConfig`.

## Variables y URLs necesarias

Valores actuales definidos en `app/build.gradle.kts`:

- `XANO_STORE_BASE` → `https://x8ki-letl-twmt.n7.xano.io/api:Ybbgn3cq/`
- `XANO_AUTH_BASE` → `https://x8ki-letl-twmt.n7.xano.io/api:iHS4Ivne/`
- `XANO_TOKEN_TTL_SEC` → `86400`

> Para cambiarlos: edita `app/build.gradle.kts` y recompila.

## Usuarios de prueba

- Admin: `juanf1@gmail.com` / `holamundo123`
- Cliente: `cliente@gmail.com` / `cliente123`

## Almacenamiento de imágenes

- Las imágenes se suben y almacenan en Xano; la API devuelve metadatos incluyendo `url`.
- La app usa Coil para cargar la `url` de `ProductImage` en las vistas de producto/galería.

## Compilación y ejecución

- Abrir el proyecto en Android Studio, sincronizar Gradle y seleccionar un dispositivo API 24+.
- Ejecutar:
  - `gradlew.bat assembleDebug`
  - `gradlew.bat installDebug`
- La actividad de inicio es `MainActivity` (pantalla de login).

## Flujo de autenticación

1. Login (`POST auth/login`) con `email` y `password`.
2. Guardado temporal del token para habilitar el interceptor.
3. Consulta protegida a `GET auth/me` para perfil básico.
4. Completar rol/estado con `GET user/{id}`.
5. Guardar sesión en `SharedPreferences` y redirigir según rol:
   - Admin → `HomeActivityAdmin`
   - Cliente → `HomeActivityCliente`

## Endpoints principales

- Auth (base `XANO_AUTH_BASE`):
  - `POST auth/login`
  - `POST auth/signup`
  - `GET auth/me`
- Tienda (base `XANO_STORE_BASE`):
  - Productos: `GET/POST product`, `PATCH/DELETE product/{id}`
  - Categorías: `GET/POST/PATCH/DELETE category`, `GET category/{id}`
  - Pedidos: `GET/POST/PATCH/DELETE order`, `GET order/{id}`
  - Relación pedido-producto: `GET/POST/PATCH/DELETE order_product`, `GET order_product/{id}`
  - Envíos: `GET/POST/PATCH/DELETE shipment`, `GET shipment/{id}`
  - Upload: `POST upload/image` (multipart)

## Estructura del proyecto

- `app/src/main/java/com/miapp/greenbunny/api` → clientes y servicios Retrofit
- `app/src/main/java/com/miapp/greenbunny/model` → modelos (Producto, Usuario, etc.)
- `app/src/main/java/com/miapp/greenbunny/ui` → Activities y Fragments
- `app/src/main/res` → layouts, drawables, menus, valores
- Configuración Gradle:
  - Raíz: `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`
  - Módulo: `app/build.gradle.kts`

## Notas

- Seguridad: el token se guarda en `SharedPreferences` (suficiente para demo); para producción usar `EncryptedSharedPreferences` o `DataStore` con cifrado.
- Si actualizas las bases de Xano, edita `app/build.gradle.kts` y recompila. La fuente de la verdad son los campos `BuildConfig`.

