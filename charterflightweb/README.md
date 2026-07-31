# Charter Flight Web

## Descripción

Sistema de gestión de reservas de vuelos charter de lujo. Permite registrar, listar, buscar, editar y eliminar clientes VIP, conectados a una base de datos MySQL mediante JDBC con PreparedStatement (prevención de inyección SQL).

## Tecnologías utilizadas

- **Java 21** — Lenguaje de programación
- **Maven** — Gestión de dependencias y construcción
- **Apache Tomcat 8.5** — Servidor de aplicaciones web
- **JSP (Jakarta Servlet Pages 2.3)** — Vistas
- **Servlets (javax.servlet 4.0)** — Controladores
- **JSTL 1.2** — Etiquetas JSP para lógica de presentación
- **Expression Language (EL)** — Acceso a datos en JSP
- **JDBC** — Conexión a base de datos
- **MySQL** — Base de datos relacional
- **XAMPP** — Entorno de desarrollo local (MySQL + Tomcat)
- **Tailwind CSS** — Framework de estilos (interfaz ya implementada)
- **Font Awesome** — Íconos

## Arquitectura MVC

```
Vista (JSP)          ←→   Controlador (Servlet)      ←→   Modelo (DAO + POJO)
listaclientes.jsp         ClienteServlet                 ClienteDAO
registrarclientes.jsp     (doGet / doPost)               Cliente.java
index.jsp                 (request.getParameter)         Conexion.java
```

### Flujo de petición

1. El usuario interactúa con una JSP (formulario, enlace).
2. La JSP envía la petición al `ClienteServlet` vía HTTP GET o POST.
3. El `ClienteServlet` recibe la petición, extrae parámetros con `request.getParameter()`.
4. El servlet delega la operación al `ClienteDAO`, que ejecuta queries con `PreparedStatement`.
5. El DAO retorna resultados al servlet, que los coloca como atributos en el `request`.
6. El servlet hace `forward` a la JSP correspondiente para renderizar la vista.

## Estructura del proyecto

```
charterflightweb/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/charterflight/
│   │   │       ├── conexion/
│   │   │       │   └── Conexion.java
│   │   │       ├── dao/
│   │   │       │   └── ClienteDAO.java
│   │   │       ├── modelo/
│   │   │       │   └── Cliente.java
│   │   │       └── servlet/
│   │   │           └── ClienteServlet.java
│   │   ├── resources/
│   │   │   └── charterflight.sql
│   │   └── webapp/
│   │       ├── index.jsp
│   │       ├── jsp/
│   │       │   ├── listaclientes.jsp
│   │       │   ├── registrarclientes.jsp
│   │       │   ├── dashboard.jsp
│   │       │   ├── flightmanagement.jsp
│   │       │   └── bookingmanagement.jsp
│   │       ├── css/
│   │       │   └── styles.css
│   │       └── WEB-INF/
│   │           └── web.xml
└── target/
```

## Funcionalidades CRUD de Clientes

| Operación | URL | Método |
|---|---|---|
| Listar clientes | `/ClienteServlet?accion=listar` | GET |
| Buscar clientes | `/ClienteServlet?accion=buscar&termino=xxx` | GET |
| Mostrar formulario registro | `/ClienteServlet?accion=editar&id=0` | GET |
| Registrar cliente | `/ClienteServlet` (form POST) | POST |
| Mostrar formulario edición | `/ClienteServlet?accion=editar&id=X` | GET |
| Actualizar cliente | `/ClienteServlet` (form POST) | POST |
| Eliminar cliente | `/ClienteServlet?accion=eliminar&id=X` | GET |

## Validaciones

- Campos obligatorios: nombre, apellido, documento, teléfono, correo
- Formato de correo válido (regex)
- Documento único (consulta previa a inserción/actualización)
- Teléfono: solo números, espacios, `+` y `-`

## Mensajes

- Mensajes de éxito/error se muestran mediante flash scope (HttpSession)
- Errores de validación se muestran en el formulario de registro

## Instrucciones para ejecutar el proyecto

Ver [MANUAL_INSTALACION.md](MANUAL_INSTALACION.md) para instrucciones detalladas.

### Resumen rápido

```bash
# 1. Iniciar XAMPP (MySQL)
# 2. Importar base de datos
mysql -u root -p < src/main/resources/charterflight.sql
# 3. Compilar y empaquetar
mvn clean package
# 4. Copiar WAR a Tomcat webapps/
cp target/charterflightweb.war $TOMCAT_HOME/webapps/
# 5. Iniciar Tomcat
$TOMCAT_HOME/bin/startup.sh
# 6. Acceder a la aplicación
http://localhost:8080/charterflightweb/ClienteServlet?accion=listar
```
