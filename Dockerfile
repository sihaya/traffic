FROM openjdk:8-jdk

EXPOSE 5678

COPY target/traffic-0.0.1-SNAPSHOT-jar-with-dependencies.jar /opt/traffic-0.0.1-SNAPSHOT-jar-with-dependencies.jar

ENTRYPOINT ["java", "-jar", "/opt/traffic-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "5678"]