FROM registry.il2.dso.mil/platform-one/devops/pipeline-templates/ironbank/redhat-openjdk11:1.11

USER 1001

COPY build/libs/mixerApi-0.0.1-SNAPSHOT.jar /app/

ENTRYPOINT ["java", "-jar", "/app/mixerApi-0.0.1-SNAPSHOT.jar"]
