#!/bin/sh
mvn package && java \
    -Dcommandbot.user.name=commandbot \
    -Dcommandbot.user.service=ejabberd.local \
    -Dcommandbot.user.resource=daemon \
    -Dcommandbot.user.password=password \
    -Dcommandbot.service.server=localhost \
    -Dcommandbot.service.port=5242 \
    -Dcommandbot.room.jid=botroom@conference.ejabberd.local \
    -Dcommandbot.room.nick=commandbot \
    -jar target/commandbot-core-1.0-SNAPSHOT.jar

