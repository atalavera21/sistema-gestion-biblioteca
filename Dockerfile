# =============================================================================
# Dockerfile - Biblioteca SENATI
#
# Imagen en 2 etapas:
#   Etapa 1 (build)  -> compila el proyecto con Maven y genera el .war
#   Etapa 2 (wildfly) -> toma ese .war, lo despliega en WildFly y configura
#                        el datasource de MySQL.
#
# Las credenciales NO estan en el codigo: llegan como build args desde el
# archivo .env (ver docker-compose.yml).
# =============================================================================

# ---- Etapa 1: compilar el WAR ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY sistema-biblioteca/pom.xml .
COPY sistema-biblioteca/src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: WildFly + la aplicacion ----
FROM quay.io/wildfly/wildfly:35.0.1.Final-jdk17

# Credenciales de la BD (inyectadas en build; ver docker-compose.yml -> build.args)
ARG DB_USER
ARG DB_PASSWORD

# Driver JDBC de MySQL (lo necesita WildFly para el datasource)
ADD --chown=jboss:jboss \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar \
    /tmp/mysql-connector-j.jar

# Registrar el driver y crear el datasource "bibliotecaDS".
# El script trae marcadores (__DB_USER__, __DB_PASSWORD__) que se reemplazan
# aqui con los valores reales antes de ejecutarlo.
COPY --chown=jboss:jboss docker/datasource.cli /tmp/datasource.cli
RUN sed -i "s|__DB_USER__|${DB_USER}|g; s|__DB_PASSWORD__|${DB_PASSWORD}|g" /tmp/datasource.cli \
 && /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/datasource.cli

# Desplegar el .war compilado en la etapa 1.
# Se renombra a ROOT.war para que la app quede en la raiz ("/") y no en
# "/sistema-gestion-biblioteca". Asi la URL no lleva ese segmento.
COPY --chown=jboss:jboss --from=build \
    /build/target/sistema-gestion-biblioteca.war \
    /opt/jboss/wildfly/standalone/deployments/ROOT.war
