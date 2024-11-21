FROM maven:3.8.7-openjdk-18-slim AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /target/task-management.jar task-management.jar
EXPOSE 8074
ENTRYPOINT ["java","-jar","task-management.jar"]