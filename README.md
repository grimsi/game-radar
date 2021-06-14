# Game Radar

**Please note that this application is still in development and not all features listed below are currently usable.**

Game Radar makes it easy for users to discover game servers on your LAN-Party!

Based on Spring Boot and Angular, this Docker-native application will scan your local network for game servers and present them in a nice web-based frontend.

## Features

Features written in italic are not (fully) implemented yet.

- Manually add game servers by IP adress or hostname
- _Auto-discovery of game servers_
- Plugin-based support of game server protocols for easy extensibility
- _Filter all discovered or manually added servers in the frontend_

## Installation

To-Do, but something like:

```
docker compose up -d
```

or

```
docker run -d --net=host --name game-radar grimsi/game-radar:latest
```

## System design

![System Overview](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/grimsi/game-radar/main/docs/overview.iuml)