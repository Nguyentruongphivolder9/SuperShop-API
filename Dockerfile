FROM openjdk:17
ARG JAR_FILE="/target/*.jar"
COPY ${JAR_FILE} supershop.jar
ENTRYPOINT ["java", "-jar", "supershop.jar"]