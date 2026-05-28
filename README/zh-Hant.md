# [GTPA] GeyserTPA

### 🌏 [ [English](../README.md) | [简体中文](zh-Hans.md) | 繁體中文 ]

---

> 一個用於 Geyser-Fabric 伺服器的傳送模組，基於 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands)
>
> 想要非 Geyser 或 NeoForge 版本？[點這裡查看~](https://github.com/MrSn0wy/TeleportCommands)
>
> 本模組新增類似 `/gtpa` 的指令，用於打開 GUI 介面，為觸控裝置提供更好的體驗。

> [!WARNING]
> 本模組的基岩版 UI 依賴 Floodgate 模組。
> 
> 如果伺服器未安裝 Floodgate，基岩版將無法打開 GUI。

> [!NOTE]
> 當伺服器與客戶端都安裝本模組後，按下 `\` 鍵可快速打開 `/gtpa` GUI。

---

### 可用指令

- `/worldspawn [<關閉安全檢查>]`  
  傳送到主世界出生點（主世界）。  
  如果填寫 `true`，將跳過安全檢查。

- `/back [<關閉安全檢查>]`  
  傳送到上一次死亡地點。  
  如果填寫 `true`，將跳過安全檢查。

**「家（Home）」是僅玩家自己可存取的私人傳送點。**
- `/sethome <名稱>`  
  建立一個新家。

- `/home [<名稱>]`  
  傳送到某個家。  
  如果未填寫名稱，將使用預設家。

- `/ghome`  
  打開家庭列表 GUI，可進行傳送與刪除操作。

- `/delhome <名稱>`  
  刪除一個家。

- `/renamehome <名稱> <新名稱>`  
  重新命名一個家。

- `/homes`  
  顯示所有家列表。

- `/defaulthome <名稱>`  
  設定預設家。

**「傳送點（Warp）」是由管理員管理的公開地點，所有玩家皆可使用。**
- `/warp <名稱>`  
  傳送到某個傳送點。

- `/gwarp`  
  打開傳送點 GUI 清單（僅管理員），可進行傳送與刪除操作。

- `/warps`  
  顯示所有傳送點。

- `/setwarp <名稱>`  
  建立傳送點，需要權限等級 4（管理員）。

- `/delwarp <名稱>`  
  刪除傳送點，需要權限等級 4（管理員）。

- `/renamewarp <名稱> <新名稱>`  
  重新命名傳送點，需要權限等級 4（管理員）。

**使用「TPA」可以請求傳送到其他玩家，或讓其他玩家傳送到你。**
- `/tpa <玩家>`  
  向其他玩家發送傳送請求。

- `/gtpa`  
  打開請求 GUI，可接受/拒絕請求並發送請求。

- `/tpahere <玩家>`  
  向其他玩家發送「傳送到我這裡」的請求。

- `/tpaaccept <玩家>`  
  接受某個玩家的 tpa/tpahere 請求。

- `/tpadeny <玩家>`  
  拒絕某個玩家的 tpa/tpahere 請求。

---

### 待辦事項

- [ ] 更好的基岩版 UI 支援
- [x] 在基岩版玩家中隱藏可點擊聊天指令

---

### 致謝

特別感謝原作者 [Mr.Snowy](https://github.com/MrSn0w)

我非常喜歡 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) 模組，它啟發我製作了這個分支版本。

---

### LICENSE - [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE)