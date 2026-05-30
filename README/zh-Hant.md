# GeyserTPC

### 🌏 [ [English](../README.md) | [简体中文](zh-Hans.md) | 繁體中文 ]

---

# GeyserTPC

專為 Geyser-Fabric 伺服器設計的跨平台傳送工具模組。

GeyserTPC 基於 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) 開發。TeleportCommands 為 Minecraft 伺服器提供了家園（Home）、傳送點（Warp）、傳送請求（TPA）、返回死亡地點、返回主世界出生點等實用傳送功能。

本分支針對基岩版玩家的使用體驗進行了最佳化，透過 Floodgate 提供原生基岩版介面支援，並強化了 Geyser 跨平台環境下的易用性與相容性。

## 功能特色

* 家園（Home）管理系統
* 全服傳送點（Warp）系統
* 玩家傳送請求（TPA）系統
* 返回死亡地點（`/back`）
* 返回主世界出生點（`/worldspawn`）
* 為基岩版玩家最佳化的 GUI 介面
* 基於 Geyser 的跨平台互通支援
* Java 版快捷鍵支援（需同時安裝於用戶端與伺服器）

## 基岩版支援

安裝 Floodgate 後，基岩版玩家可透過原生基岩版表單介面進行：

* 家園管理（`/ghome`）
* 傳送點管理（`/gwarp`）
* 傳送請求管理（`/gtpa`）
* 返回死亡地點與出生點確認介面

相較於純指令操作，此方式在手機、平板及遊戲主機平台上更加直覺且易於使用。

> **警告**
>
> 若伺服器未安裝 Floodgate，基岩版玩家將無法使用原生表單介面，而需改用標準物品欄 GUI 介面。

---

## 介面展示

### TPA 選單（Java 版 / 基岩版）

![Tpa Menu](https://cdn.modrinth.com/data/5c5E3utm/images/aacc512143b127c3e8464c02b3b56f9057d4c1e8_350.webp)

透過 `/gtpa` 指令開啟的主選單介面。

### 選擇目標玩家（Java 版 / 基岩版）

![Tpa to...](https://cdn.modrinth.com/data/5c5E3utm/images/797d6c060985574a42f05f1437297e459ebde4bf_350.webp)

選擇要發送傳送請求的目標玩家。

### Home 選單（Java 版 / 基岩版）

![Home Menu](https://cdn.modrinth.com/data/5c5E3utm/images/f281376f2b6b24efb9a81b067f5e5f78e51327e0_350.webp)

透過 `/ghome` 指令開啟的家園管理介面。

`/gwarp` 的操作方式與介面風格相似。

### 不安全位置提示

![Unsafe Destination Warning](https://cdn.modrinth.com/data/5c5E3utm/images/1103a30733bfe731b8c9bad63f4a133b48ecc860_350.webp)

當 `/back` 或 `/worldspawn` 的目標位置存在安全風險時，將顯示確認提示。

---

## 指令列表

### 世界與實用傳送

* `/worldspawn [<skipSafetyCheck>]`

  傳送至主世界出生點。

  當參數為 `true` 時，將略過安全檢查。

* `/back [<skipSafetyCheck>]`

  傳送至最近一次死亡的位置。

  當參數為 `true` 時，將略過安全檢查。

### Home 系統

Home 為玩家個人的專屬傳送點。

* `/sethome <name>`

  在目前位置建立一個 Home。

* `/home [<name>]`

  傳送至指定 Home。

  若未指定名稱，則傳送至預設 Home。

* `/ghome`

  開啟 Home 管理介面。

* `/delhome <name>`

  刪除指定 Home。

* `/renamehome <name> <new_name>`

  重新命名指定 Home。

* `/homes`

  查看所有 Home。

* `/defaulthome <name>`

  設定預設 Home。

### Warp 系統

Warp 為伺服器公共傳送點，由管理員維護。

* `/warp <name>`

  傳送至指定 Warp。

* `/gwarp`

  開啟 Warp 管理介面（管理員）。

* `/warps`

  查看所有 Warp。

* `/setwarp <name>`

  建立 Warp。

  需要 4 級權限。

* `/delwarp <name>`

  刪除 Warp。

  需要 4 級權限。

* `/renamewarp <name> <new_name>`

  重新命名 Warp。

  需要 4 級權限。

### TPA 傳送請求系統

* `/tpa <player>`

  向目標玩家發送傳送請求。

* `/gtpa`

  開啟 TPA 管理介面。

* `/tpahere <player>`

  請求目標玩家傳送至自己的位置。

* `/tpaaccept <player>`

  接受傳送請求。

* `/tpadeny <player>`

  拒絕傳送請求。

---

## 計畫功能

* 基岩版介面搜尋功能

---

## 致謝

特別感謝原作者 [MrSnowy](https://github.com/MrSn0wy)。

本專案基於 [TeleportCommands](https://github.com/MrSn0wy/TeleportCommands) 開發，並在其基礎上進行了適配與擴充。

---

## 授權條款

本專案採用 [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/LICENSE) 開源。

原始 TeleportCommands 專案同樣採用 [MIT License](https://raw.githubusercontent.com/WJiangzhi/GeyserTPC/refs/heads/master/.orig_mod/LICENSE) 開源。
