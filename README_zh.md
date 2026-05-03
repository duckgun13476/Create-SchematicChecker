# Create: Schematic Checker (CSC)

Create: Schematic Checker 是一个面向
[机械动力 Create](https://github.com/Creators-of-Create/Create) 蓝图的服务器防护模组。
它会在蓝图被打印进世界之前扫描上传的蓝图，阻止或清理已经记录在案的恶意 NBT 与异常结构。

CSC 主要面向允许玩家上传蓝图的公益服、生存服和整合包服务器。它可以防护常见的机械动力蓝图漏洞，包括物品复制、获取创造物品、卡服、崩服、异常结构、以及附属模组带来的危险 NBT。

> 曾用名：`Create:SchematicChecker`、`机械动力：蓝图校验`

<p align="center">
<a href="https://modrinth.com/mod/createschematicchecker"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/modrinth_vector.svg" alt="Modrinth 页面"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/create-schematicchecker"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/curseforge_vector.svg" alt="CurseForge 页面"></a>
<a href="https://discord.gg/rQV5JPauY7"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/social/discord-singular_vector.svg" alt="Discord 服务器"></a>
</p>

<p align="center">
<a href="README.md">English</a> | 简体中文
</p>

![CSC preview](https://github.com/user-attachments/assets/85848717-c13d-4c70-8049-6c136a387021)

## 支持与反馈

- 答疑 / 问题反馈 / 帮助：QQ群 `1061133894`
- GitHub 反馈：[Issues](https://github.com/duckgun13476/Create-SchematicChecker/issues)

## 功能概览

- 异步扫描蓝图，不占用 Minecraft 服务器主线程。
- 检测并阻止已知的机械动力蓝图恶意漏洞。
- 支持清理或阻止指定 NBT 字段、标签、方块、物品和实体。
- 校验传送带、链式传动轮、机械手、过滤器、流体储罐、剪贴板等高风险方块实体。
- 支持通过 `config/CSC/user_rule.json` 编写本地自定义规则。
- 可选联网同步云端规则，服务器管理员自行决定是否启用。
- 支持日志和蓝图备份，便于审计、追溯和后续排查。

## 支持版本

请根据 Minecraft 版本、加载器和 Create 版本选择对应构建。

| 加载器 | Minecraft | 目标 Create 版本 |
| --- | --- | --- |
| Forge | 1.14.4 | 0.2.3 |
| Forge | 1.15.2 | 0.3.1 |
| Forge | 1.16.5 | 0.3.2g |
| Forge | 1.17.1 | 0.4 |
| Forge | 1.18.2 | 0.5.1i |
| Forge | 1.19.2 | 0.5.1i |
| Forge | 1.20.1 | 0.5.1j |
| Forge | 1.20.1 | 6.0.x |
| Fabric | 1.18.2 | 0.5.1i |
| Fabric | 1.19.2 | 0.5.1i |
| Fabric | 1.20.1 | 6.0.x |
| Fabric | 1.21.1 | 6.0.x |
| NeoForge | 1.21.1 | 6.0.x |

## 依赖

- Minecraft
- Create

部分构建会随目标版本要求依赖 Flywheel、Ponder、Registrate 等 Create 生态依赖，请以对应版本的整合环境为准。

## 安装

1. 下载与你的加载器、Minecraft 版本、Create 版本匹配的 CSC jar。
2. 将 jar 放入服务器 `mods` 文件夹。
3. 启动一次服务器，生成 `config/CSC/config.toml`。
4. 在开放蓝图上传前检查并调整配置。
5. 修改配置后执行 `/csc reload` 重新加载。

## 配置

主配置文件：

```text
config/CSC/config.toml
```

常用配置项：

| 配置项 | 作用 |
| --- | --- |
| `core.Enable` | 启用或关闭 CSC 检查。 |
| `core.BanBlock` | 阻止或清理高风险方块与物品。 |
| `core.BanTag` | 移除或阻止危险 NBT 标签。 |
| `core.KillEntity` | 清理蓝图内实体，防止实体相关复制漏洞。 |
| `core.WhiteListModEnable` | 仅允许白名单 mod 保留 NBT，推荐公益服开启。 |
| `debug.EnableBackup` | 备份上传蓝图，便于后续审计，推荐开启。 |
| `online.enableAutoUpdate` | 允许 CSC 获取云端规则更新，默认关闭。 |
| `online.enableManualConfig` | 启用 `config/CSC/user_rule.json` 中的本地规则。 |

本地自定义规则：

```text
config/CSC/user_rule.json
```

云端规则缓存：

```text
config/CSC/online/
```

## 指令

| 指令 | 说明 |
| --- | --- |
| `/csc` | 显示基础状态。 |
| `/csc help` | 显示帮助和反馈链接。 |
| `/csc reload` | 重载 CSC 配置和规则。 |
| `/csc list` | 显示规则列表帮助。 |
| `/csc list IdMatchRuleAll` | 列出全部 ID 匹配规则。 |
| `/csc list OperateMatchRuleAll` | 列出全部 NBT 操作规则。 |
| `/csc DisableTemp` | 临时关闭检查 5 分钟。 |
| `/csc Enable` | 恢复临时关闭的 CSC。 |
| `/csc notice whitelistid on|off` | 开关 mod ID 白名单扫描提示。 |
| `/csc mode whitelistid on|off` | 开关 mod ID 白名单模式。 |

## 已覆盖的漏洞与问题

CSC 覆盖了大量已经公开或已被报告的机械动力蓝图漏洞，以及附属模组带来的 NBT 风险。能公开的视频样例会保留链接。

| # | 状态 | 问题 |
| --- | --- | --- |
| 1 | 修复 | 使用讲台、剪贴板打印创造物品。 [视频](https://www.bilibili.com/video/BV1sDp4ePEVp) |
| 2 | 修复 | 阀门数据篡改导致变量缓存溢出和内存泄漏。 [视频](https://www.bilibili.com/video/BV1UdC9YjET5) |
| 3 | 修复 | 剪贴板复制与创造属性剪贴板。 [视频](https://www.bilibili.com/video/BV1SXC9YEEeW) |
| 4 | 修复 | 传送带长度被篡改到超过 1000 格。 [视频](https://www.bilibili.com/video/BV1SXC9YEEeW) |
| 5 | 修复 | `-1` 长度传送带导致旧 Forge 服务器崩溃。 [视频](https://www.bilibili.com/video/BV1u9ytY2E8R) |
| 6 | 修复 | 巨量恶意传送带导致卡服、崩服或存档损坏。 [视频](https://www.bilibili.com/video/BV1NwybY3ERY) |
| 7 | 修复 | 锦致装饰翁在旧版本中的无限 GT 漏洞。 [视频](https://www.bilibili.com/video/BV1LUS9YCEk1) |
| 8 | 修复 | 弹射置物台弹射力量被恶意篡改。 [视频 1](https://www.bilibili.com/video/BV1itXDY3EwJ)、[视频 2](https://www.bilibili.com/video/BV13RKneEEFG) |
| 9 | 修复 | 过滤器 NBT 过大导致极高 tick 卡顿。 |
| 10 | 修复 | 超长链式传动轮匹配导致服务器卡顿。 [视频](https://www.bilibili.com/video/BV1vz9bY7EW5) |
| 11 | 修复 | `using_converts_to` 标签获取创造物品。 [视频](https://www.bilibili.com/video/BV1c19tYsEBL) |
| 12 | 修复 | 机械手距离被篡改到不合理范围。 [视频](https://www.bilibili.com/video/BV1XpXYYDEt7) |
| 13 | 修复 | 链式传动轮目标被篡改到非法位置。 [视频](https://www.bilibili.com/video/BV1nddcYSEWQ) |
| 14 | 修复 | 异常扭曲传送带导致传送带或传动杆复制。 [视频](https://www.bilibili.com/video/BV1omdmYrE3S) |
| 15 | 修复 | 链式传动轮含数千目标导致卡顿和复制。 [视频](https://www.bilibili.com/video/BV1Ze5Wz7EB2) |
| 16 | 修复 | 机械手 safe-NBT 被篡改导致无限产出。 [视频](https://www.bilibili.com/video/BV1udtYzwEQN) |
| 17 | 修复 | 机械手与伪装板类模组组合导致物品复制。 [视频](https://www.bilibili.com/video/BV1dubezLEp7) |
| 18 | 修复 | 安山漏斗返还过滤器漏洞。 [视频](https://www.bilibili.com/video/BV1UrEGzmEDU) |
| 19 | 修复 | 使用 burn 标签获取创造物品。 [视频](https://www.bilibili.com/video/BV1UrEGzmEDU) |
| 20 | 修复 | 实体相关复制与创造物品获取，包括盔甲架。 [视频](https://www.bilibili.com/video/BV1wtRNYaE5m) |
| 21 | 修复 | 特定版本轧机相关创造物品漏洞。 [视频](https://www.bilibili.com/video/BV1b4eyzGEoj) |
| 22 | 修复 | 附魔工业创造标签打印。 [视频](https://www.bilibili.com/video/BV1GKemzWEKm) |
| 23 | 修复 | 创意传动无限能源电池打印。 [视频](https://www.bilibili.com/video/BV193vAzNEJ2) |
| 24 | 修复 | 集成农业鸡舍 GT 卡服漏洞。 [视频](https://www.bilibili.com/video/BV13nh2z5EvT) |
| 25 | 修复 | 利用 NBT 执行命令的恶性漏洞。 [视频](https://www.bilibili.com/video/BV1rZY5z1Eo6) |
| 26 | 修复 | Create 6.0+ 流体储罐容量篡改。 [视频](https://www.bilibili.com/video/BV1hSnMzCE6A/) |
| 27 | 修复 | 夸克铁丝网无限岩浆漏洞。 [视频](https://www.bilibili.com/video/BV1hSnMzCE6A/) |
| 28 | 修复 | 修改动力合成器实现物品虚空传送。 [视频](https://www.bilibili.com/video/BV1hSnMzCE6A/) |
| 29 | 修复 | 其他物品虚空传送漏洞变体。 |
| 30 | 修复 | 强力胶黏着检测被滥用导致严重卡顿或崩溃。 |
| 31 | 修复 | 机壳底盘、斜向底盘黏着检测被滥用导致严重卡顿或崩溃。 |
| 32 | 检测 | 机械动力：火炮炮弹引信数据篡改以获取创造物品。 |
| 33 | 修复 | 暮色森林巨型方块打印无视地形并破坏基岩。 |

## 相关项目

旧版 Python 实现和历史使用说明：

- [CreateSchematicsChecker-Python](https://github.com/duckgun13476/CreateSchematicsChecker-Python)

## 致谢

特别感谢：

- **crackun24**：提供 Mixin 部分参考代码。
- 起飞的玫瑰、恐鱼、air、crackun24、runner、CTR 服主、千鹤公益服，以及其他多位机械动力公益服服主：提供检查样本和后续处理支持。
- CMS 蓝图站的部分合作 UP (cym,柠喵喵喵)：看到蓝图 bug 修复后选择直接拉黑作者的行为，也成为了本项目持续更新的动力之一。
- B 站 UP 主 **一只不屑的屑蜘蛛**。多次使用问题蓝图攻击公益服、传播问题样本，移花接木污蔑作者、伪造自己为受害者却丝毫拿不出证据等行为，直接促成了本项目的诞生，并推动机械动力蓝图漏洞更快得到修复。

## 许可证

本项目使用 GNU LGPL 3.0 许可证。
