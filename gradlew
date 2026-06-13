#!/bin/sh
GRADLE_WRAPPER_JAR_PATH="gradle/wrapper/gradle-wrapper.jar"
exec java -jar "$GRADLE_WRAPPER_JAR_PATH" "$@"
