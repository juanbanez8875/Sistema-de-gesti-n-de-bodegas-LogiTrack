# 📦 LogiTrack — Sistema de Gestión y Auditoría de Bodegas

Sistema backend centralizado en **Spring Boot 4.1** para la empresa LogiTrack S.A., que administra bodegas distribuidas en distintas ciudades. Permite controlar movimientos de inventario (entradas, salidas y transferencias), registrar auditorías automáticas de todos los cambios y proteger la información con autenticación JWT.

## ✅ Características

| Requisito | Estado |
|-----------|--------|
| Gestión de Bodegas (CRUD) | ✅ id, nombre, ubicación, capacidad, encargado |
| Gestión de Productos (CRUD) | ✅ id, nombre, categoría, stock, precio |
| Movimientos de Inventario | ✅ ENTRADA, SALIDA, TRANSFERENCIA con stock por bodega |
| Auditoría automática | ✅ JPA EntityListeners (INSERT, UPDATE, DELETE) |
| Autenticación JWT | ✅ Spring Security 7 + jjwt 0.12 |
| Roles ADMIN / EMPLEADO | ✅ Guardado directo en la entidad Usuario |
| Consultas avanzadas | ✅ Stock bajo (<10), movimientos por fecha, auditorías filtradas |
| Reporte de resumen | ✅ Stock por bodega + productos más movidos |
| Swagger/OpenAPI 3 | ✅ http://localhost:8080/swagger-ui.html |
| Manejo de excepciones | ✅ @ControllerAdvice con JSON para 400/401/403/404/500 |
| Validaciones | ✅ @NotNull, @Size, @Min, @Email, @DecimalMin |
| Scripts SQL | ✅ schema.sql + data.sql |
| Frontend HTML/CSS/JS | ✅ Login + dashboard completo |

## 🛠️ Stack Tecnológico

- **Java 21** + **Spring Boot 4.1.2** + **Spring Framework 7**
- **Spring Security 7** (stateless, lambda DSL)
- **Spring Data JPA** + **Hibernate 7.4**
- **jjwt 0.12.6** (JWT con HMAC-SHA512)
- **H2** (base de datos por defecto, sin instalación) / **MySQL** (perfil `mysql`)
- **springdoc-openapi 3.2.0** (Swagger UI)
- **Lombok** (reduce boilerplate)
- **Maven** (gestión de dependencias)

## 📁 Estructura del Proyecto

```
project/
├── src/main/java/com/logitracksa/project/
│   ├── config/              # OpenApiConfig, DataInitializer, AuditConfig, ApplicationContextHolder
│   ├── controller/          # Auth, Bodega, Producto, Movimiento, Auditoria, Reporte
│   ├── exception/           # GlobalExceptionHandler, BusinessException, ResourceNotFoundException
│   ├── listener/            # AuditEntityListener, AuditHolder, AuditInfo, AuditSynchronization
│   ├── model/
│   │   ├── entity/          # Usuario, Bodega, Producto, Inventario, Movimiento, DetalleMovimiento, Auditoria
│   │   ├── enums/           # Rol, TipoMovimiento, TipoOperacion
│   │   └── dto/            # auth/, request/, response/
│   ├── repository/          # 7 interfaces Spring Data JPA
│   ├── security/           # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
│   ├── service/            # AuthService, BodegaService, ProductoService, MovimientoService, AuditoriaService, ReporteService
│   └── util/               # SecurityUtils
├── src/main/resources/
│   ├── application.properties        # Configuración H2 (por defecto)
│   ├── application-mysql.properties  # Configuración MySQL
│   ├── sql/schema.sql                # Script DDL
│   ├── sql/data.sql                   # Script DML (datos seed)
│   └── static/                        # Frontend copiado para servir desde Spring Boot
├── frontend/               # Frontend HTML/CSS/JS original
│   ├── index.html         # Página de login
│   ├── dashboard.html     # Panel principal
│   ├── css/style.css
│   └── js/                # api.js, auth.js, dashboard.js
├── pom.xml
└── README.md
```

## 🚀 Instalación y Ejecución

### Opción 1: H2 en memoria (sin instalar nada)

```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080` con base de datos H2 en memoria.

### Opción 2: MySQL

1. Tener MySQL corriendo en `localhost:3306`
2. Ejecutar con el perfil MySQL:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

Los datos de conexión están en `application-mysql.properties` (usuario `root`, password `root`). Ajuste según su entorno.

### Scripts SQL independientes

Los archivos `src/main/resources/sql/schema.sql` y `data.sql` pueden ejecutarse directamente en MySQL para crear la base de datos y cargar datos iniciales.

## 👥 Usuarios por Defecto

