***
**The content discussed in this article is still work in progress so it may be out of date!**
***


If you need functionality that is not provided by the Game-Radar or the plugins that ship with it you can create your very own plugin using Java or any other JVM compatible language.

# Prerequisites:
* JDK
* Maven (gradle should work too, but there are no templates for it so you would need to create your own)
* git

# Generating the template
The easiest way to create your own plugin is by generating it from one of the provided [maven archetypes](https://maven.apache.org/archetype/index.html).  
Follow these steps to generate your template:

### 1. Use git to clone the repository (if you didn't yet)  
`git clone https://github.com/grimsi/game-radar.git`  
`cd game-radar`   

### 2. Install the plugin-api to your local maven repository  
`mvn install -f plugin-api/pom.xml`

### 3. Install the maven archetypes (depending on the type of the plugin you want to generate)  
All archetypes are in the folder `game-radar/plugins/archetypes`. In the following example we will generate a barebone plugin that is able to query the status of game servers.  
`mvn install -f plugins/archetypes/server-status-plugin-archetype/pom.xml`

### 4. Generate the plugin template  
Now go to the folder where you want to create your new plugin and execute the following command:
`mvn archetype:generate`  
Maven will then show you a list of available archetypes. In this example we want to use ` de.grimsi.gameradar.plugins:server-status-plugin-archetype`. Enter the number corresponding to the archetype you want to use. Maven will now ask you to enter some basic information about your plugin. In this example we use the following values (although you're free to use your own):  
`groupId`: de.grimsi.gameradar.plugins  
`artifactId`: example-server-status-plugin  
`version`: 1.0-SNAPSHOT  
`package`: de.grimsi.gameradar.plugins (I would advise you to choose the same value as for `groupId`)  
`pluginAuthor`: grimsi  
`pluginDescription`: Example plugin to showcase the process of generating an empty template.  
  
Enter 'Y' to confirm. Maven will now generate your plugin.

### 5. Inspect the template
The template contains three types of files:
| File                       | Description                                                                                                                                                                                                                                                            |
|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|           pom.xml          | The [Project Object Model](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) of your plugin                                                                                                                                                   |
| files in META-INF/services | These files are needed so that the application can recognize your plugin and load it. Basically the files tell the main application which functionality is implemented in your plugin. More info [here](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html). |
| Java class file            | This is the main class of your plugin. You need to implement all necessary methods in here (but you're free to add own classes of course). When you generate your template this class will be filled with placeholder values.                                          |

# Implementing your plugin

Now that you generated the template it's time to start implementing. If you need any help you can always take a look at the plugins that ship with the application (https://github.com/grimsi/game-radar/tree/master/plugins).

### PluginMetaInfo
Every plugin needs to implement the `getMetaInfo()` method. Let's take a look at what information should be returned:
| Property              | Description                                                                                                                                                                      | Example                                                                                      |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| author                | The plugin author                                                                                                                                                                | grimsi                                                                                       |
| name                  | The name of the plugin                                                                                                                                                           | example-server-status-plugin                                                                 |
| type                  | The plugin type (which class it implements)                                                                                                                                      | ServerStatusPlugin                                                                           |
| version               | The plugin version                                                                                                                                                               | 1.0.0                                                                                        |
| description           | A short text explaining what your plugin can do                                                                                                                                  | This plugin allows you to view the status of a Minecraft server.                             |
| supportedProviders    | This one is a bit tricky. A plugin can support one or many "providers". A provider is essentially a source where the plugin gets its data from (for example a minecraft server). | Minecraft                                                                                    |
| license (optional)    | The license for the source code of the plugin.                                                                                                                                   | MIT                                                                                          |
| repository (optional) | A url pointing to the source code of the plugin.                                                                                                                                 | https://github.com/grimsi/game-radar/tree/master/plugins/minecraft-server-status-plugin |

### ServerStatus

If your plugin is a ServerStatusPlugin (currently the only one thats available) then it will also need to implement the methods `getServerStatus(String address, int port)` and `getDefaultServerPort()`.
Let's take a look at what information should be returned:
| Property        | Description                                 | Example                |
|-----------------|---------------------------------------------|------------------------|
| game            | The name of the game the server is hosting. | Garry's Mod            |
| status          | The current status of the server.           | RUNNING                |
| address         | The host address of the server.             | mygmodserver.com       |
| port            | The port of the game server.                | 27015                  |
| currentPlayers  | The number of player currently playing.     | 10                     |
| maxPlayers      | The total number of slots on the server.    | 24                     |
| currentMap      | The map currently in rotation.              | ttt_lego               |
| currentGamemode | The current gamemode.                       | TTT2 (Advanced Update) |

### Building your plugin

The plugin can be compiled with the following command:  
`mvn clean package`  
After the successful compilation you will find two .jar files in the "target" folder of your project.
Take the one **without** the version number at the end and copy it into the "plugins" folder of Game-Radar.
