FROM gradle:jdk11 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN ./gradlew clean build -x test

CMD ["java", "-jar", "build/libs/producer.jar", "--spring.profiles.active=dev"]
