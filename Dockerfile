FROM registry.il2.dso.mil/platform-one/devops/pipeline-templates/base-image/harden-openjdk11-jre:11.0.9

USER appuser

COPY build/libs/midasApi-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-jar", "/app/midasApi-0.0.1-SNAPSHOT.jar"]
