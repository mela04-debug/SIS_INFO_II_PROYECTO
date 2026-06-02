# DevOps Collaboration Suite 🚀
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2022-red)](https://www.microsoft.com/sql-server)
[![Git](https://img.shields.io/badge/Git-Main-blue)](https://git-scm.com/)

Una suite de escritorio integrada desarrollada en **Java Swing** y conectada a **SQL Server**, diseñada para gestionar metodologías ágiles como *Pair Programming* y simular de extremo a extremo un entorno de **Integración Continua (CI/CD)**.

---

## 📌 Características Principales

### 1. Registro de Pair Programming & Administración
* **Gestión de Integrantes:** Permite registrar nuevos usuarios controlando restricciones de roles (`DESARROLLADOR`, `ADMINISTRADOR`) directamente validadas por la base de datos.
* **Control de Conducción:** Sistema dinámico que evita la selección del mismo usuario como conductor y copiloto simultáneamente.
* **Métricas del Administrador (KPI):** Panel analítico que calcula de manera exacta el tiempo real invertido por cada desarrollador (en horas y minutos) sumando las sesiones cerradas mediante cálculos nativos de tiempo.
* **Historial con Filtros Avanzados:** Buscador inteligente por rangos de fecha, tareas específicas o nombres de usuarios.

### 2. Entorno del Desarrollador (Simulador de IDE)
* Área de trabajo interactiva donde los programadores escriben código real.
* Opción de **Finalizar Sesión Abierta** (Pausar desarrollo y guardar tiempos de forma asíncrona).
* Opción de **Enviar Merge Request** (Mapeo automático de ramas de código como `feature-branch` hacia la rama estable `main`).

### 3. Servidor DevOps & Pipeline de Pruebas
* **Pipeline Automático:** Escaneo inteligente mediante hilos asíncronos (`Timer`) que analizan el código en busca de errores sintácticos (como estructuras de llaves `{}` rotas) o pruebas de seguridad.
* **Auditoría Histórica:** Registro estricto y transaccional en SQL Server que guarda el estado final (`EXITOSO` o `RECHAZADO`) junto al log de la consola en la tabla de validaciones.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 17+
* **Interfaz Gráfica:** Java Swing / AWT (con Look and Feel nativo del sistema)
* **Base de Datos:** Microsoft SQL Server
* **Conectividad:** JDBC (Java Database Connectivity) con persistencia transaccional (ACID).

---

## 📐 Arquitectura de la Base de Datos

El sistema se apoya en un modelo relacional robusto compuesto por las siguientes entidades en SQL Server:

* `usuario`: Almacena el personal con restricciones `CHECK` de rol corporativo.
* `tarea_modulo`: Módulos del sistema asignados a las sesiones de programación.
* `sesion_pair_programming`: Registra conductores, copilotos, tiempos (`hora_inicio`, `hora_fin`) y estados de la sesión.
* `merge_request`: Almacena el código fuente enviado y las ramas origen/destino.
* `historial_validacion`: Bitácora de auditoría de los resultados del pipeline DevOps.

---

## 🚀 Instalación y Configuración

### Prerrequisitos
1.  Tener instalado el **JDK 17** o superior.
2.  Tener una instancia de **SQL Server** activa.

### Configuración de la Base de Datos
1. Ejecuta el script de creación de tablas en tu manejador de SQL Server (SSMS).
2. Asegúrate de configurar las credenciales correctas en tu clase de conexión Java (`ConexionDB.java`):
   ```java
   String url = "jdbc:sqlserver://localhost:1433;databaseName=devops_suite;encrypt=true;trustServerCertificate=true;";
   String user = "tu_usuario";
   String password = "tu_password";
