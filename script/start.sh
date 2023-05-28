#!/bin/bash

SHELL_FOLDER=$(dirname $(readlink -f "$0"))
JAVA_JAR_NAME=$(ls "$SHELL_FOLDER" | grep "aliyun-ddns-java")
JAVA_JAR_PATH="$SHELL_FOLDER/$JAVA_JAR_NAME"

PID=$(pgrep -f "$JAVA_JAR_PATH")
if [ -n "$PID" ]; then
    echo "Process[$PID] is already running."
    exit 0
fi

nohup java -jar "$JAVA_JAR_PATH" >/dev/null 2>&1 &
PID=$(pgrep -f "$JAVA_JAR_PATH")
echo "Process[$PID] started successfully."