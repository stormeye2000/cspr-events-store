FROM openjdk:11-jdk-slim

ARG GITHUB_APP_VERSION
RUN echo $GITHUB_APP_VERSION

COPY ./build/libs/producer-0.0.1.jar /home
WORKDIR /home
RUN ls -ltra
CMD ["java", "-jar", "producer-0.0.1.jar", "--spring.profiles.active=dev"]
