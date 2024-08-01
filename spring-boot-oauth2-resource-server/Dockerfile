FROM maven:3.8.4-openjdk-17-slim
COPY ./src ./src
COPY pom.xml ./
RUN mvn clean package -DskipTests
ENTRYPOINT java -jar ./target/*.jar