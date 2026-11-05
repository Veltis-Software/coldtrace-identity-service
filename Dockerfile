# syntax=docker/dockerfile:1.7
FROM maven:3.9.16-eclipse-temurin-26 AS build
WORKDIR /build
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src/ src/
RUN ./mvnw -B -DskipTests package
FROM eclipse-temurin:26-jre-noble AS runtime
WORKDIR /app
RUN useradd --system --uid 10001 coldtrace
COPY --from=build --chown=10001:10001 /build/target/*.jar app.jar
USER 10001
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
