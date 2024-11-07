FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-alpine

COPY target/basic-demo-app-2.0.176-SNAPSHOT.jar /usr/src/basicapp.jar
EXPOSE 8083
CMD ["java","-jar","/usr/src/basicapp.jar"]
