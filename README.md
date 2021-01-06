# Tickets plugin
### Support ticket management for Spigot & Bungee

## Features
* Run across your Bungee/Waterfall server network (requires a small helper plugin running on each server)
* Manage tickets without typing commands by using the clickable chat buttons
* Teleport to ticket location across servers
* Discord integration (TODO)

### TODO
* Make helper plugin optional (resulting in reduced functionality)
* Add support for more databases

## Build

Remove the maven-resources-plugin or change the path in the `pom.xml` for **BungeeTickets** and **TicketsHelper**. 
The maven-resources-plugin copies the packaged jars to a server's plugins directory for easy testing.

Run `mvn clean package` for the root TicketsPlugin folder. This will build and package all plugins.