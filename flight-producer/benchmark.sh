#!/bin/bash

echo "WARNING: To run this benchmark, first remove all occurences of Thread.sleep() from producer code and let application close after sending all records"

docker-compose up -d 2> /dev/null
rm input/* 2> /dev/null
cp test_input/1000.csv input/
echo "TESTING: 1 000 RECORDS"
time ./gradlew bootRun > /dev/null
docker-compose down

docker-compose up -d 2> /dev/null
rm input/*
cp test_input/10000.csv input/
echo "TESTING: 10 000 RECORDS"
time ./gradlew bootRun > /dev/null
docker-compose down

docker-compose up -d 2> /dev/null
rm input/*
cp test_input/100000.csv input/
echo "TESTING: 100 000 RECORDS"
time ./gradlew bootRun > /dev/null
docker-compose down

docker-compose up -d 2> /dev/null
rm input/*
cp test_input/1000000.csv input/
echo "TESTING: 1 000 000 RECORDS"
time ./gradlew bootRun > /dev/null
docker-compose down

rm input/*
