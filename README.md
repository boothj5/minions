# Minions
XMPP chat bot, with plugin architecture for implementing commands.

# Overview
Minions runs as a bot in a XMPP MUC chat room, and responds to commands.  Commands are implemented as plugins, and may be loaded at runtime by dropping the JAR file in the plugins directory.

# Project structure

Folder | Contents
------ | --------
`minions-api` | The interface to be implemented by Minion plugins.
`minions-core` | The Minions chat bot.
`minions-contrib` | Example Minion plugins.

# Running Minions
First build Minions, from the project root run:

```
mvn clean install
```

Then create a YAML file with your configuration:

`user:`<br>
&nbsp;&nbsp;&nbsp;&nbsp;`name:` The user name for the account Minions should log in as.<br>
&nbsp;&nbsp;&nbsp;&nbsp;`resource:` The resource for login, `minions-core` by default.<br>
&nbsp;&nbsp;&nbsp;&nbsp;`password:` The password for the account.<br>
`service:`<br>
&nbsp;&nbsp;&nbsp;&nbsp;`server:` Optional server if not the same as domain part of the username.<br>
&nbsp;&nbsp;&nbsp;&nbsp;`port:` Optional port if not the default `5222`.<br>
`room:`<br>
&nbsp;&nbsp;&nbsp;&nbsp;`jid:` The JID of the room to join.<br>
&nbsp;&nbsp;&nbsp;&nbsp;`nick:` Nickname to use in the room, `minions` by default.<br>
&nbsp;&nbsp;&nbsp;&nbsp;`password:` Password for the room if required.<br>
`plugins:`<br>
&nbsp;&nbsp;&nbsp;&nbsp;`refreshSeconds:` Polling interval to check for new plugins, defaults to `10` seconds.<br>
&nbsp;&nbsp;&nbsp;&nbsp;`prefix:` The command prefix, defaults to `!`<br>
&nbsp;&nbsp;&nbsp;&nbsp;`dir:` The directory in which plugins are located, defaults to `~/.local/share/minions/plugins`<br>

An example can be found [here](https://github.com/boothj5/minions/blob/master/minions.yml)

Run the Jar file passing your configuration file as an argument:

```
java -jar minions-core/target/minions-core-1.0-SNAPSHOT.jar minions.yml
```

# Creating a plugin
Declare the Minions API as a dependency:

```xml
<dependency>
  <groupId>com.boothj5.minions</groupId>
  <artifactId>minions-api</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Using the `echo-minion` as an example,  extend the `Minion` class:

```java
package com.boothj5.minions.echo;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;

public class EchoMinion extends Minion {
  @Override
  public String getHelp() {
    return "[message] - Echo something.";
  }

  @Override
  public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
    String trimmed = message.trim();
    if ("".equals(trimmed)) {
      muc.sendMessage(from + " didn't say anything for me to echo");
    } else {
      muc.sendMessage(from + " said: " + trimmed);
    }
  }
}
```

The plugin needs to be packaged as a fat jar (with dependencies) and must include two Manifest attributes:

`MinionClass` - Implementation of the `Minion` interface.  
`MinionCommand` - The command name.

Example from `echo-minion`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>2.5.3</version>
  <configuration>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
    <archive>
      <manifest>
        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
      </manifest>
      <manifestEntries>
        <MinionClass>com.boothj5.minions.echo.EchoMinion</MinionClass>
        <MinionCommand>echo</MinionCommand>
      </manifestEntries>
    </archive>
  </configuration>
  <executions>
    <execution>
      <id>assemble-all</id>
      <phase>package</phase>
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Build the plugin:

```
mvn clean package
```

Copy the fat jar to the plugins directory, e.g. for the `echo-minion`:

```
cp target/echo-minion-1.0-SNAPSHOT-jar-with-dependencies.jar ~/.local/share/minions/plugins/.
```

The plugin will be available on the next refresh.

#Using Minions
When the Minions bot is present in the chat room, use the following to list available commands, (assuming the default prefix '!'):

```
!help
```

Example output:

```
22:17 - boothj5: !help
22:17 - minions:
        !help - Show this help.
        !status [url] - Get the http status code for a URL.
        !digest - Calculate various digests of a given value. Send 'help' for more information.
        !calc [expression] - Calculate result of evaluating expression.
        !apples give|take - Give or take an apple from the minion.
        !bin to|from [value] - Convert integer to binary, or binary to integer.
        !chatter [message] - Send a message to chatterbot.
        !echo [message] - Echo something.
        !props - Show OS system properties.
```

To list Jars that are currently loaded:

```
!jars
```

To execute a command, enter the command with the prefix, and any args required e.g.:

```
18:49 - boothj5: !status http://www.profanity.im
18:49 - minions: Status http://www.profanity.im: 200 - OK
```
