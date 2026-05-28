# [GTPA] GeyserTPA

---

### 🌏 [ [English](../README.md) | 简体中文 | [繁體中文](zh-Hant.md) ]

---

> 一个用于 Geyser-Fabric 服务器的传送模组，基于 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands)
>
> 想要非 Geyser 或 NeoForge 版本？[点这里查看~](https://github.com/MrSn0wy/TeleportCommands)
>
> 本模组新增类似 `/gtpa` 的指令，用于打开 GUI 界面，为触屏设备提供更好的体验。

> [!WARNING]
> 本模组的基岩版 UI 依赖 Floodgate 模组。
> 
> 如果服务器未安装 Floodgate，基岩版将无法打开 GUI。

> [!NOTE]
> 即使只安装在客户端也仍然可用。  
> 
> 当服务器和客户端都安装本模组后，按下 `\` 键可快速打开 `/gtpa` GUI。

---

### 可用指令

- `/worldspawn [<关闭安全检查>]`  
  传送到主世界出生点（主世界）。  
  如果填写 `true`，将跳过安全检查。

- `/back [<关闭安全检查>]`  
  传送到上一次死亡地点。  
  如果填写 `true`，将跳过安全检查。

**“家（Home）”是仅玩家自己可访问的私人传送点。**
- `/sethome <名称>`  
  创建一个新家。

- `/home [<名称>]`  
  传送到某个家。  
  如果未填写名称，将使用默认家。

- `/ghome`  
  打开家庭列表 GUI，可进行传送和删除操作。

- `/delhome <名称>`  
  删除一个家。

- `/renamehome <名称> <新名称>`  
  重命名一个家。

- `/homes`  
  显示所有家列表。

- `/defaulthome <名称>`  
  设置默认家。

**“传送点（Warp）”是由管理员管理的公共地点，所有玩家可用。**
- `/warp <名称>`  
  传送到某个传送点。

- `/gwarp`  
  打开传送点 GUI 列表（仅管理员），可进行传送和删除操作。

- `/warps`  
  显示所有传送点。

- `/setwarp <名称>`  
  创建传送点，需要权限等级 4（管理员）。

- `/delwarp <名称>`  
  删除传送点，需要权限等级 4（管理员）。

- `/renamewarp <名称> <新名称>`  
  重命名传送点，需要权限等级 4（管理员）。

**使用 “TPA” 可以请求传送到其他玩家或让其他玩家传送到你。**
- `/tpa <玩家>`  
  向其他玩家发送传送请求。

- `/gtpa`  
  打开请求 GUI，可接受/拒绝请求并发送请求。

- `/tpahere <玩家>`  
  向其他玩家发送“传送到我这里”的请求。

- `/tpaaccept <玩家>`  
  接受某个玩家的 tpa/tpahere 请求。

- `/tpadeny <玩家>`  
  拒绝某个玩家的 tpa/tpahere 请求。

---

### 待办事项

- [ ] 更好的基岩版 UI 支持
- [x] 在基岩版玩家中隐藏可点击聊天命令

---

### 致谢

特别感谢原作者 [Mr.Snowy](https://github.com/MrSn0w)

我非常喜欢 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) 模组，它启发我制作了这个分支版本。

---

### LICENSE - [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE)