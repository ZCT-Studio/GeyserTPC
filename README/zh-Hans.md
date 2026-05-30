# GeyserTPC

### 🌏 [ [English](../README.md) | 简体中文 | [繁體中文](zh-Hant.md) ]

---

# GeyserTPC

专为 Geyser-Fabric 服务器设计的跨平台传送工具模组。

GeyserTPC 基于 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) 开发。TeleportCommands 为 Minecraft
服务器提供了家园（Home）、地标点（Warp）、传送请求（TPA）、返回死亡点、返回主世界出生点等实用传送功能。

本分支针对基岩版玩家的使用体验进行了优化，通过 Floodgate 提供原生基岩版界面支持，并增强了 Geyser 跨平台环境下的易用性与兼容性。

## 功能特性

* 家园（Home）管理系统
* 全服地标点（Warp）系统
* 玩家传送请求（TPA）系统
* 返回死亡地点（`/back`）
* 返回主世界出生点（`/worldspawn`）
* 面向基岩版玩家优化的 GUI 界面
* 基于 Geyser 的跨平台互通支持
* Java 版快捷键支持（客户端与服务端同时安装时可用）

## 基岩版支持

安装 Floodgate 后，基岩版玩家可使用原生基岩版表单界面进行：

* 家园管理（`/ghome`）
* 地标点管理（`/gwarp`）
* 传送请求管理（`/gtpa`）
* 返回死亡点与出生点确认界面

相比纯命令操作，该方式在手机、平板及主机平台上更加直观和易于使用。

> **警告**
>
> 如果服务器未安装 Floodgate，基岩版玩家将无法使用原生表单界面，而需要使用标准的物品栏 GUI 界面。

---

## 界面展示

### TPA 菜单（Java 版 / 基岩版）

![Tpa Menu](https://cdn.modrinth.com/data/5c5E3utm/images/aacc512143b127c3e8464c02b3b56f9057d4c1e8_350.webp)

`/gtpa` 命令打开的主菜单界面。

### 选择目标玩家（Java 版 / 基岩版）

![Tpa to...](https://cdn.modrinth.com/data/5c5E3utm/images/797d6c060985574a42f05f1437297e459ebde4bf_350.webp)

选择要发送传送请求的目标玩家。

### Home 菜单（Java 版 / 基岩版）

![Home Menu](https://cdn.modrinth.com/data/5c5E3utm/images/f281376f2b6b24efb9a81b067f5e5f78e51327e0_350.webp)

`/ghome` 命令打开的家园管理界面。

`/gwarp` 的操作方式与界面风格类似。

### 不安全位置提示

![Unsafe Destination Warning](https://cdn.modrinth.com/data/5c5E3utm/images/1103a30733bfe731b8c9bad63f4a133b48ecc860_350.webp)

当 `/back` 或 `/worldspawn` 的目标位置存在安全风险时，将显示确认提示。

---

## 指令列表

### 世界与实用传送

* `/worldspawn [<skipSafetyCheck>]`

  传送至主世界出生点。

  当参数为 `true` 时，将跳过安全检查。

* `/back [<skipSafetyCheck>]`

  传送至最近一次死亡的位置。

  当参数为 `true` 时，将跳过安全检查。

### Home 系统

Home 为玩家个人专属传送点。

* `/sethome <name>`

  在当前位置创建一个 Home。

* `/home [<name>]`

  传送至指定 Home。

  若未指定名称，则传送至默认 Home。

* `/ghome`

  打开 Home 管理界面。

* `/delhome <name>`

  删除指定 Home。

* `/renamehome <name> <new_name>`

  重命名指定 Home。

* `/homes`

  查看所有 Home。

* `/defaulthome <name>`

  设置默认 Home。

### Warp 系统

Warp 为服务器公共传送点，由管理员管理。

* `/warp <name>`

  传送至指定 Warp。

* `/gwarp`

  打开 Warp 管理界面（管理员）。

* `/warps`

  查看所有 Warp。

* `/setwarp <name>`

  创建 Warp。

  需要 4 级权限。

* `/delwarp <name>`

  删除 Warp。

  需要 4 级权限。

* `/renamewarp <name> <new_name>`

  重命名 Warp。

  需要 4 级权限。

### TPA 传送请求系统

* `/tpa <player>`

  向目标玩家发送传送请求。

* `/gtpa`

  打开 TPA 管理界面。

* `/tpahere <player>`

  请求目标玩家传送至自己所在位置。

* `/tpaaccept <player>`

  接受传送请求。

* `/tpadeny <player>`

  拒绝传送请求。

---

## 计划功能

* 基岩版界面搜索功能

---

## 致谢

特别感谢原作者 [MrSnowy](https://github.com/MrSn0wy)。

本项目基于 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) 开发，并在其基础上进行了适配与扩展。

---

## 许可证

本项目采用 [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE) 开源。

原始 TeleportCommands 项目同样采用 [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/.orig_mod/LICENSE)开源。
