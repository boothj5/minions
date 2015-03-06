#!/bin/sh
mvn clean package 1>/dev/null 2>&1 && nohup java \
    -Dminions.user.name=stripbot \
    -Dminions.user.service=dukgo.com \
    -Dminions.user.resource=macmini \
    -Dminions.user.password=l@kitul@kitu \
    -Dminions.room.jid=whatsnotapp@conference.jabber.org \
    -Dminions.room.nick=minions \
    -Dminions.room.password=1r0nm41d3n \
    -Dminions.refresh.seconds=10 \
    -jar target/minions-core-1.0-SNAPSHOT.jar 1>/dev/null 2>&1 &

