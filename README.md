# Minions
XMPP chat bot, with plugin architecture for implementing commands.

# Overview
Minions runs as a bot in a XMPP MUC chat room, and responds to commands.  Commands are implemented as plugins, and may be loaded at runtime by dropping the JAR file in the plugins directory.

# Project structure
`minions-api`: The interface to be implemented by Minion plugins.

`minions-core`: The Minions chat bot.

`minions-contrib`: Example Minion plugins.

# Running Minions
To run Minions, first build and locally install the Minions API using Maven, from the `minions-api` directory:


```
mvn clean install
```

Edit the `runMinions.sh` script in the `minions-core folder`, to set your configuration options:

```
minions.user.name - The user name (JID localpart) for the account Minions should log in as.
minions.user.service - The chat service (JID domainpart) for the account.
minions.user.resource - The resource (JID resourcepart) for login.
minions.user.password - The password for the account.
minions.service.server - Optional server if not the same as service.
minions.service.port - Optional port if not the default 5222.
minions.room.jid - The JID of the room to join.
minions.room.nick - Nickname to use in the room.
minions.refresh.seconds - Polling interval to check for new plugins, defaults to 10 seconds.
minions.prefix - The command prefix, defaults to '!'
minions.pluginsdir - The directory in which plugins are located, defaults to ~/.local/share/minions/plugins
```

Run the starter script from the `minions-core` folder:

```
./runMinions.sh
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

Using the `echo-minion` as an example,  create your implementation of the `Minion` interface:

```java
public class EchoMinion implements Minion {
    private static final String COMMAND = "echo";
    
    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " [message] - Echo something.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String[] jid = StringUtils.split(from, "/");
        try {
            String toEcho = message.substring(COMMAND.length() + 2);
            muc.sendMessage(jid[1] + " said: " + toEcho);
        } catch (RuntimeException e) {
            muc.sendMessage(jid[1] + " didn't say anything for me to echo");
        }
    }
}
```

The plugin needs to be packaged as a fat jar (with dependencies) and must include a Manifest attribute `MinionClass` to let Minions know how to load it.  Example from `echo-minion`:

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

The plugin will be available on the next refresh (`minions.refresh.seconds`).

#Using Minions
When Minions is present in the chat room, use the following to list available commands, (assuming the default prefix '!' is configured:

```
!help
```

Example output:

```
18:11 - minions: 
        !help - Show this help.
        !status [url] - Get the http status code for a URL.
        !chatter [message] - Send a message to chatterbot.
        !echo [message] - Echo something.
        !props - Show OS system properties.
```

To execute a command, enter the command with the prefix, and any args required e.g.:

```
18:49 - boothj5: !status http://www.profanity.im
18:49 - minions: Status http://www.profanity.im: 200 - OK
```
