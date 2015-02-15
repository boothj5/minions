#!/bin/sh
mvn package && java \
    -Dminions.user.name=commandbot \
    -Dminions.user.service=ejabberd.local \
    -Dminions.user.resource=daemon \
    -Dminions.user.password=password \
    -Dminions.service.server=localhost \
    -Dminions.service.port=5242 \
    -Dminions.room.jid=botroom@conference.ejabberd.local \
    -Dminions.room.nick=minions \
    -jar target/minions-core-1.0-SNAPSHOT.jar

