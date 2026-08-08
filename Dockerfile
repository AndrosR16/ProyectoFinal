# syntax=docker/dockerfile:1

# ---- Stage 1: build ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copiamos primero el wrapper y el pom para aprovechar el cache de capas de Docker:
# mientras no cambien las dependencias, esta capa no se vuelve a descargar.
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Ahora sí copiamos el código fuente y empaquetamos.
COPY src src
RUN ./mvnw package -DskipTests -B

# ---- Stage 2: runtime ----
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Usuario no-root para ejecutar la aplicación.
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring

EXPOSE 8080

# JAVA_OPTS permite ajustar memoria/flags desde la plataforma sin tocar la imagen.
# El puerto lo resuelve Spring (server.port=${PORT:8080} en application-prod.properties),
# no se fuerza aquí con -Dserver.port para que funcione igual en cualquier proveedor.
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
