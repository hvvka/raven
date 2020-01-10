#!/bin/bash

dir=$(pwd)

sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no hania@23.101.135.177:/home/hania/logs "$dir"/logs

echo "Logs folder is mounted to ${dir}/logs"
