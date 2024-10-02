FROM openjdk:latest
COPY ./target/devopsethods-0.1.0.2-jar-with-dependencies.jar /tmp
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "devopsethods-0.1.0.2-jar-with-dependencies.jar"]