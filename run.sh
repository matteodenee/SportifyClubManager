#!/usr/bin/env bash
set -e
mvn -q -DskipTests exec:java -Dexec.mainClass=com.sportify.manager.communication.network.ChatServerApp &
SERVER_PID=$!
sleep 1
mvn -q -DskipTests javafx:run
kill $SERVER_PID
