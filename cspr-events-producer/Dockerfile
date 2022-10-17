FROM openjdk:17-jdk-slim
COPY ./build/libs/producer.jar /home
WORKDIR /home
CMD ["java", "-jar", "producer.jar", "--spring.profiles.active=dev"]
