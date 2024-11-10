**Plugin Features**
> Join and Leave messages configurable in the config.yml file.


> 1v1 Rtp Fight


> Everything is configurable in the config.yml


> Global Rtp Queue Join Message


> Support for Spigot/Paper/Purpur (and other spigot-based forks)


>  1.20.x to 1.21.x (I Haven't tested other versions)


> Permissions:
- yech.rtpqreload (reloads the config.yml file)
- heaven.rtpq


> TODO:
- idk

> Config.yml:
```
messages:
  joined-rtpq: "&7You joined the queue."
  left-rtpq: "&7You left the queue."
  global-joined-rtpq: "&a%player% joined the queue."
  global-left-rtpq: "&c%player% left the queue."
  being-teleported: "&7You are being teleported."
  config-reloaded: "&2Config Reloaded"
  actionbar-joined-rtpq: "§2You joined the queue."
  actionbar-left-rtpq: "§cYou left the queue."
  actionbar-being-teleported: "§7You are being teleported."
  no-perms: "&cYou don't have the yech.rtpqreload permission, if u believe this is an error, contact a member of the staff."
  queue-flushed-message: "&cRtpqueue data was flushed, you may need to reenter the queue."
Min x: 50 # Minimum X coordinate for tp
Min z: 50 # Minimum Z coordinate for tp
Max x: 8000 # Maximum X coordinate for tp (I suggest setting this to your chunky radius)
Max z: 8000 # Maximum Z coordinate for tp (I suggest setting this to your chunky radius)
queue-flush-interval: 300 #Interval in seconds to clear the queue
teleport-delay: 1 # Time in seconds before the plugin teleports the players
queue-size: 2 # Number of players needed to start the tp

```
