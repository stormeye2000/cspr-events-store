FROM openjdk:17-jdk-slim

ARG GITHUB_APP_VERSION
ENV APP="producer-"$GITHUB_APP_VERSION".jar"

COPY ./build/libs/$APP /home

WORKDIR /home

CMD java -jar $APP --spring.profiles.active=dev
