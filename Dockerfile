FROM openjdk:8-jdk

EXPOSE 5678

COPY target/traffic-0.0.1-SNAPSHOT-jar-with-dependencies.jar /opt/

COPY start_server.sh /opt/

RUN chmod a+rx /opt/start_server.sh

ENTRYPOINT ["/opt/start_server.sh"]