# [GTPA] GeyserTPA

### 🌏 Need instructions in other languages? Please visit the [GitHub](https://github.com/WJiangzhi/GeyserTPC)

---

> A teleportation utility mod for Geyser-Fabric servers, built on top of [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands).
>
> Looking for the non-Geyser or NeoForge version? [View original project](https://github.com/MrSn0wy/TeleportCommands)
>
> This mod introduces command-based and GUI-based teleportation systems (e.g. `/gtpa`) optimized for cross-platform gameplay, with enhanced usability for Bedrock Edition players.

> ***WARNING**
>
> The Bedrock Edition GUI depends on the Floodgate mod.
> 
> Without Floodgate installed on the server, GUI features will not be available for Bedrock players.

> ***NOTE**
> 
> When installed on both server and client, pressing the `\` key provides quick access to the `/gtpa` interface.

---

## Commands

### World & Utility Teleportation

- `/worldspawn [<skipSafetyCheck>]`  
  Teleports the player to the Overworld spawn point.  
  If set to `true`, safety checks will be bypassed.

- `/back [<skipSafetyCheck>]`  
  Teleports the player to their last death location.  
  If set to `true`, safety checks will be bypassed.

---

### Home System

Homes are private teleport locations bound to individual players.

- `/sethome <name>`  
  Creates a new home at the current location.

- `/home [<name>]`  
  Teleports to the specified home.  
  If omitted, the default home will be used.

- `/ghome`  
  Opens the home management GUI for teleportation and deletion.

- `/delhome <name>`  
  Deletes an existing home.

- `/renamehome <name> <new_name>`  
  Renames an existing home.

- `/homes`  
  Lists all saved homes.

- `/defaulthome <name>`  
  Sets the default home.

---

### Warp System

Warps are server-wide teleport points managed by administrators.

- `/warp <name>`  
  Teleports to a specified warp point.

- `/gwarp`  
  Opens the warp management GUI (admin only).

- `/warps`  
  Lists all available warps.

- `/setwarp <name>`  
  Creates a new warp point. Requires permission level 4.

- `/delwarp <name>`  
  Removes an existing warp point. Requires permission level 4.

- `/renamewarp <name> <new_name>`  
  Renames a warp point. Requires permission level 4.

---

### Player Teleport Requests (TPA System)

- `/tpa <player>`  
  Sends a teleport request to another player.

- `/gtpa`  
  Opens the TPA request management GUI, allowing players to accept or deny requests.

- `/tpahere <player>`  
  Requests a player to teleport to your location.

- `/tpaaccept <player>`  
  Accepts an incoming teleport request.

- `/tpadeny <player>`  
  Denies an incoming teleport request.

---

## TODO

- [ ] Improve Bedrock Edition GUI compatibility
- [x] Hide clickable chat commands for Bedrock Edition players

---

## Credits

Special thanks to the original author [Mr.Sn0wy](https://github.com/MrSn0w).

This project is heavily inspired by [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands), which provided the foundation for this fork.

---

## License

This project is licensed under the [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE).