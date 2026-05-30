# GeyserTPC

**🌏 Need instructions in other languages? Please visit the [GitHub](https://github.com/WJiangzhi/GeyserTPC)**

---

A teleportation utility mod designed specifically for Geyser-Fabric servers.

GeyserTPC is based on [TeleportCommands](https://modrinth.com/mod/teleport-commands), a teleportation mod that provides
homes, warps, teleport requests, spawn teleportation, and other quality-of-life commands for Minecraft servers.

This fork extends the original experience with Bedrock Edition support, offering Floodgate-powered graphical interfaces
and cross-platform usability improvements for players joining through Geyser.

## Features

- Home management system
- Server-wide warps
- Teleport request (tpa) system
- Death location teleportation (/back)
- Overworld spawn teleportation (/worldspawn)
- Bedrock-friendly GUI interfaces
- Cross-platform gameplay support through Geyser
- Java Edition keyboard shortcuts (When installed on both server and client)

## Bedrock Edition Support

When Floodgate is installed, Bedrock Edition players can access the Bedrock Edition native interface for:

- Home management (/ghome)
- Warp management (/gwarp)
- Teleport request management (/gtpa)
- Back / Worldspawn dialog

This provides a more user-friendly experience than command-only interaction on mobile and console devices.

> ***WARNING**
>
> Without Floodgate installed on the server, Bedrock Edition players must use the standard inventory-based interface
> instead.

---

## GUI

### Tpa Menu (Java/Bedrock Edition)

![Tpa Menu](https://cdn.modrinth.com/data/5c5E3utm/images/aacc512143b127c3e8464c02b3b56f9057d4c1e8_350.webp)

The "/gtpa" command menu

### Tpa to... Menu (Java/Bedrock Edition)

![Tpa to...](https://cdn.modrinth.com/data/5c5E3utm/images/797d6c060985574a42f05f1437297e459ebde4bf_350.webp)

The gui when you choose tpa player

### Home Menu (Java/Bedrock Edition)

![Home Menu](https://cdn.modrinth.com/data/5c5E3utm/images/f281376f2b6b24efb9a81b067f5e5f78e51327e0_350.webp)

The "/ghome" command menu
The '/gwarp' command is similar as well

### No safe location dialog

![Home Menu](https://cdn.modrinth.com/data/5c5E3utm/images/1103a30733bfe731b8c9bad63f4a133b48ecc860_350.webp)

When you enter /back or /worldspawn, the target location is not safe

---

## Commands

### World & Utility Teleportation

- `/worldspawn [<skipSafetyCheck>]`

  Teleports the player to the Overworld spawn point.

  If set to `true`, safety checks will be bypassed.

- `/back [<skipSafetyCheck>]`

  Teleports the player to their last death location.

  If set to `true`, safety checks will be bypassed.

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

## Planned Features

- Search support in Bedrock Edition UI

---

## Credits

Special thanks to the original author [MrSnowy](https://modrinth.com/user/MrSnowy).

This project is heavily inspired by [TeleportCommands](https://modrinth.com/mod/teleport-commands), which provided the
foundation for this fork.

---

## License

This project is licensed under
the [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE).

The original Teleport Commands project is also licensed under
the [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/.orig_mod/LICENSE).