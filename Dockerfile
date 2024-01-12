FROM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/MyChatApp-0.0.1-SNAPSHOT.jar /demo.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/demo.jar"]
