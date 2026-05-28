# [GTPA] GeyserTPA

### 🌏 [ English | [简体中文](README/zh-Hans.md) | [繁體中文](README/zh-Hant.md) ]

---

> A teleportation mod for Geyser-Fabric servers, based on [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands)
>
> Looking for the non-Geyser or NeoForge version? [Check it out here~](https://github.com/MrSn0wy/TeleportCommands)
>
> This mod adds commands like `/gtpa` to open GUI interfaces, providing a much better experience on touch-screen devices.

> [!WARNING]
> The BE UI of this mod relies on the Floodgate Mod.
> 
> If the server has not installed Floodgate, the BE cannot open the GUI.

> [!NOTE]
> After the server and client installs this mod, pressing the `\` key will quickly open the `/gtpa` GUI.

---

### Available Commands

- `/worldspawn [<disable safety check>]`  
  Teleports you to the world spawn point (in the Overworld).  
  If `true` is provided, safety checks will be skipped.

- `/back [<disable safety check>]`  
  Teleports you to your last death location.  
  If `true` is provided, safety checks will be skipped.

**"Homes" are private player locations that only the owner can access.**
- `/sethome <name>`  
  Creates a new home.

- `/home [<name>]`  
  Teleports you to a home.  
  If no name is provided, the default home will be used.

- `/ghome`  
  Opens a GUI listing all homes, allowing teleport and delete operations.

- `/delhome <name>`  
  Deletes a home.

- `/renamehome <name> <new_name>`  
  Renames a home.

- `/homes`  
  Displays a list of your homes.

- `/defaulthome <name>`  
  Sets the default home.

**"Warps" are public locations managed by administrators and available to all players.**
- `/warp <name>`  
  Teleports you to a warp point.
  
- `/gwarp`  
  Opens a GUI listing all warp points, allowing teleport and delete operations (admin only).

- `/warps`  
  Displays all available warp points.

- `/setwarp <name>`  
  Creates a warp point. Requires permission level 4 (admin).

- `/delwarp <name>`  
  Deletes a warp point. Requires permission level 4 (admin).

- `/renamewarp <name> <new_name>`  
  Renames a warp point. Requires permission level 4 (operator).

**Using "TPA", you can teleport to other players or request them to teleport to you.**
- `/tpa <player>`  
  Sends a teleport request to another player.

- `/gtpa`  
  Opens a GUI listing all requests, allowing accept/deny operations and sending requests.

- `/tpahere <player>`  
  Sends a "tpaHere" request to another player.

- `/tpaaccept <player>`  
  Accepts a player's tpa/tpahere request.

- `/tpadeny <player>`  
  Denies a player's tpa/tpahere request.

---

### TODO

- [ ] Search in BE UI

---

### Credits

Special thanks to the original mod author [Mr.Snowy](https://github.com/MrSn0w)

I really enjoyed the [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) mod, which inspired me to create this fork.

---

### LICENSE - [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE)
