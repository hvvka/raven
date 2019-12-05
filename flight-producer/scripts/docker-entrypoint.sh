#!/bin/sh

set -e

sh ../mount.sh

while( ! nc -z -v -w5 kafka 9092 )
do 
    sleep 5
    echo "waiting for kafka"
done

exec "$@"
