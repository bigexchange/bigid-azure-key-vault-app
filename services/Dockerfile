FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-jammy

COPY target/bigid-azure-key-vault-integration-app-*-SNAPSHOT.jar /usr/src/app.jar

EXPOSE 8083

CMD ["java", "-jar", "/usr/src/app.jar"]
