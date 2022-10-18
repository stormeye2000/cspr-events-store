FROM openjdk:17-jdk-slim

ARG GITHUB_APP_VERSION
ENV APP="consumer-audit-"$GITHUB_APP_VERSION".jar"

COPY ./build/libs/$APP /home

WORKDIR /home

CMD java -jar $APP --spring.profiles.active=dev
