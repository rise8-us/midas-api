FROM registry1.dso.mil/ironbank/google/distroless/java-11:nonroot

USER 1001

COPY build/libs/midasApi-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-jar", "/app/midasApi-0.0.1-SNAPSHOT.jar"]
