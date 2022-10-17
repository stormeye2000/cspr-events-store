FROM gradle:jdk11 as gradleimage
RUN ls .
RUN pwd
COPY build/libs/cspr-events-producer-0.0.1-SNAPSHOT.jar /home
WORKDIR /home

RUN ls -ltra
RUN pwd

CMD ["java", "-jar", "cspr-events-producer-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=dev"]
