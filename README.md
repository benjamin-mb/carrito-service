# carrito-service

# 🛒 Carrito Service - ARKA

Microservicio reactivo de gestión de carritos de compra para el sistema ARKA.

---

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Endpoints API](#endpoints-api)
- [Integración con RabbitMQ](#integración-con-rabbitmq)
- [Scheduler de Carritos Abandonados](#scheduler-de-carritos-abandonados)
- [Webhook de Notificaciones](#webhook-de-notificaciones)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Programación Reactiva](#programación-reactiva)
- [Testing](#testing)
- [Manejo de Errores](#manejo-de-errores)

---

## 🎯 Descripción

**carrito-service** es el microservicio reactivo encargado de gestionar los carritos de compra del sistema ARKA. Proporciona funcionalidades para:

- ✅ Gestión completa de carritos de compra
- ✅ Agregar/actualizar/eliminar productos del carrito
- ✅ Finalizar carrito y crear órdenes
- ✅ **Programación reactiva** con WebFlux (Mono/Flux)
- ✅ Publicación de eventos a RabbitMQ (órdenes confirmadas)
- ✅ **Scheduler automático** para detectar carritos abandonados
- ✅ **Webhook a n8n** para enviar notificaciones (12 horas)
- ✅ Cambio de estado a abandonado (24 horas)
- ✅ Expiración automática de carritos (24 horas)
- ✅ Estados de carrito: abierto, abandonado, finalizado

---

## 🏗️ Arquitectura

Este microservicio implementa **Arquitectura Limpia (Clean Architecture)** con **Programación Reactiva**.

### ¿Por qué Clean Architecture + Reactiva?

Este microservicio combina dos paradigmas poderosos:

1. **Clean Architecture**: Separa la lógica de negocio de los detalles técnicos
2. **Programación Reactiva**: Maneja operaciones asíncronas y no bloqueantes eficientemente

**Beneficios:**
- 🚀 **Alto rendimiento**: Operaciones no bloqueantes con WebFlux
- 🔄 **Escalabilidad**: Maneja múltiples operaciones concurrentes
- 🧪 **Testabilidad**: Dominio independiente de infraestructura
- 🔌 **Integración asíncrona**: RabbitMQ, Webhooks, Scheduler

### Capas de la Arquitectura:

```
carrito-service/
│
├── 📦 domain/                         # CAPA DE DOMINIO (Lógica de Negocio)
│   │                                  # ⚠️ NO depende de infraestructura
│   ├── model/                         # Entidades de dominio puras
│   │   ├── Carrito.java              # Carrito con detalles
│   │   ├── DetalleCarrito.java       # Items del carrito
│   │   ├── Producto.java             # Referencia a producto
│   │   ├── Usuario.java              # Referencia a usuario
│   │   └── Estado.java               # Enum: abierto, abandonado, finalizado
│   │
│   ├── gateway/                       # Interfaces (Puertos de salida)
│   │   ├── CarritoGateway.java       # Contrato para persistencia reactiva
│   │   ├── DetalleCarritoGateway.java
│   │   ├── ProductoGateway.java
│   │   ├── UsuarioGateway.java
│   │   ├── EventPublisherGateway.java     # Contrato para RabbitMQ
│   │   └── NotificacionGateway.java       # Contrato para Webhook
│   │
│   ├── useCases/                      # Casos de Uso (Lógica de negocio reactiva)
│   │   ├── AgregarProductoAlCarritoUseCase.java
│   │   ├── ActualizarCantidadDeDetalleCarrito.java
│   │   ├── EliminarDetalleUseCase.java
│   │   ├── ObtenerCarritoUseCase.java
│   │   ├── FinalizarCarritoUseCase.java
│   │   └── NotificarCarritosAbandonadosUseCase.java
│   │
│   └── exception/                     # Excepciones del dominio
│       ├── CarritoActivoExistenteException.java
│       ├── CarritoVacioException.java
│       ├── StockInsuficienteException.java
│       └── ...
│
├── 🔧 infrastructure/                 # CAPA DE INFRAESTRUCTURA
│   │
│   ├── adapters/
│   │   ├── entity/                   # Entidades JPA
│   │   │   ├── CarritoEntity.java
│   │   │   ├── DetalleCarritoEntity.java
│   │   │   ├── EstadoEntity.java
│   │   │   └── ...
│   │   │
│   │   ├── repository/               # Implementación de Gateways (Reactivo)
│   │   │   ├── CarritoRepositoryImpl.java        # Usa Mono/Flux
│   │   │   ├── DetalleCarritoRepositoryImpl.java
│   │   │   ├── EventPublisherAdapters.java       # Publica a RabbitMQ
│   │   │   └── NotifacionAdapter.java            # Envía Webhooks a n8n
│   │   │
│   │   └── mapper/                   # Conversión Domain ↔ Entity
│   │       ├── CarritoMapper.java
│   │       ├── DetalleMapper.java
│   │       └── ...
│   │
│   ├── controllers/                  # Controladores REST Reactivos
│   │   └── CarritoController.java    # Retorna Mono<ResponseEntity>
│   │
│   ├── config/                       # Configuraciones
│   │   └── RabbitMQConfig.java       # Config RabbitMQ + Retry
│   │
│   ├── messages/                     # Mensajería
│   │   ├── OrdenPublisher.java       # Publisher RabbitMQ
│   │   └── Dto/                      # DTOs para eventos
│   │
│   ├── scheduler/                    # Tareas programadas
│   │   └── SchedulerCarritoAbandonado.java  # Ejecuta cada hora
│   │
│   └── exceptions/                   # Exception Handlers
│       └── GlobalExceptionHandler.java
│
└── 🔌 applicationConfig/              # CAPA DE APLICACIÓN
    └── Config.java                    # Inyección de dependencias
```

### 🎯 Principios de Clean Architecture + Reactiva:

1. **Independencia de Frameworks**: El dominio no conoce Spring WebFlux
2. **Casos de Uso Reactivos**: Retornan Mono/Flux para operaciones asíncronas
3. **Adaptadores Reactivos**: Los repositorios convierten operaciones bloqueantes a reactivas
4. **Event-Driven**: Publica eventos a RabbitMQ de forma asíncrona
5. **Regla de Dependencia**: Infraestructura → Dominio (nunca al revés)

### 🔄 Flujo Reactivo:

```
Controller (WebFlux)
    ↓ Mono<ResponseEntity>
Use Cases (Reactive)
    ↓ Mono<Carrito>
Gateways (Reactive)
    ↓ subscribeOn(Schedulers.boundedElastic())
Repository (JPA bloqueante → Reactivo)
```

**El dominio define operaciones reactivas, la infraestructura las implementa**

---

## 🛠️ Tecnologías

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 3.5.6 | Framework principal |
| **Spring WebFlux** | 3.5.6 | Framework web reactivo (NO WebMVC) |
| **Spring Data JPA** | 3.5.6 | Persistencia de datos |
| **Project Reactor** | Latest | Programación reactiva (Mono/Flux) |
| **MySQL** | 8.0+ | Base de datos |
| **RabbitMQ** | Latest | Mensajería asíncrona |
| **Lombok** | Latest | Reducción de boilerplate |
| **SpringDoc OpenAPI** | 2.7.0 | Documentación API (Swagger WebFlux) |
| **Eureka Client** | 2025.0.0 | Service Discovery |
| **Spring Scheduler** | 3.5.6 | Tareas programadas |

---

## 📦 Requisitos Previos

Antes de ejecutar este microservicio, asegúrate de tener:

1. ✅ **Java 21** o superior instalado
2. ✅ **Maven 3.8+** instalado
3. ✅ **MySQL 8.0+** corriendo en `localhost:3306`
4. ✅ **RabbitMQ** corriendo en `localhost:5672`
5. ✅ **Base de datos `arka`** creada en MySQL
6. ✅ **Eureka Server** corriendo (opcional)
7. ✅ **usuario-service** corriendo (validación de usuarios)
8. ✅ **catalog-service** corriendo (validación de productos y stock)

---

## 🚀 Instalación

### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd carrito-service
```

### 2. Configurar base de datos
```sql
CREATE DATABASE IF NOT EXISTS arka;
USE arka;

-- Tablas creadas automáticamente por JPA
-- carritos
-- detalle_carrito
```

### 3. Configurar RabbitMQ
```bash
# Crear usuario y permisos
rabbitmqctl add_user arka arka123
rabbitmqctl set_user_tags arka administrator
rabbitmqctl set_permissions -p / arka ".*" ".*" ".*"
```

### 4. Instalar dependencias
```bash
mvn clean install
```

### 5. Ejecutar el servicio
```bash
mvn spring-boot:run
```

El servicio estará disponible en: `http://localhost:8084`

---

## ⚙️ Configuración

### application.yml

```yaml
server:
  port: 8084

notificacion:
  webhook:
    url: http://localhost:8080/webhook-mock  # URL del servicio de notificaciones

spring:
  application:
    name: carrito-service

  datasource:
    url: jdbc:mysql://localhost:3306/arka?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  rabbitmq:
    host: localhost
    port: 5672
    username: arka
    password: arka123

  jpa:
    hibernate:
      ddl-auto: validate  # Cambiar a 'update' o 'create' en desarrollo
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect

eureka:
  client:
    service-url:
      defaultZone: http://admin:admin123@localhost:8761/eureka/
```

### Variables de Entorno (Recomendado para producción)

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=arka
DB_USER=root
DB_PASSWORD=your_password

RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=arka
RABBITMQ_PASSWORD=arka123

WEBHOOK_URL=http://localhost:8080/webhook-mock
EUREKA_URL=http://localhost:8761/eureka/
```

---

## 📡 Endpoints API

### 🛒 Gestión de Carrito

Todos los endpoints requieren autenticación JWT.

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| `GET` | `/api/carrito/{idUsuario}` | Obtener carrito activo del usuario | 🔐 JWT |
| `POST` | `/api/carrito/agregar` | Agregar producto al carrito | 🔐 JWT |
| `PUT` | `/api/carrito/detalle/{idDetalle}/cantidad/{cantidad}` | Actualizar cantidad | 🔐 JWT |
| `DELETE` | `/api/carrito/detalle/{idDetalle}` | Eliminar producto | 🔐 JWT |
| `POST` | `/api/carrito/finalizar/{idUsuario}` | Finalizar carrito (crear orden) | 🔐 JWT |

---

### 📝 Ejemplos de Uso

#### 1. Obtener Carrito Activo

```bash
GET /api/carrito/1
Authorization: Bearer <token>
```

**Respuesta:**
```json
{
  "creado": "2025-10-24T10:30:00",
  "estado": "abierto",
  "detalles": [
    {
      "idDdetalleCarrito": 1,
      "idCarrito": 1,
      "idProducto": 5,
      "cantidad": 2,
      "precioUnitario": 2500000,
      "subtotal": 5000000
    }
  ],
  "montoTotal": 5000000
}
```

---

#### 2. Agregar Producto al Carrito

```bash
POST /api/carrito/agregar?idUsuario=1&cantidad=2
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": 5,
  "nombre": "Laptop HP Pavilion 15",
  "precio": 2500000,
  "stock": 15,
  "caracteristicas": "Intel Core i7, 16GB RAM",
  "marca": "HP",
  "categoria": 1,
  "proveedor": 1
}
```

**Comportamiento:**
- Si NO existe carrito activo → Crea uno nuevo
- Si existe carrito activo y el producto YA está → Suma la cantidad
- Si existe carrito activo y el producto NO está → Agrega nuevo detalle
- Valida stock disponible antes de agregar
- Carrito expira automáticamente en 24 horas

**Respuesta:**
```json
{
  "idCarrito": 1,
  "idUsuario": 1,
  "creado": "2025-10-24T10:30:00",
  "estado": "abierto",
  "expirado": "2025-10-25T10:30:00",
  "detalles": [
    {
      "idDdetalleCarrito": 1,
      "idCarrito": 1,
      "idProducto": 5,
      "cantidad": 2,
      "precioUnitario": 2500000,
      "subtotal": 5000000
    }
  ]
}
```

---

#### 3. Actualizar Cantidad

```bash
PUT /api/carrito/detalle/1/cantidad/5
Authorization: Bearer <token>
```

**Comportamiento:**
- Valida stock disponible
- Solo funciona si el carrito está en estado "abierto"
- Recalcula automáticamente el subtotal

**Respuesta:**
```json
{
  "idCarrito": 1,
  "idUsuario": 1,
  "estado": "abierto",
  "detalles": [
    {
      "idDdetalleCarrito": 1,
      "cantidad": 5,
      "subtotal": 12500000
    }
  ]
}
```

---

#### 4. Eliminar Producto del Carrito

```bash
DELETE /api/carrito/detalle/1
Authorization: Bearer <token>
```

**Respuesta:**
```json
{
  "idCarrito": 1,
  "detalles": []
}
```

---

#### 5. Finalizar Carrito (Crear Orden)

```bash
POST /api/carrito/finalizar/1
Authorization: Bearer <token>
```

**Comportamiento:**
- Valida que el carrito NO esté expirado
- Valida que el carrito NO esté vacío
- Cambia el estado a "finalizado"
- Publica 2 eventos a RabbitMQ:
  1. `order.confirmed` → Para crear la orden
  2. `order.created` → Para reducir stock

**Respuesta:**
```json
{
  "idCarrito": 1,
  "estado": "finalizado",
  "montoTotal": 12500000
}
```

---

## 🐰 Integración con RabbitMQ

### Eventos que PUBLICA:

#### 1️⃣ Orden Confirmada (`order.confirmed`)

**Exchange:** `confirmed.exchange`  
**Routing Key:** `order.confirmed`  
**Cuándo:** Al finalizar un carrito

**Payload:**
```json
{
  "idUsuario": 1,
  "montoTotal": 12500000,
  "fechaCreacion": "2025-10-24T10:30:00",
  "idCarrito": 1,
  "detalles": [
    {
      "idProducto": 5,
      "cantidad": 2,
      "precioUnitario": 2500000,
      "subtotal": 5000000
    }
  ]
}
```

**Consumidor:** `orders-service` (crea la orden en su BD)

---

#### 2️⃣ Reducir Stock (`order.created`)

**Exchange:** `orders.exchange`  
**Routing Key:** `order.created`  
**Cuándo:** Al finalizar un carrito

**Payload:**
```json
{
  "items": [
    {
      "idProducto": 5,
      "cantidad": 2
    }
  ]
}
```

**Consumidor:** `catalog-service` (reduce el stock de los productos)

---

### Configuración de Reintentos:

- **Intentos máximos:** 3
- **Intervalo inicial:** 2 segundos
- **Multiplicador:** 2.0
- **Intervalo máximo:** 10 segundos

Si todos los reintentos fallan → Log de error

---

## ⏰ Scheduler de Carritos Abandonados

### Funcionamiento:

**Frecuencia:** Cada **1 hora** (3600000 ms)  
**Lógica del Scheduler:**

El scheduler ejecuta **2 tareas distintas** cada hora:

#### 1️⃣ Notificación a las 12 horas (Recordatorio)
- Busca carritos en estado "abierto" creados hace **más de 12 horas**
- Envía notificación por **Webhook a n8n** para recordar al usuario
- **NO cambia el estado** del carrito (sigue "abierto")

#### 2️⃣ Cambio de estado a las 24 horas (Expiración)
- Busca carritos en estado "abierto" creados hace **más de 24 horas**
- Cambia el estado del carrito a **"abandonado"**
- **NO envía notificación** (ya fue notificado a las 12h)

### Implementación:

```java
@Scheduled(fixedDelay = 3600000)  // Cada 1 hora
public void verificarCarritosAbandonados() {
    // Tarea 1: Notificar carritos con 12h de inactividad
    notificarCarritosAbandonadosUseCase.notifyUsersAboutAbandonedCar()
        .doOnSuccess(v -> log.info("✅ Notificaciones enviadas"))
        .subscribe();
    
    // Tarea 2: Cambiar estado de carritos con 24h de inactividad
    notificarCarritosAbandonadosUseCase.changeStateToAbandoned()
        .doOnSuccess(v -> log.info("✅ Estados actualizados a abandonado"))
        .subscribe();
}
```

### Línea de Tiempo del Carrito:

```
Hora 0: Carrito creado (estado: abierto)
   ↓
Hora 12: Notificación enviada a n8n (estado: abierto)
   ↓
Hora 24: Estado cambia a abandonado (estado: abandonado)
```

### Criterios:

- **12 horas sin actividad** → Notificación de recordatorio vía n8n
- **24 horas sin actividad** → Estado cambia a "abandonado"

---

## 📞 Webhook de Notificaciones (n8n)

### Integración con n8n:

Este microservicio se integra con **n8n**, una plataforma de automatización de workflows, para enviar notificaciones de carritos abandonados.

### URL Configurada:

```yaml
notificacion:
  webhook:
    url: http://localhost:8080/webhook-mock  # URL del workflow de n8n
```

**En producción, esta URL apuntaría a:**
- `https://n8n.tu-dominio.com/webhook/carrito-abandonado`

### Cuándo se envía:

Cuando el Scheduler detecta un carrito con **12 horas de inactividad** (ejecuta cada hora)

### Payload del Webhook:

El carrito-service envía este JSON al workflow de n8n:

```json
{
  "usuarioEmail": "cliente@example.com",
  "detalles": [
    {
      "nombreProducto": "Laptop HP Pavilion 15",
      "cantidad": 2,
      "subtotal": 5000000
    }
  ],
  "total": 5000000
}
```

### Workflow de n8n (Ejemplo):

El workflow de n8n puede:
1. **Recibir** el webhook con los datos del carrito
2. **Formatear** un email personalizado con los productos
3. **Enviar** el email al usuario usando Gmail/SendGrid/etc.

### Implementación Reactiva:

```java
public Mono<Void> sendNotiOfCarritoAbandonado(Carrito carrito) {
    return webClient.post()
        .uri(webhookUrl)  // URL del workflow de n8n
        .bodyValue(dto)
        .retrieve()
        .bodyToMono(Void.class)
        .onErrorResume(e -> {
            log.error("Error enviando a n8n: {}", e.getMessage());
            return Mono.empty();  // No falla si n8n no responde
        });
}
```

### Ventajas de usar n8n:

- ✅ **Sin código**: Los workflows se crean visualmente
- ✅ **Flexible**: Fácil cambiar el destino de las notificaciones
- ✅ **Escalable**: n8n maneja reintentos y errores
- ✅ **Multi-canal**: Email, SMS, Slack, Discord, etc.
- ✅ **Personalizable**: Plantillas de mensajes dinámicas

---

## 📁 Estructura del Proyecto

```
src/main/java/com/arka/carrito_service/
│
├── 📦 domain/
│   ├── Dto/
│   │   └── DtoCarrito.java
│   ├── Mapper/
│   │   └── Mapper.java
│   ├── model/
│   │   ├── Carrito.java
│   │   ├── DetalleCarrito.java
│   │   ├── Producto.java
│   │   ├── Usuario.java
│   │   ├── Estado.java (enum)
│   │   └── UserType.java (enum)
│   ├── gateway/
│   │   ├── CarritoGateway.java
│   │   ├── DetalleCarritoGateway.java
│   │   ├── ProductoGateway.java
│   │   ├── UsuarioGateway.java
│   │   ├── EventPublisherGateway.java
│   │   └── NotificacionGateway.java
│   ├── useCases/
│   │   ├── AgregarProductoAlCarritoUseCase.java
│   │   ├── ActualizarCantidadDeDetalleCarrito.java
│   │   ├── EliminarDetalleUseCase.java
│   │   ├── ObtenerCarritoUseCase.java
│   │   ├── FinalizarCarritoUseCase.java
│   │   └── NotificarCarritosAbandonadosUseCase.java
│   └── exception/
│       ├── CarritoActivoExistenteException.java
│       ├── CarritoVacioException.java
│       ├── StockInsuficienteException.java
│       └── ...
│
├── 🔧 infrastructure/
│   ├── adapters/
│   │   ├── entity/
│   │   │   ├── CarritoEntity.java
│   │   │   ├── DetalleCarritoEntity.java
│   │   │   ├── EstadoEntity.java
│   │   │   ├── ProductosEntity.java
│   │   │   └── UsuarioEntity.java
│   │   │
│   │   ├── repository/
│   │   │   ├── CarritoJpaRepository.java
│   │   │   ├── CarritoRepositoryImpl.java (Reactivo)
│   │   │   ├── DetalleCarritoJpaRepository.java
│   │   │   ├── DetalleCarritoRepositoryImpl.java
│   │   │   ├── EventPublisherAdapters.java
│   │   │   └── NotifacionAdapter.java
│   │   │
│   │   └── mapper/
│   │       ├── CarritoMapper.java
│   │       ├── DetalleMapper.java
│   │       ├── ProductoMapper.java
│   │       └── UsuarioMapper.java
│   │
│   ├── controllers/
│   │   └── CarritoController.java (Reactivo)
│   │
│   ├── config/
│   │   └── RabbitMQConfig.java
│   │
│   ├── messages/
│   │   ├── OrdenPublisher.java
│   │   └── Dto/
│   │       ├── CrearOrdenEventDto.java
│   │       ├── DetalleOrdenDto.java
│   │       ├── DetallesDto.java
│   │       └── CarritoAbandonadoDto.java
│   │
│   ├── scheduler/
│   │   └── SchedulerCarritoAbandonado.java
│   │
│   └── exceptions/
│       ├── GlobalExceptionHandler.java
│       └── dto/
│           └── ErrorResponseDto.java
│
└── 🔌 applicationConfig/
    └── Config.java
```

---

## ⚛️ Programación Reactiva

### ¿Por qué Reactiva?

Este microservicio usa **Spring WebFlux** en lugar de WebMVC por:

1. **Operaciones no bloqueantes**: Mejor uso de threads
2. **Escalabilidad**: Maneja más requests concurrentes
3. **Backpressure**: Controla flujo de datos
4. **Integración asíncrona**: RabbitMQ, Webhooks

### Mono vs Flux

```java
// Mono<T> - 0 o 1 elemento
Mono<Carrito> findById(Integer id);

// Flux<T> - 0 a N elementos
Flux<Carrito> findCarritosAbandonados(LocalDateTime fecha);
```

### Conversión Bloqueante → Reactivo

Los repositorios JPA son bloqueantes, pero los convertimos a reactivos:

```java
@Override
public Mono<Carrito> save(Carrito carrito) {
    return Mono.fromCallable(() -> {
        CarritoEntity entity = mapper.toEntity(carrito);
        CarritoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    })
    .subscribeOn(Schedulers.boundedElastic());  // Thread pool para operaciones bloqueantes
}
```

### Composición Reactiva

```java
public Mono<Carrito> agregarProducto(Integer idUsuario, Producto producto, Integer cantidad) {
    return obtenerOCrearCarrito(idUsuario)
        .flatMap(carrito -> validarStock(producto, cantidad)
            .flatMap(stock -> agregarDetalle(carrito, producto, cantidad))
        )
        .flatMap(carritoRepository::save);
}
```

---

## 🧪 Testing

### Testing Manual con Swagger
Accede a: `http://localhost:8084/swagger-ui.html`

### Ejemplos de Testing:

#### 1. Agregar Producto al Carrito
```bash
curl -X POST 'http://localhost:8084/api/carrito/agregar?idUsuario=1&cantidad=2' \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 5,
    "nombre": "Laptop Dell",
    "precio": 3000000,
    "stock": 10,
    "caracteristicas": "Intel i7",
    "marca": "Dell",
    "categoria": 1,
    "proveedor": 1
  }'
```

#### 2. Obtener Carrito
```bash
curl -X GET http://localhost:8084/api/carrito/1 \
  -H "Authorization: Bearer <token>"
```

#### 3. Finalizar Carrito
```bash
curl -X POST http://localhost:8084/api/carrito/finalizar/1 \
  -H "Authorization: Bearer <token>"
```

#### 4. Simular Carrito Abandonado (Manual)

**Paso 1:** Crea un carrito agregando productos  
**Paso 2:** NO lo finalices  

**Paso 3:** Simular 12 horas (Notificación a n8n):
```sql
-- Modificar fecha de creación para simular 12 horas
UPDATE carritos 
SET creado = DATE_SUB(NOW(), INTERVAL 13 HOUR)
WHERE id_carrito = 1 AND estado = 'abierto';
```

**Paso 4:** Espera que el Scheduler ejecute (cada hora) o reinicia el servicio  
**Resultado:** Webhook enviado a n8n, estado sigue "abierto"

**Paso 5:** Simular 24 horas (Cambio a abandonado):
```sql
-- Modificar fecha de creación para simular 24 horas
UPDATE carritos 
SET creado = DATE_SUB(NOW(), INTERVAL 25 HOUR)
WHERE id_carrito = 1;
```

**Paso 6:** Espera que el Scheduler ejecute nuevamente  
**Resultado:** Estado cambia a "abandonado"

---

## ❌ Manejo de Errores

### Excepciones del Dominio:

| Excepción | HTTP Status | Descripción |
|-----------|-------------|-------------|
| `CarritoActivoExistenteException` | 400 | Ya existe un carrito activo |
| `CarritoVacioException` | 400 | El carrito está vacío |
| `CarritoExpiradoException` | 400 | El carrito ha expirado |
| `CarritoDiferenteDeAbiertoException` | 400 | El carrito no está abierto |
| `DetalleCarritoNoEncontradoException` | 404 | Detalle no encontrado |
| `ProductNotFoundException` | 404 | Producto no encontrado |
| `UsuarioNoEncontradoException` | 404 | Usuario no encontrado |
| `StockInsuficienteException` | 404 | Stock insuficiente |
| `IllegalArgumentException` | 400 | Argumento inválido |
| `IllegalStateException` | 400 | Estado inválido |

### Ejemplo de Respuesta de Error:

```json
{
  "status": 400,
  "message": "Insufficient stock",
  "timestamp": "2025-10-24T12:30:00"
}
```

### Global Exception Handler (Reactivo):

```java
@ExceptionHandler(StockInsuficienteException.class)
public Mono<ResponseEntity<ErrorResponseDto>> handleStockInsuficiente(StockInsuficienteException ex) {
    ErrorResponseDto error = new ErrorResponseDto(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        LocalDateTime.now()
    );
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
}
```

---

## 🔗 Integración con Gateway

Este microservicio se integra con el **API Gateway** de ARKA:

### Rutas (Todas requieren autenticación JWT):

- `GET /arka/carrito/{idUsuario}`
- `POST /arka/carrito/agregar`
- `PUT /arka/carrito/detalle/{id}/cantidad/{cantidad}`
- `DELETE /arka/carrito/detalle/{id}`
- `POST /arka/carrito/finalizar/{idUsuario}`

**Gateway URL:** `http://localhost:8090`

---

## 📊 Modelo de Datos

### Tabla: carritos

```sql
CREATE TABLE carritos (
  id_carrito INT PRIMARY KEY AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  creado_en DATETIME NOT NULL,
  estado ENUM('abierto', 'abandonado', 'finalizado') NOT NULL,
  expirado DATETIME NOT NULL,
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);
```

### Tabla: detalle_carrito

```sql
CREATE TABLE detalle_carrito (
  id_detalle_carrito INT PRIMARY KEY AUTO_INCREMENT,
  id_carrito INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL,
  precioUnitario INT NOT NULL,
  subtotal INT NOT NULL,
  FOREIGN KEY (id_carrito) REFERENCES carritos(id_carrito) ON DELETE CASCADE,
  FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);
```

---

## 🚀 Despliegue

### Docker (Próximamente)
```bash
docker build -t carrito-service .
docker run -p 8084:8084 carrito-service
```

### Variables de Entorno Requeridas:
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USER`
- `RABBITMQ_PASSWORD`
- `WEBHOOK_URL`

---

## 📝 Notas Adicionales

### Dependencias con Otros Servicios:

- **usuario-service**: Validación de usuarios
- **catalog-service**: Validación de productos y stock
- **orders-service**: Recibe eventos de órdenes confirmadas
- **n8n** (Webhook): Recibe notificaciones de carritos abandonados y las distribuye

### Características Especiales:

- **Reactivo**: WebFlux + Project Reactor
- **Clean Architecture**: Dominio independiente
- **Event-Driven**: RabbitMQ
- **Scheduler**: Tarea cada hora (2 operaciones)
- **Webhook a n8n**: Notificaciones externas automatizadas
- **Doble verificación**: 12h (notificación) y 24h (abandono)

### Performance:

- **No bloqueante**: Operaciones asíncronas
- **Backpressure**: Control de flujo reactivo
- **Thread pool**: Schedulers.boundedElastic() para JPA

### Estados del Carrito:

1. **abierto**: Carrito activo, se puede modificar
   - Creación → Hora 0
   - Notificación n8n → Hora 12 (sigue abierto)
   
2. **abandonado**: Sin actividad por 24 horas
   - Cambio automático → Hora 24
   - Ya no se puede modificar
   
3. **finalizado**: Orden creada exitosamente
   - Al finalizar el carrito manualmente
   - Ya no se puede modificar

### Timeline del Carrito:

```
00:00 → Carrito creado (ABIERTO)
  |
12:00 → Notificación enviada a n8n (ABIERTO)
  |     Usuario recibe email de recordatorio
  |
24:00 → Estado cambia a ABANDONADO
  |     Carrito ya no se puede modificar
```

---

**🛒 carrito-service v1.0.0**  
*Microservicio reactivo de carritos con Clean Architecture*