| Usuario   | Contraseña    | Rol      |
|-----------|-------------|----------|
| `admin`   | `admin123`   | ADMIN    |
| `empleado`| `empleado123`| EMPLEADO |

> El `DataInitializer` crea estos usuarios automáticamente al arrancar si la BD está vacía. Las contraseñas se cifran con BCrypt.

## 📡 Endpoints de la API

### Autenticación (públicos)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Iniciar sesión, devuelve JWT |
| POST | `/auth/register` | Registrar nuevo usuario |

### Bodegas
| Método | Ruta | Acceso |
|--------|------|--------|
| GET | `/bodegas` | Autenticado |
| GET | `/bodegas/{id}` | Autenticado |
| POST | `/bodegas` | ADMIN |
| PUT | `/bodegas/{id}` | ADMIN |
| DELETE | `/bodegas/{id}` | ADMIN |

### Productos
| Método | Ruta | Acceso |
|--------|------|--------|
| GET | `/productos` | Autenticado |
| GET | `/productos/{id}` | Autenticado |
| POST | `/productos` | ADMIN |
| PUT | `/productos/{id}` | ADMIN |
| DELETE | `/productos/{id}` | ADMIN |

### Movimientos
| Método | Ruta | Acceso |
|--------|------|--------|
| GET | `/movimientos` | Autenticado |
| GET | `/movimientos/{id}` | Autenticado |
| POST | `/movimientos` | Autenticado |
| GET | `/movimientos/fecha?inicio=...&fin=...` | Autenticado |

### Reportes
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/reportes/resumen` | Stock por bodega + productos más movidos |
| GET | `/reportes/stock-bajo` | Productos con stock < 10 |
| GET | `/reportes/movimientos?inicio=...&fin=...` | Movimientos por rango de fechas |

### Auditorías (solo ADMIN)
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/auditorias` | Todas las auditorías |
| GET | `/auditorias/usuario/{usuario}` | Filtrar por usuario |
| GET | `/auditorias/tipo/{tipo}` | Filtrar por INSERT/UPDATE/DELETE |
| GET | `/auditorias/entidad/{entidad}` | Filtrar por entidad |
| GET | `/auditorias/fecha?inicio=...&fin=...` | Filtrar por rango de fechas |

## 🔑 Ejemplo de uso con JWT

### 1. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIs...",
  "type": "Bearer",
  "username": "admin",
  "rol": "ADMIN",
  "expiresIn": 86400000
}
```

### 2. Usar el token en peticiones protegidas

```bash
curl http://localhost:8080/bodegas \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 3. Crear un movimiento

```bash
curl -X POST http://localhost:8080/movimientos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "tipo": "ENTRADA",
    "bodegaDestinoId": 1,
    "detalles": [{"productoId": 1, "cantidad": 50}]
  }'
```

## 📄 Swagger / OpenAPI

La documentación interactiva está disponible en:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

Para probar endpoints protegidos desde Swagger:
1. Haga clic en el botón **Authorize** (🔒)
2. Pegue el token JWT obtenido de `/auth/login`
3. Los endpoints protegidos usarán el token automáticamente

## 🖥️ Frontend

El frontend está en la carpeta `frontend/` y también se sirve desde Spring Boot en `src/main/resources/static/`.

**Acceder al frontend:**
- Desde Spring Boot: `http://localhost:8080/index.html`
- Abrir `frontend/index.html` directamente en el navegador

El frontend permite:
- Login y registro de usuarios
- Gestión de bodegas y productos (CRUD para ADMIN)
- Registro de movimientos con detalles
- Visualización de reportes (stock por bodega, productos más movidos, stock bajo)
- Consulta de auditorías con filtros (solo ADMIN)

## 🔍 Auditoría Automática

El sistema registra automáticamente todas las operaciones INSERT, UPDATE y DELETE sobre las entidades `Bodega`, `Producto`, `Usuario` y `Movimiento` mediante:

1. **`AuditEntityListener`** — listener JPA con `@PostLoad`, `@PostPersist`, `@PostUpdate`, `@PostRemove`
2. **`@PostLoad`** — captura el estado anterior (snapshot JSON) para compararlo en UPDATE/DELETE
3. **`AuditHolder`** — acumula eventos en un `ThreadLocal` durante la transacción
4. **`AuditSynchronization`** — persiste los registros **después del commit** (solo si la operación tuvo éxito)
5. Cada auditoría registra: tipo de operación, fecha/hora, usuario, entidad afectada, valores anteriores y nuevos (JSON)

## 📐 Arquitectura

Ver el documento [`docs/ARQUITECTURA.md`](docs/ARQUITECTURA.md) para el diagrama de clases, descripción de capas y flujo de autenticación JWT.
