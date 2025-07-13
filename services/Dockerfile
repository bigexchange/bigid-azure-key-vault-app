FROM cgr.dev/bigid.com/jre:openjdk-17

COPY target/bigid-azure-key-vault-integration-*-SNAPSHOT.jar /usr/src/app.jar

EXPOSE 8083

CMD ["java", "-jar", "/usr/src/app.jar"]
