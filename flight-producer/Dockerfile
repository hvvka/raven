FROM hirokimatsumoto/alpine-openjdk-11

COPY . .

RUN apk update && \
    apk add --update netcat-openbsd && \
    apk add sshfs
RUN ./gradlew bootJar
RUN chmod +x ./scripts/docker-entrypoint.sh
RUN chmod +x ./mount.sh

ENTRYPOINT ["./scripts/docker-entrypoint.sh"]

CMD ["java", "-jar" ,"build/libs/flight-producer-0.0.1-SNAPSHOT.jar"]
