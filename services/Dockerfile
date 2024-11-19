FROM cgr.dev/bigid.com/adoptium-jre:adoptium-openjdk-17

COPY target/bigid-azure-key-vault-integration-app-2.0.176-SNAPSHOT.jar /usr/src/app.jar

EXPOSE 8083

CMD ["java", "-jar", "/usr/src/app.jar"]
