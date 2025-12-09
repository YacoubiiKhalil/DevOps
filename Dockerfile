FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY student-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
