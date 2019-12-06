#!/bin/bash

dir=$(pwd)

sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no pdzd@40.118.42.162:/home/pdzd/input "$dir"/input
sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no pdzd@40.118.42.162:/home/pdzd/output "$dir"/output
sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no pdzd@40.118.42.162:/home/pdzd/logs "$dir"/logs

echo "Input data folder is mounted to ${dir}/input"
echo "Output data folder is mounted to ${dir}/output"
echo "Logs folder is mounted to ${dir}/logs"
