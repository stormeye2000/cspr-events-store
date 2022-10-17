FROM openjdk:17-jdk-slim
RUN ls /home/runner/work/cspr-events-store/cspr-events-store -ltra

COPY /home/runner/work/cspr-events-store/cspr-events-store/build/libs/cspr-events-producer-0.0.1-SNAPSHOT.jar /home

WORKDIR /home


CMD ["java", "-jar", "cspr-events-producer-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=dev"]
