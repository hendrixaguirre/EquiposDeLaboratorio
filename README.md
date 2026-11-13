
# Control de Equipos de Laboratorio

**Estudiante:** Hendrix Devadip Aguirre Zamora  
**Universidad:** Universidad Americana (UAM)  
**Asignatura:** Programación de Aplicaciones de Escritorio  
**Evaluación:** Corte III - Evaluación #1

## Descripción

Aplicación de escritorio para llevar el control de los equipos de un laboratorio universitario. Permite registrar, consultar, actualizar y eliminar equipos, además de buscarlos por nombre y filtrarlos por tipo.

## Tecnologías

- Java 21 y JavaFX (FXML)
- Hibernate y JPA
- PostgreSQL
- Maven e IntelliJ IDEA

## Base de datos

La base de datos `bd_laboratorio_orm` utiliza el esquema `catalogos` y contiene dos tablas:

- `tipo_equipo`: guarda el nombre y la descripción de cada tipo.
- `equipo`: guarda el código, nombre, marca, estado, cantidad y tipo del equipo.

La relación es de **uno a muchos (1:N)**: un tipo puede tener varios equipos. En Java se representa con `@OneToMany` y `@ManyToOne`.

## Funciones principales

- Guardar, actualizar y eliminar equipos.
- Mostrar los equipos registrados en una tabla.
- Buscar por parte del nombre y filtrar por tipo, incluso al mismo tiempo.
- Validar los campos y mostrar mensajes cuando ocurre un error.

Las operaciones se realizan con `EntityManager` y transacciones. Las búsquedas utilizan consultas JPQL con parámetros.

## Cómo ejecutar el proyecto

1. Abrir el proyecto en IntelliJ IDEA con JDK 21 y cargar las dependencias de Maven.
2. Crear `bd_laboratorio_orm` en PostgreSQL y ejecutar el script SQL del proyecto para crear las tablas e insertar los datos iniciales.
3. Revisar la conexión en `src/main/resources/META-INF/persistence.xml` (unidad `laboratorioPU`).
4. Configurar la variable de entorno `DB_PASSWORD` con la contraseña local de PostgreSQL.
5. Ejecutar la clase `LaboratorioApplication`.

## Organización

- `model`: entidades `Equipo` y `Tipo_Equipo`.
- `dao`: operaciones CRUD y consultas.
- `database`: clase `JPAUtil`.
- `controller`: eventos de la interfaz JavaFX.

## Pruebas

La evaluación contempla comprobar el registro, actualización y eliminación de equipos, el rechazo de códigos duplicados y cantidades negativas, las búsquedas y la conservación de los datos al reiniciar la aplicación. Las evidencias se documentan por separado en la matriz de pruebas.
