
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# copiamos los archivos de configuración de Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# descargamos las dependencias
RUN ./mvnw dependency:go-offline -B

# copiamos el código fuente y compilamos
COPY src src
RUN ./mvnw clean package -DskipTests

# ejecustamos la aplicación 
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# copiamos el jar compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# creamos un comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]