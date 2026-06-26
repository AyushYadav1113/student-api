# Build stage — Maven + JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Cache Maven dependencies separately from source code
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage — lightweight JRE only
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build --chown=spring:spring /build/target/student-api-*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
