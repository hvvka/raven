#!/bin/sh

dir=$(pwd)

fusermount -u "$dir"/input
fusermount -u "$dir"/output
fusermount -u "$dir"/logs
