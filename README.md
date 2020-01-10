# raven

[![Build Status](https://travis-ci.com/hvvka/raven.svg?token=AtJu5RATvaNahLGCYye5&branch=master)](https://travis-ci.com/hvvka/raven)

Sequel to [innovative-project-jackdaw](https://github.com/nokia-wroclaw/innovative-project-jackdaw), but with Spring Kafka and other cool things.

```bash
$ docker-compose up -d
$ cd flight-producer
$ ./mount.sh && ./gradlew bootRun &
$ cd ../flight-consumer && ./gradlew bootRun & 
```
