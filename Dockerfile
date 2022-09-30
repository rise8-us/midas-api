FROM registry1.dso.mil/ironbank/redhat/openjdk/openjdk11:1.11

USER 1001

COPY build/libs/midasApi-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-Dcom.redhat.fips=false", "-jar", "/app/midasApi-0.0.1-SNAPSHOT.jar"]
