# CSC 机械动力：蓝图检查

---

### ~~玩bug蓝图的熊孩子的对策杀手（bushi~~



![icon.png](icon.png)


## 答疑/问题反馈/帮助  QQ群：1061133894 
---
## 概述
这是一个基于JAVA的机械动力附属。
该MOD的全部检测功能完全异步，因此不会占用任何服务器主线程性能！

允许自定义规则。它能够自动筛查蓝图文件夹下的所有 bug 与异常蓝图，防止任何恶意篡改 NBT 的蓝图流入 Minecraft 服务器。

它可以修复极其大量的机械动力蓝图相关漏洞，包括最新版本的大量潜在蓝图漏洞，所有相关的修复与问题来源都已经在最后列出
![已经修复的机械动力漏洞](https://github.com/duckgun13476/CreateSchematicsChecker-Python?tab=readme-ov-file#%E9%92%88%E5%AF%B9%E5%B7%B2%E7%9F%A5%E9%97%AE%E9%A2%98%E7%9A%84%E4%BF%AE%E5%A4%8D)
![如何使用](https://github.com/duckgun13476/CreateSchematicsChecker-Python?tab=readme-ov-file#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95)

## 功能
- **自动筛查**：自动检查蓝图文件夹下的所有蓝图，识别并过滤出潜在的 bug 与异常蓝图，确保服务器安全。
- **高度自定义规则**：允许用户自定义规则，以针对特定模组实现特殊的 NBT 扫描。
- **传送带篡改校验**：使用全校验算法对 0.5.1 和 6.0.0 版本的传送带进行校验，阻止传送带蓝图的 bug 与复制、卡服、崩服特性。
- **齿轮传动轮配对校验**：在 6.0.0 版本后，基于简单匹配逻辑进行齿轮传动轮的配对校验，防止已知蓝图 bug 与复制特性。
- **多种恶意 NBT 筛查**：针对多种恶意 NBT 篡改值进行筛查，确保蓝图的安全性。
- **自动云端同步**：自动更新 NBT 检查规则，让恶意新出的bug蓝图无处可逃。
- **自动更新规则**：已实现自动更新规则功能，确保规则始终保持最新状态。

---

## 使用方法：
#### 根据您的机械动力版本选择合适的版本即可↓

## 自定义规则和配置

- 日志文件在 `log` 文件夹内，每次上传的蓝图都保存在`save`文件夹下。
- 规则文件一般情况下不需要改变，如果需要，则您只需要按照需求填入即可：
   ```toml
   # 核心配置
   [check]
   # 检查频率 默认为0.5秒
   check_frequency = 0.5
   # 是否自动清理被禁止的方块
   fast_handle = false
   # 是否统计蓝图内方块信息，会占用一定性能，但可以可视化
   count_block = false
   # 是否剔除蓝图内的全部实体，这会导致创造打印蓝图不包含实体，但是可以杜绝全部实体相关的复制漏洞
   kill_entity = true
   # 禁止的实体，填入后将会剔除蓝图内的此实体
   ban_entity = [
   "minecraft:armor_stand"
   ]
   # 禁止的tag，由于nbt的递归隐藏机制，如果填入的tag在蓝图内检测到，就会将蓝图清空，因为nbt数据结构无法针对tag剔除进行修复
   ban_tags = [
   "AttributeModifiers",
   "Enchantments",  # 附魔标签，这会阻止创造蓝图，但也会导致蓝图不能带有附魔特性，因为它们的结构相同
   "using_converts_to",  # 食物标签，阻止返回复制特性
   "bundle_contents"  # 存储袋标签，阻止复制特性
   ]
   # 禁止的方块，填入后将会剔除蓝图内的此类方块，如果剔除不完全，则会清空蓝图
   ban_block = [
   "create:creative_crate",
   "create:creative_fluid_tank",
   "create:creative_motor",
   "create:creative_blaze_cake",
   "create:handheld_worldshaper",
   "minecraft:command_block",  # 不多说了，这玩意是命令方块
   "minecraft:kelp"  # 这可以阻止绝大多数gt机，他们极其卡顿！
   ]
   
   # 实验功能，可以在发现异常蓝图后推送smtp邮箱，免费又好用，还能利用免费的推送服务！
   [smtp]
   # 是否启用，true 或 false
   enable = false
   # 接收报警的邮箱，所有报警信息都会发送到这个邮箱！
   email_receive = "example@qq.com"
   # smtp的默认根服务器，一般情况不需要改
   smtp_server = "smtp.qq.com"
   # smtp的默认服务器端口，一般情况不需要改
   smtp_port = 587
   # 使用哪个邮箱进行发送，报警信息会从这个邮箱发出
   smtp_sender_email = "<EMAIL>"
   # 这个邮箱的smtp密码，需要在qq邮箱网页版获取
   smtp_password = "<PASSWORD>"
   ```



---

## 针对已知问题的修复

- **1. 修复** 使用讲台、剪贴板打印出创造物品的恶性漏洞。
  - [X] [视频链接](https://www.bilibili.com/video/BV1sDp4ePEVp)

- **2. 修复** 阀门数据篡改导致的变量缓存溢出，这会导致阀门实体内存无法回收，同时导致内存泄漏。（无限阀门旋转特性）  
  - [X] [视频链接](https://www.bilibili.com/video/BV1UdC9YjET5)

- **3. 修复** 剪贴板复制特性，附魔标签剔除 | 这可以复制创造物品，打印带创造属性的剪贴板  
  - [X] [视频链接](https://www.bilibili.com/video/BV1SXC9YEEeW)

- **4. 修复** 阻止传送带修改打印上限超过一千格 | 这会导致渲染器溢出崩溃客户端，传送带的弱加载区块会卡服
  - [X] [视频链接](https://www.bilibili.com/video/BV1SXC9YEEeW)
- **5. 修复** 阻止打印 -1 长度传送带 | 这会在旧版本崩溃 Forge 原生服务器。  
  - [X] [视频链接](https://www.bilibili.com/video/BV1u9ytY2E8R)

- **6. 修复** 恶意利用修改传送带长度导致的巨量传送带破区块 | 这会导致服务器极其卡顿，破坏 CPU，崩溃服务器，还有概率导致存档损坏  
  - [X] [视频链接](https://www.bilibili.com/video/BV1NwybY3ERY)

- **7. 修复** 锦致装饰的翁在旧版本的无限 GT 特性，可以无限开宝藏 | 使用 GT 会导致服务器巨量卡顿  
  - [X] [视频链接](https://www.bilibili.com/video/BV1LUS9YCEk1)

- **8. 修复** 恶意修改弹射置物台弹射力量产生的屠龙炮 | 这会直接导致 Forge 端崩服，极快的弹射速度会导致服务器直接卡死，弹射路径的区块全部会极快的生成  
  - [X] [视频链接](https://www.bilibili.com/video/BV1itXDY3EwJ)  
  - [X] [视频链接](https://www.bilibili.com/video/BV13RKneEEFG)

- **9. 修复** 恶意修改过滤器使其内部 NBT 过大 | 将其放进漏斗会导致其在判定时产生极高的游戏卡顿刻
  - [X] 暂时还没视频
- **10. 修复** 恶意修改超长的链式传动轮匹配 | 这会导致巨量的卡服问题  
  - [X] [视频链接](https://www.bilibili.com/video/BV1vz9bY7EW5)

- **11. 修复** 添加标签 use convert to 会返回任意创造物品的漏洞  
  - [X] [视频链接](https://www.bilibili.com/video/BV1c19tYsEBL)

- **12. 修复** 篡改动力臂距离到不合理的距离导致的超远距离传输 | 在某些情况下会因为距离问题直接导致崩服  
  - [X] [视频链接](https://www.bilibili.com/video/BV1XpXYYDEt7)

- **13. 修复** 篡改链式传动轮的匹配目标到虚空导致的 90° 锁链 | 这会在某些情况下崩服  
  - [X] [视频链接](https://www.bilibili.com/video/BV1nddcYSEWQ)

- **14. 修复** 传送带的奇怪扭曲形式导致的传送带与传动杆复制  
  - [X] [视频链接](https://www.bilibili.com/video/BV1omdmYrE3S)

- **15. 修复** 恶意修改链式传动轮的匹配对象到几千个时导致的服务器巨量卡顿和锁链复制特性  
  - [X] [视频链接](https://www.bilibili.com/video/BV1Ze5Wz7EB2)

- **16. 修复** 恶意修改机械手的返还物品导致的无限产出漏洞(比如为机械手加入safenbt列表)  
  - [X] [视频链接](https://www.bilibili.com/video/BV1udtYzwEQN)

- **17. 修复** 机械手在伪装板模组下的物品复制特性  
  - [X] [视频链接](https://www.bilibili.com/video/BV1dubezLEp7)

- **18. 修复** 在安山漏斗可以返还过滤器的漏洞
  - [X] [视频链接](https://www.bilibili.com/video/BV1UrEGzmEDU)
- **19. 修复** 可以使用烧毁标签来获得创造物品的漏洞  
  - [X] [视频链接](https://www.bilibili.com/video/BV1UrEGzmEDU)

- **20. 修复** 可以使用实体（盔甲架）导致的任何复制特性和获取创造物品特性 | 使用实体剔除功能  
  - [X] [视频链接](https://www.bilibili.com/video/BV1wtRNYaE5m)

- **21. 修复** 轧机在物品附加的某个发行版本创造物品恶性获取漏洞  
  - [X] [视频链接](https://www.bilibili.com/video/BV1b4eyzGEoj)

- **22. 修复** 附魔工业在某个版本会打印创造标签的恶性附属漏洞  
  - [X] [视频链接](https://www.bilibili.com/video/BV1GKemzWEKm)

- **23. 修复** 创意传动会打印无限能源电池的特性  
  - [X] [视频链接](https://www.bilibili.com/video/BV193vAzNEJ2)

- **24. 修复** 集成农业会打印鸡舍实现 GT 机卡服的特性  
  - [X] [视频链接](https://www.bilibili.com/video/BV13nh2z5EvT)

- **25. 修复** 利用nbt漏洞实现的运行指令恶性bug
  - [X] [视频链接](https://www.bilibili.com/video/BV1rZY5z1Eo6)

- **26. 修复** 6.0.+版本下可以篡改流体储罐导致容量异常的bug

  - [X] [视频链接](https://www.bilibili.com/video/BV1hSnMzCE6A/)


- **27. 修复** 夸克铁丝网的无限岩浆漏洞。
  - [X] [视频链接](https://www.bilibili.com/video/BV1hSnMzCE6A/)


- **28. 修复** 通过修改动力合成器实现的虚空传送物品漏洞。 
  - [X] [视频链接](https://www.bilibili.com/video/BV1hSnMzCE6A/)


- **29. 修复** 通过修改动力合成器实现的虚空传送物品漏洞。
  - [X] [没找到样例视频]()

    
- **30. 修复** 通过修改强力胶导致的超大黏着检测引起的服务器巨量卡顿甚至崩溃。
  - [X] [没找到样例视频]()

- **31. 修复** 通过修改机壳地盘、斜向地盘导致的超大黏着检测引起的服务器巨量卡顿甚至崩溃。
  - [X] [没找到样例视频]()
  - 
- **32. 检测** 通过修改机械动力：火炮的炮弹引信导致的获取任意创造物品的漏洞。
  - [X] [没找到样例视频]()


## 致谢
 - 特别感谢 crackun24 
   - 参考了Mixin部分代码。
 - 特别致谢：
   - 起飞的玫瑰、恐鱼、air、crackun24、runner、CTR服主 等总计14个机械动力公益服服主，它们为这个脚本提供了检查样本和后续辅助处理。
---
 - 特别特别致谢 B站 up主 一只不屑的屑蜘蛛 
   - **如果不是这 sb 熊孩子故意用蓝图崩了作者开的公益服好几次、在不知道多少服传bug蓝图破坏服务器、在作者在b站发修复时给作者拉黑、还在b站造谣诋毁作者，也不会有这个项目，蓝图bug也不会这么快有不错的解决方案！**


## 依赖
- Minecraft
- Create

---

### 优点：
- 包含Python项目的全部优点。
### 缺点：
- 版本覆盖支持不全，针对1.18以及更低版本不支持，这些版本需要使用python版本

---
