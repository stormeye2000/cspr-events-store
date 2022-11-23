./gradlew --stop
rm -rf ~/.gradle/daemon/7.5
pkill -f '.*GradleDaemon.*'
./gradlew --status