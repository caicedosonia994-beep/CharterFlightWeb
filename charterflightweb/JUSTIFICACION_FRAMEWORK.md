# Justificación del Framework y Arquitectura

## Framework y Tecnologías Utilizadas

### Maven

**Justificación:** Maven fue elegido como herramienta de construcción y gestión de dependencias porque:

- **Gestión automática de dependencias:** Resuelve y descarga automáticamente las bibliotecas necesarias (Servlet API, MySQL Connector, JSTL) mediante el archivo `pom.xml`, evitando conflictos de versiones y eliminando la necesidad de incluir JARs manualmente.
- **Convenciones de estructura de directorios:** Define una estructura estándar (`src/main/java`, `src/main/webapp`, `src/main/resources`) que garantiza consistencia y facilidad de mantenimiento.
- **Ciclo de vida de construcción:** Permite compilar, testear y empaquetar el proyecto (`mvn clean package`) con comandos únicos, lo que facilita la integración continua.
- **Portabilidad:** El archivo `pom.xml` es portable entre sistemas operativos y entornos de desarrollo, garantizando que cualquier desarrollador puede construir el proyecto con un solo comando.

### JSP (JavaServer Pages)

**Justificación:** JSP fue seleccionado como tecnología de presentación porque:

- **Separación de preocupaciones:** Permite separar la lógica de presentación (HTML + Tailwind CSS) de la lógica de negocio (Servlets), facilitando el trabajo del equipo de UX/UI.
- **Expression Language (EL):** Permite acceder a atributos del request (`${cliente.nombre}`) de forma declarativa, sin necesidad de código Java embebido.
- **JSTL (JSP Standard Tag Library):** Proporciona etiquetas estándar como `<c:forEach>` y `<c:if>` para iterar y condicionar la presentación de datos, manteniendo las JSP limpias de scriptlets.
- **Compatibilidad con Tomcat 8.5:** JSP 2.3 es compatible con Tomcat 8.5, ofreciendo una solución estable y probada en el ecosistema empresarial Java.

### Servlets

**Justificación:** Los Servlets son el controlador de la arquitectura MVC porque:

- **Punto central de entrada:** `ClienteServlet` recibe todas las peticiones HTTP (GET y POST), actuando como controlador único que orquesta la lógica de negocio.
- **Manejo de peticiones:** Implementa `doGet()` y `doPost()` para diferenciar operaciones de lectura (listar, buscar, editar, eliminar) de operaciones de escritura (registrar, actualizar).
- **Pattern POST-redirect-GET:** Al usar `sendRedirect()` después de operaciones de escritura, previene reenvíos duplicados al refrescar la página.
- **Integración con JDBC:** Los Servlets delegan operaciones de base de datos al DAO, manteniendo un bajo acoplamiento.

### JDBC (Java Database Connectivity)

**Justificación:** JDBC fue elegido como capa de acceso a datos porque:

- **Control total sobre las consultas:** Permite escribir SQL exactamente como se necesita, sin capas de abstracción innecesarias.
- **PreparedStatement:** Todas las consultas utilizan `PreparedStatement` con parámetros (`?`), lo que:
  - Previene ataques de inyección SQL.
  - Mejora el rendimiento mediante el precompilado de consultas.
- **Gestión manual de conexiones:** La clase `Conexion` centraliza la obtención y cierre de recursos (`Connection`, `PreparedStatement`, `ResultSet`), evitando fugas de memoria.
- **Sin dependencia de framework ORM:** Mantiene el proyecto ligero sin dependencias adicionales como Hibernate, ideal para proyectos de tamaño mediano.

### Arquitectura MVC (Model-View-Controller)

**Justificación:** La arquitectura MVC fue adoptada porque:

- **Modelo (Model):** `Cliente.java` (POJO/entidad) y `ClienteDAO.java` (acceso a datos) encapsulan la lógica y datos del negocio.
- **Vista (View):** Las páginas JSP (`listaclientes.jsp`, `registrarclientes.jsp`) son responsables únicamente de la presentación, usando JSTL y EL para mostrar datos dinámicos.
- **Controlador (Controller):** `ClienteServlet.java` recibe las peticiones, valida datos, delega al DAO y prepara los datos para la vista.
- **Mantenibilidad:** Cada capa tiene responsabilidades bien definidas, facilitando el testing, el debugging y el crecimiento del sistema.
- **Separación de roles:** Los diseñadores pueden trabajar en las JSP sin tocar la lógica Java, y los desarrolladores pueden modificar el DAO sin afectar la interfaz.

### MySQL + XAMPP

**Justificación:** MySQL fue seleccionado como sistema de base de datos porque:

- **Ligereza y rendimiento:** Adecuado para aplicaciones de mediano tamaño sin necesidad de servidores de base de datos robustos.
- **Facilidad de uso con XAMPP:** XAMPP proporciona un entorno todo-en-uno (Apache + MySQL + PHP) que simplifica el desarrollo local.
- **Amplia documentación y comunidad:** Gran cantidad de recursos y ejemplos disponibles.

## Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                      Navegador                          │
│        (HTML + Tailwind CSS + JavaScript)              │
└──────────────────┬──────────────────────────────────────┘
                   │ HTTP (GET/POST)
                   ▼
┌─────────────────────────────────────────────────────────┐
│                  ClienteServlet                         │
│                    (Controller)                         │
│  doGet()  → listar, buscar, editar, eliminar            │
│  doPost() → registrar, actualizar                       │
├─────────────────────────────────────────────────────────┤
│  Validación  │  Mensajes Flash  │  Forward/Redirect     │
└──────────────┼──────────────────┼───────────────────────┘
               │                  │
               ▼                  ▼
┌────────────────────────┐  ┌──────────────────────────┐
│    ClienteDAO          │  │          JSP             │
│    (Model/Data)        │  │        (View)            │
│                        │  │                          │
│  insertar()            │  │  listaclientes.jsp       │
│  listar()              │  │  registrarclientes.jsp   │
│  buscar()              │  │  index.jsp               │
│  buscarPorId()         │  │  (JSTL + EL)             │
│  actualizar()          │  │                          │
│  eliminar()            │  └──────────────────────────┘
│  existeDocumento()     │
└──────────┬─────────────┘
           │ JDBC (PreparedStatement)
           ▼
┌─────────────────────────────────────────────────────────┐
│                   Conexion.java                         │
│              (Connection Pool Manager)                  │
└──────────┬──────────────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────┐
│                     MySQL                               │
│              Base de datos: charterflight               │
│              Tabla: clientes                            │
└─────────────────────────────────────────────────────────┘
```
