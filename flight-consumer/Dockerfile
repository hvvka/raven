FROM hirokimatsumoto/alpine-openjdk-11

COPY . .

RUN apk update && apk add --update netcat-openbsd
RUN ./gradlew bootJar
RUN chmod +x ./scripts/docker-entrypoint.sh

ENTRYPOINT ["./scripts/docker-entrypoint.sh"]

CMD ["java", "-jar" ,"build/libs/flight-consumer-0.0.1-SNAPSHOT.jar"]
