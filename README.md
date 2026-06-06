# Diaz Moviles - App Android

Aplicación móvil Android para tienda de equipos móviles. Consume la API REST Django/PostgreSQL desplegada en DigitalOcean.

## 📱 Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM con Hilt (DI)
- **Red:** Retrofit + OkHttp + Gson
- **Almacenamiento local:** DataStore Preferences (tokens)
- **Imágenes:** Coil
- **Autenticación:** JWT (SimpleJWT)

## 🔐 Credenciales de Prueba

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Admin | `admin` | `admin123` |
| Cliente | `cliente` | `cliente123` |

## 🚀 Instalación

### Requisitos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Gradle 8.13+

### Pasos
1. Clonar el repositorio
2. Abrir con Android Studio
3. Configurar `local.properties`:
   ```properties
   API_BASE_URL=https://diaz-moviles.uaeftt-ute.site/api/
   ```
4. Sincronizar Gradle
5. Ejecutar en emulador o dispositivo físico

### Compilar APK
```bash
./gradlew assembleDebug
# APK generado en: app/build/outputs/apk/debug/app-debug.apk
```

## 📱 Pantallas

| Pantalla | Ruta | Descripción |
|----------|------|-------------|
| **Login** | `login` | Inicio de sesión con JWT, animaciones |
| **Home** | `home` | Menú principal con cards de navegación |
| **Catálogo** | `productos` | Grid de productos con búsqueda y scroll infinito |
| **Detalle Producto** | `detalle/{id}` | Imagen, especificaciones, botón agregar al carrito |
| **Carrito** | `carrito` | Items en carrito, cantidad +/- , total, checkout |
| **Checkout** | `checkout` | Formulario cliente + método pago + confirmación |
| **Pedidos** | `pedidos` | Historial de ventas con estado |
| **Registrar** | `registrar` | Registro de nuevo cliente |
| **Admin** | `admin` | Panel admin: productos, marcas, categorías |
| **Admin Producto** | `admin/producto/{id}` | Crear/editar producto con dropdowns |
| **Admin Marcas** | `admin/marcas` | CRUD de marcas con búsqueda |
| **Admin Categorías** | `admin/categorias` | CRUD de categorías con búsqueda |

## 🏗️ Arquitectura MVVM

```
app/
├── data/
│   ├── local/          # TokenDataStore
│   ├── remote/
│   │   ├── api/        # Interfaces Retrofit (7 APIs)
│   │   ├── dto/        # Data Transfer Objects
│   │   └── interceptor/# BearerTokenInterceptor
│   └── repository/     # Implementaciones repositorio
├── domain/
│   ├── model/          # Modelos de dominio (7 entidades)
│   └── repository/     # Interfaces repositorio
├── di/                 # Hilt modules (NetworkModule, RepositoryModule)
├── presentation/
│   ├── navigation/     # NavGraph (12 rutas)
│   ├── ui/             # Screens (12 pantallas)
│   ├── viewmodel/      # ViewModels (11)
│   └── theme/          # Tema monochrome + gold
└── MainActivity.kt
```

## 🔌 APIs Consumidas (7 entidades)

| Entidad | Listar | Detalle | Crear | Actualizar | Eliminar |
|---------|--------|---------|-------|------------|----------|
| **Producto** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Marca** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Categoría** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Cliente** | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Venta** | ✅ | ✅ | ✅ | ❌ | ✅ |
| **DetalleVenta** | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Proveedor** | ✅ | ✅ | ✅ | ✅ | ✅ |

## 🔑 Autenticación

- Login con JWT → almacena token en DataStore
- Token se envía automáticamente en todas las peticiones (Interceptor)
- Logout limpia sesión completamente
- Admin: acceso al panel de administración
- Cliente: solo consulta y compra

## 🎨 Diseño

- **Colores:** Monocromático negro/blanco con acento dorado (#D4A843)
- **Tema:** Oscuro y claro
- **Tipografía:** Sans-Serif, pesos variados
- **Animaciones:** Login con fade-in y slide
- **Componentes:** Material 3 Cards, Chips, Dialogs, Snackbars

## 📦 APK

Descargar: `app/build/outputs/apk/debug/app-debug.apk`

## 🔗 Backend

API desplegada en: `https://diaz-moviles.uaeftt-ute.site`
Repositorio backend: [diaz_moviles](https://github.com/X-Kemi02-X/diaz_moviles)
