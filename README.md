# Diaz Moviles - Aplicación Android

Aplicación móvil Android para tienda de equipos móviles, construida con Kotlin y Jetpack Compose (Material 3). Consume una API REST Django desplegada en DigitalOcean.

---

## Requisitos de instalación

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17+
- Gradle 8.13+
- Android SDK 24+ (minSdk), target SDK 35
- Dispositivo o emulador Android 7.0 (API 24) o superior
- Conexión a Internet

---

## Configuración de la URL base del backend

1. Abre el proyecto en Android Studio
2. En la raíz del proyecto, crea o edita el archivo `local.properties`
3. Agrega la siguiente línea:

```
API_BASE_URL=https://diaz-moviles.uaeftt-ute.site/api/
```

Si el archivo `local.properties` no existe, la URL por defecto es `https://diaz-moviles.uaeftt-ute.site/api/`.

---

## Usuarios de prueba

| Rol | Usuario | Contraseña | Staff |
|---|---|---|---|
| Administrador | `admin` | `admin123` | Sí |
| Cliente regular | `cliente` | `cliente123` | No |

---

## Entidades implementadas

| # | Entidad | Descripción | Endpoints CRUD |
|---|---|---|---|
| 1 | **Producto** | Teléfonos, tablets y accesorios en venta. Campos: nombre, marca, categoría, modelo, precio, stock, descripción, activo | `GET/POST /api/productos/`, `GET/PUT/DELETE /api/productos/{id}/` |
| 2 | **Marca** | Fabricantes (Samsung, Apple, Xiaomi, etc.) | `GET/POST /api/marcas/`, `GET/PUT/DELETE /api/marcas/{id}/` |
| 3 | **Categoría** | Clasificaciones (Smartphones, Tablets, Accesorios) | `GET/POST /api/categorias/`, `GET/PUT/DELETE /api/categorias/{id}/` |
| 4 | **Cliente** | Usuarios registrados que realizan compras. Vinculado a User vía `usuario` FK | `GET/POST /api/clientes/`, `GET/PUT/DELETE /api/clientes/{id}/` |
| 5 | **Proveedor** | Empresas que abastecen productos | `GET/POST /api/proveedores/`, `GET/PUT/DELETE /api/proveedores/{id}/` |
| 6 | **Venta** | Órdenes de compra realizadas por clientes. Estados: pendiente, completada, anulada, cancelada | `GET/POST /api/ventas/`, `GET/PUT/DELETE /api/ventas/{id}/` |
| 7 | **DetalleVenta** | Productos individuales dentro de una venta (cantidad, precio unitario, subtotal) | `GET/POST /api/detalles-venta/`, `GET/PUT/DELETE /api/detalles-venta/{id}/` |

---

## Pantallas de la aplicación

| # | Pantalla | Ruta | Descripción |
|---|---|---|---|
| 1 | **Home** | `home` | Pantalla principal con header degradado, banner promocional, fila de categorías dinámicas desde API y cards de productos destacados |
| 2 | **Login** | `login` | Inicio de sesión con JWT, fondo animado con gradiente, campos de usuario/contraseña con foco por Enter |
| 3 | **Registro** | `registrar` | Registro de nuevo usuario (crea User + Cliente simultáneamente) |
| 4 | **Productos** | `productos?categoriaId={id}` | Catálogo con búsqueda, filtros por marca y categoría, carga infinita (scroll), shimmer loading |
| 5 | **Detalle Producto** | `detalle/{productoId}` | Vista detallada del producto con precio, stock, descripción y botón "Agregar al carrito" |
| 6 | **Carrito** | `carrito` | Lista de productos agregados al carrito, persistido por usuario en DataStore. Permite modificar cantidades y eliminar items |
| 7 | **Checkout** | `checkout` | Formulario de datos del cliente (busca por email, crea si no existe), selecciona método de pago, confirma pedido |
| 8 | **Pedidos** | `pedidos` | Historial de pedidos del usuario autenticado |
| 9 | **Perfil** | `perfil` | Menú de perfil con acceso a "Mis pedidos" y "Editar mis datos" |
| 10 | **Editar Perfil** | `editar-perfil` | Edición de datos personales del cliente (nombre, apellido, cédula, email, teléfono, dirección) y cambio de contraseña |
| 11 | **Admin** | `admin` | Panel de administración con TabRow de 5 pestañas (Productos, Marcas, Categorías, Clientes, Ventas). CRUD completo, búsqueda, paginación con botones |
| 12 | **Admin - Formulario Producto** | `admin/producto/{productoId}` | Crear/editar producto desde el panel admin |

