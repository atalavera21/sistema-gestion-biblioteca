# =============================================================================
# Dockerfile - Biblioteca SENATI
#
# Imagen en 2 etapas:
#   Etapa 1 (build)  -> compila el proyecto con Maven y genera el .war
#   Etapa 2 (wildfly) -> toma ese .war, lo despliega en WildFly y configura
#                        el datasource de MySQL.
#
# El resultado es UNA imagen lista para correr la aplicacion.
# =============================================================================

# ---- Etapa 1: compilar el WAR ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY sistema-biblioteca/pom.xml .
COPY sistema-biblioteca/src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: WildFly + la aplicacion ----
FROM quay.io/wildfly/wildfly:35.0.1.Final-jdk17

# Driver JDBC de MySQL (lo necesita WildFly para el datasource)
ADD --chown=jboss:jboss \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar \
    /tmp/mysql-connector-j.jar

# Registrar el driver y crear el datasource "bibliotecaDS" dentro de la imagen
COPY docker/datasource.cli /tmp/datasource.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/datasource.cli

# Desplegar el .war compilado en la etapa 1
COPY --chown=jboss:jboss --from=build \
    /build/target/sistema-gestion-biblioteca.war \
    /opt/jboss/wildfly/standalone/deployments/
