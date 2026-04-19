# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV PORT=8080
ENTRYPOINT ["java", \
  "-Dserver.port=${PORT}", \
  "-Dspring.datasource.url=jdbc:postgresql://dpg-d7il9unlk1mc73a0j5k0-a:5432/artclass_db", \
  "-Dspring.datasource.username=artclass_db_user", \
  "-Dspring.datasource.password=7NPBEunEoMdyvGyHP9evhrmWZLNXCXIZ", \
  "-Dspring.datasource.driver-class-name=org.postgresql.Driver", \
  "-Dspring.jpa.hibernate.ddl-auto=update", \
  "-jar", "app.jar"]