---

## Ejemplos de consumo de la API con token

### Login

```
POST https://diaz-moviles.uaeftt-ute.site/api/token/
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta:
```json
{
  "access": "eyJ0eXAiOiJKV1Qi...",
  "refresh": "eyJ0eXAiOiJKV1Qi...",
  "user_id": 1,
  "username": "admin",
  "email": "admin@email.com",
  "is_staff": true
}
```

### Listar productos (público, sin token)

```
GET https://diaz-moviles.uaeftt-ute.site/api/productos/?search=galaxy&page=1
```

### Crear producto (admin, requiere token)

```
POST https://diaz-moviles.uaeftt-ute.site/api/productos/
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "nombre": "Galaxy S25",
  "marca": 1,
  "categoria": 1,
  "modelo": "S25",
  "precio": "999.99",
  "stock": 15,
  "descripcion": "Samsung Galaxy S25",
  "activo": true
}
```

### Listar ventas del usuario autenticado

```
GET https://diaz-moviles.uaeftt-ute.site/api/ventas/?usuario=1
Authorization: Bearer <access_token>
```

### Actualizar estado de venta (admin)

```
PATCH https://diaz-moviles.uaeftt-ute.site/api/ventas/1/
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "estado": "completada"
}
```

---

## Capturas de pantalla

*(Agregar aquí las capturas de pantalla de la aplicación)*

---

## Estructura del proyecto

```
app/src/main/java/com/diazmoviles/app/
├── data/
│   ├── local/          # TokenDataStore (DataStore Preferences)
│   ├── remote/
│   │   ├── api/        # Interfaces Retrofit (AuthApi, ProductoApi, etc.)
│   │   ├── dto/        # Data Transfer Objects
│   │   ├── interceptor/# AuthInterceptor (refresh automático de token)
│   │   └── util/       # ErrorUtils (parseError, safeApiCall)
│   └── repository/     # Implementaciones de repositorios
├── di/                 # Hilt modules (NetworkModule, RepositoryModule)
├── domain/
│   ├── model/          # Modelos de dominio (Producto, Cliente, Venta, etc.)
│   └── repository/     # Interfaces de repositorio
├── presentation/
│   ├── navigation/     # NavGraph con rutas de todas las pantallas
│   ├── ui/             # Composables por pantalla
│   │   ├── admin/
│   │   ├── auth/
│   │   ├── cart/
│   │   ├── checkout/
│   │   ├── components/ # AnimatedBackground, ShimmerEffect
│   │   ├── detail/
│   │   ├── home/
│   │   ├── orders/
│   │   ├── products/
│   │   ├── profile/
│   │   └── register/
│   └── viewmodel/      # ViewModels para cada pantalla
├── ui/theme/           # Tema Material 3 (Color, Type, Theme)
├── MainActivity.kt
└── ShopApplication.kt
```

---

## Tecnologías usadas

- **Kotlin** - Lenguaje principal
- **Jetpack Compose (Material 3)** - UI declarativa
- **Hilt** - Inyección de dependencias
- **Retrofit + OkHttp** - Cliente HTTP
- **Coil** - Carga de imágenes
- **DataStore Preferences** - Persistencia local de tokens y carrito
- **Navigation Compose** - Navegación entre pantallas
- **ViewModel + StateFlow** - Manejo de estado y ciclo de vida

---

## Instrucciones para ejecutar la app

### Desde Android Studio

1. Clona el repositorio:
   ```bash
   git clone https://github.com/X-Kemi02-X/diaz-moviles-app.git
   ```

2. Abre el proyecto en Android Studio (selecciona la carpeta raíz)

3. Espera a que Gradle sincronice las dependencias

4. Crea o edita `local.properties` en la raíz del proyecto y agrega:
   ```
   API_BASE_URL=https://diaz-moviles.uaeftt-ute.site/api/
   ```

5. Conecta un dispositivo Android o inicia un emulador

6. Haz clic en **Run** (o `Shift+F10`)

### Generar APK

```
./gradlew assembleDebug
```

El APK se generará en `app/build/outputs/apk/debug/app-debug.apk`.

### Notas

- Si cambias la URL del backend, solo necesitas actualizar `API_BASE_URL` en `local.properties` y recompilar.
- La app funciona sin conexión para el catálogo solo si ya se cargaron datos previamente (sin caché offline).
- El carrito se persiste por usuario en DataStore: al cerrar sesión se guarda, al iniciar sesión se carga automáticamente.
