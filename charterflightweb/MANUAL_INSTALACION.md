# Manual de Instalación — Charter Flight Web

## Requisitos previos

| Software | Versión mínima |
|---|---|
| Java (JDK) | 21 |
| Apache Maven | 3.8+ |
| Apache Tomcat | 8.5 |
| MySQL | 5.7+ (o MariaDB 10.3+) |
| XAMPP | 8.0+ (opcional, incluye MySQL y Tomcat) |

---

## 1. Instalar Java

### Windows

1. Descargar JDK 21 desde [https://adoptium.net](https://adoptium.net).
2. Ejecutar el instalador.
3. Verificar:

```cmd
java -version
```

4. Configurar variable de entorno `JAVA_HOME`:

```
JAVA_HOME = C:\Program Files\Temurin\jdk-21
Path += %JAVA_HOME%\bin
```

### Linux

```bash
sudo apt install openjdk-21-jdk
java -version
```

---

## 2. Instalar Maven

### Windows

1. Descargar desde [https://maven.apache.org/download](https://maven.apache.org/download).
2. Extraer a `C:\apache-maven-3.x`.
3. Configurar variable de entorno `MAVEN_HOME` y agregar a `Path`.

### Linux

```bash
sudo apt install maven
mvn -version
```

---

## 3. Configurar Tomcat

### Opción A: Usar Tomcat standalone

1. Descargar Apache Tomcat 8.5 desde [https://tomcat.apache.org](https://tomcat.apache.org).
2. Extraer a una carpeta (ej. `C:\apache-tomcat-8.5.x`).
3. Iniciar:

```cmd
cd C:\apache-tomcat-8.5.x\bin
startup.bat
```

4. Verificar: abrir `http://localhost:8080` en el navegador.

### Opción B: Usar XAMPP (incluye Tomcat)

1. Iniciar XAMPP Control Panel.
2. Iniciar servicios: `MySQL` y `Tomcat`.
3. Verificar: `http://localhost:8080` y `http://localhost:8080/phpMyAdmin`.

---

## 4. Importar la base de datos

1. Iniciar MySQL (vía XAMPP o servicio).
2. Ejecutar el script SQL:

```bash
mysql -u root -p < charterflightweb/src/main/resources/charterflight.sql
```

3. Verificar:

```bash
mysql -u root -p -e "USE charterflight; SHOW TABLES; DESCRIBE clientes;"
```

La tabla `clientes` debe tener las columnas: `id_cliente`, `nombre`, `apellido`, `documento`, `telefono`, `correo`.

---

## 5. Compilar con Maven

```bash
cd charterflightweb
mvn clean package
```

El WAR se genera en: `target/charterflightweb.war`

---

## 6. Desplegar el WAR en Tomcat

### Opción A: Copiar WAR manualmente

```bash
cp target/charterflightweb.war $TOMCAT_HOME/webapps/
```

### Opción B: Usar XAMPP

1. Copiar `target/charterflightweb.war` a `C:\xampp\tomcat\webapps\`.
2. Reiniciar Tomcat desde XAMPP Control Panel.

---

## 7. Ejecutar la aplicación

Abrir en el navegador:

```
http://localhost:8080/charterflightweb/
```

### Rutas principales

| Funcionalidad | URL |
|---|---|
| Página de inicio | `http://localhost:8080/charterflightweb/` |
| Lista de clientes | `http://localhost:8080/charterflightweb/ClienteServlet?accion=listar` |
| Registrar cliente | `http://localhost:8080/charterflightweb/ClienteServlet?accion=editar&id=0` |
| Buscar clientes | `http://localhost:8080/charterflightweb/ClienteServlet?accion=buscar&termino=Juan` |
| Editar cliente | `http://localhost:8080/charterflightweb/ClienteServlet?accion=editar&id=1` |
| Eliminar cliente | `http://localhost:8080/charterflightweb/ClienteServlet?accion=eliminar&id=1` |

---

## Solución de problemas

### Error de conexión a MySQL

- Verificar que MySQL esté corriendo: `xampp\mysql\bin\mysqladmin -u root status`
- Verificar usuario/contraseña en `Conexion.java` (por defecto: `root` / vacío)
- Verificar que el firewall permita conexiones al puerto 3306

### Error 404 al acceder a la aplicación

- Verificar que el WAR esté en la carpeta `webapps/` de Tomcat
- Verificar que Tomcat esté corriendo en el puerto 8080
- Verificar el nombre del WAR: `charterflightweb.war`

### Error de compilación Maven

- Verificar que `JAVA_HOME` apunte a JDK 21
- Verificar conexión a internet (para descargar dependencias)
- Limpiar caché: `mvn clean compile -U`

### La tabla `clientes` no existe

- Ejecutar nuevamente el script SQL: `mysql -u root -p < charterflight.sql`
- Verificar que la base de datos se llame exactamente `charterflight`
