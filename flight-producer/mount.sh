#!/bin/bash

dir=$(pwd)

sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no hania@23.101.135.177:/home/hania/input "$dir"/input
sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no hania@23.101.135.177:/home/hania/output "$dir"/output
sshfs -o allow_other -o cache=yes -o kernel_cache -o compression=no hania@23.101.135.177:/home/hania/logs "$dir"/logs

echo "Input data folder is mounted to ${dir}/input"
echo "Output data folder is mounted to ${dir}/output"
echo "Logs folder is mounted to ${dir}/logs"
