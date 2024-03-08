FROM openjdk:21-jdk-slim
ARG JAR_FILE=build/libs/*-all.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
