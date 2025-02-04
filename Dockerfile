#Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean test package

# Package stage
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/*.jar app.jar
ENV SPRING_H2_CONSOLE_SETTINGS_WEB_ALLOW_OTHERS=true
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar","-web -webAllowOthers -tcp -tcpAllowOthers -browser"]