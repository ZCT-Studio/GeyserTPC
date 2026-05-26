# [GTPA] GeyserTPA

---

> 基于[TeleportCommands](https://github.com/MrSn0wy/TeleportCommands)开发的面向Geyser-Fabric服务器的传送模组
>
> 添加了类似 `/gtpa` 的命令用于打开GUI界面，可以在触屏设备上获得更好的体验
>

> [!WARNING]
> 此模组必须依赖于Geyser-Fabric Mod

> [!NOTE]
> 如果装在客户端上，它依旧有用，在服务端添加本模组后按下`\`键可快速打开`/gtpa`图形界面

### [更新日志](CHANGELOG.md) 在这儿~

---

### 当前可用命令:

- `/worldspawn [<禁用安全检查>]` - 将你传送到世界出生点（在主世界中），如果给定 true，它将不会进行安全检查
- `/back [<禁用安全检查>]` -  将你传送到上一次死亡的位置，如果传入true，它将不会进行安全检查
<br><br>
    **"home"(家)是特定玩家的位置，只有该玩家可以传送到那里**
- `/sethome <名称>` - 创建一个新家
- `/home [<名称>]` - 将你传送到家，如果没有提供名称，将传送到默认家
- `/ghome` - 弹出GUI，列出所有的家，可以进行传送/删除操作
- `/delhome <名称>` - 删除一个家
- `/renamehome <名称> <新名称>` - 重命名一个家
- `/homes` - 显示您的家的列表
- `/defaulthome <名称>` - 设置默认的家
<br><br>
    **"warp"(传送点)是由管理员管理的地点，所有玩家都可以传送到那里**
- `/warp <名称>` - 将你传送到传送点
- `/gwarp` - 弹出GUI，列出所有的传送点，可以进行传送/删除（管理员）操作
- `/warps` - 显示可用传送点列表
- `/setwarp <名称>` - 设置一个传送点。需要4级权限（管理员）
- `/delwarp <名称>` - 删除一个传送点。需要4级权限（管理员）
- `/renamewarp <名称> <新名称>` - 重命名传送点。需要4级权限（操作员）。
<br><br>
    **使用"tpa"，你可以传送到其他玩家或让他们传送到你身边**
- `/tpa <玩家>` - 向另一名玩家发送"tpa"请求
- `/gtpa` - 弹出GUI，列出所有的请求，可以同意/拒绝操作/发起请求
- `/tpahere <玩家>` - 向另一位玩家发送"tpaHere"请求
- `/tpaaccept <玩家>` - 接受该玩家的 tpa/tpahere 请求
- `/tpadeny <玩家>` - 拒绝该玩家的 tpa/tpahere 请求
<br>

---

### TODO
- [ ] 更适配的基岩版UI
- [ ] 基岩版玩家不再在聊天栏看见点击指令，

---

### 致谢

感谢原模组作者[Mr. Snowy](https://github.com/MrSn0wy)

他的[TeleportCommands](https://github.com/MrSn0wy/TeleportCommands)模组我很喜欢，才有兴趣二次开发

---

### LICENSE - [MIT-License](LICENSE)