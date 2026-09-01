# Stage 1: Build JAR with Maven and OpenJDK 17
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal JRE 17 runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create upload directory volume
RUN mkdir -p /app/uploads

# Copy built jar from build stage
COPY --from=build /app/target/career-match-backend-*.jar app.jar

ENV UPLOAD_DIR=/app/uploads
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
