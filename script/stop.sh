#!/bin/bash

SHELL_FOLDER=$(dirname $(readlink -f "$0"))
JAVA_JAR_NAME=$(ls "$SHELL_FOLDER" | grep "aliyun-ddns-java")
JAVA_JAR_PATH="$SHELL_FOLDER/$JAVA_JAR_NAME"

PID=$(pgrep -f "$JAVA_JAR_PATH")
if [ -z "$PID" ]; then
    echo "No process running."
    exit 0
fi
kill -9 "$PID"
echo "Stop PID[$PID] success."