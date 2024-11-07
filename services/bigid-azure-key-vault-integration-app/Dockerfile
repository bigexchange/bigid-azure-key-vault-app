FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-alpine

COPY target/bigid-azure-key-vault-integration-app-2.0.176-SNAPSHOT.jar /usr/src/app.jar
EXPOSE 8083
CMD ["java","-jar","/usr/src/app.jar"]
