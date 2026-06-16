# 📱 Smart Helper

Aplicación móvil Android desarrollada en **Java** para la gestión inteligente de recordatorios, geo-recordatorios y organización personal mediante notificaciones, geolocalización y persistencia local.

---

## 📖 Descripción

Smart Helper es una aplicación móvil diseñada para ayudar a los usuarios a organizar sus actividades diarias mediante recordatorios tradicionales y geo-recordatorios basados en ubicación.

El proyecto integra funcionalidades como programación de tareas, geofencing, reconocimiento de voz, historial de actividades y almacenamiento local utilizando Room Database y SQLite, ofreciendo una solución práctica para la gestión personal.

---

## ✨ Características principales

* Gestión inteligente de recordatorios personalizados.
* Creación de geo-recordatorios utilizando Google Maps.
* Activación automática mediante Geofencing.
* Sistema de notificaciones locales programadas.
* Reconocimiento de voz para facilitar la creación de tareas.
* Historial de actividades y recordatorios completados.
* Configuración personalizada del usuario.
* Persistencia local mediante Room Database.

---

## 🛠️ Tecnologías utilizadas

### Lenguaje

* Java

### Desarrollo móvil

* Android Studio
* Android SDK

### Persistencia de datos

* Room Database
* SQLite

### Geolocalización

* Google Maps SDK
* Google Play Services Location
* Geofencing API

### Navegación

* Fragments
* Navigation Component

### Notificaciones

* AlarmManager
* BroadcastReceiver
* NotificationCompat

### Diseño

* Material Design
* ConstraintLayout

---

## 🏗️ Arquitectura del proyecto

```text
jdc.trabajos.smarthelper
│
├── activities
├── adapters
├── broadcasts
├── database
├── fragments
├── helpers
└── models
```

La aplicación sigue una arquitectura modular que separa la lógica de negocio, persistencia de datos, interfaz de usuario y componentes auxiliares para facilitar el mantenimiento y la escalabilidad.

---

## 📋 Funcionalidades

### Gestión de recordatorios

* Crear recordatorios personalizados.
* Registrar título y descripción.
* Configurar fecha, hora y frecuencia.
* Almacenar información localmente.

### Geo-recordatorios

* Seleccionar ubicaciones mediante Google Maps.
* Configurar radio de activación.
* Activación automática mediante Geofencing.

### Sistema de notificaciones

* Notificaciones locales.
* Alertas programadas.
* Posponer recordatorios.
* Canal de notificaciones personalizado.

### Reconocimiento de voz

* Captura de texto mediante voz.
* Creación rápida de recordatorios.

### Historial

* Consulta de actividades completadas.
* Seguimiento de recordatorios.

### Configuración

* Administración de preferencias del usuario.
* Configuración de sincronización y notificaciones.

---

## 🎥 Video demostrativo

El proyecto incluye una demostración funcional donde se presentan las principales características de la aplicación:

* Creación de recordatorios.
* Creación de geo-recordatorios.
* Integración con Google Maps.
* Sistema de notificaciones.
* Historial de tareas.
* Configuración del usuario.
* Navegación mediante BottomNavigationView.

**Video:**

`demo/smart-helper-demo.mp4`

---

## 📸 Capturas

### Inicio

![Inicio](screenshots/home.png)

### Crear recordatorio

![Crear Recordatorio](screenshots/create-reminder.png)

### Recordatorios

![Recordatorios](screenshots/reminders.png)

### Historial

![Historial](screenshots/history.png)

### Ajustes

![Ajustes](screenshots/settings.png)

---

## 📦 Requisitos

* Android 8.0 (API 26) o superior.
* Google Play Services.
* Permisos de ubicación.
* Permisos de notificaciones.
* Permisos de calendario.

---

## 🚀 Instalación

### Clonar el repositorio

```bash
git clone https://github.com/Ing-MairaAlejandraRangel/smart-helper.git
```

### Ejecutar el proyecto

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Configurar la Google Maps API Key.
4. Ejecutar en un emulador o dispositivo físico compatible.

---

## ✅ Estado del proyecto

Proyecto funcional y probado en dispositivo Android físico.

Incluye:

* ✔ Creación de recordatorios
* ✔ Geo-recordatorios
* ✔ Geofencing
* ✔ Persistencia con Room Database
* ✔ Notificaciones locales
* ✔ Historial de actividades
* ✔ Configuración personalizada
* ✔ Reconocimiento de voz
* ✔ Integración con Google Maps

---

# 👩‍💻 Autora

**Maira Alejandra Rangel Murillo**
**Ingeniera de Sistemas**

Apasionada por el desarrollo de software y la creación de soluciones tecnológicas orientadas a la automatización, la gestión de datos y el desarrollo de aplicaciones web y móviles.

**Áreas de interés:**

* 💻 Desarrollo Full Stack
* ☕ Desarrollo Backend con Java y Spring Boot
* 🌐 Desarrollo Web con JavaScript y Node.js
* 📱 Desarrollo de aplicaciones Android
* 🗄️ Bases de Datos SQL (PostgreSQL y MySQL)
* 🔗 Diseño e implementación de APIs REST
* 🧪 Aseguramiento de la calidad (QA) y pruebas de software

Este proyecto fue desarrollado con fines académicos y forma parte de mi portafolio profesional como Ingeniera de Sistemas.
