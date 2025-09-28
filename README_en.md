
---

# CSC Mechanical Power: Blueprint Inspection

---

### ~~Killing the brat who plays with bug blueprints (bushi~~

![icon.png](icon.png)

## Q&A/Feedback/Help  QQ Group: 1061133894
---
## Overview
This is an add-on based on JAVA for mechanical power.
All detection functions of this MOD are completely asynchronous, so they will not occupy any server main thread performance!

It allows for custom rules. It can automatically screen all bug and abnormal blueprints in the blueprint folder to prevent any malicious NBT tampered blueprints from entering the Minecraft server.

It can fix a large number of mechanical power blueprint-related vulnerabilities, including a large number of potential blueprint vulnerabilities in the latest version. All related fixes and issue sources are listed at the end.
![Mechanical Power Vulnerabilities Fixed](https://github.com/duckgun13476/CreateSchematicsChecker-Python?tab=readme-ov-file#%E9%92%88%E5%AF%B9%E5%B7%B2%E7%9F%A5%E9%97%AE%E9%A2%98%E7%9A%84%E4%BF%AE%E5%A4%8D)
![How to Use](https://github.com/duckgun13476/CreateSchematicsChecker-Python?tab=readme-ov-file#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95)

## Features
- **Automatic Screening**: Automatically check all blueprints in the blueprint folder, identify and filter out potential bug and abnormal blueprints to ensure server security.
- **Highly Customizable Rules**: Allows users to customize rules to implement special NBT scans for specific mods.
- **Conveyor Belt Tampering Check**: Use full verification algorithms to verify conveyors in versions 0.5.1 and 6.0.0, preventing conveyor blueprint bugs, duplication, server lag, and crash features.
- **Gear Pairing Verification**: After version 6.0.0, perform gear pairing verification based on simple matching logic to prevent known blueprint bugs and duplication features.
- **Multiple Malicious NBT Screening**: Screen multiple malicious NBT tampering values to ensure blueprint security.
- **Automatic Cloud Sync**: Automatically update NBT inspection rules to leave no room for malicious new bug blueprints.
- **Automatic Rule Updates**: Automatic rule update feature implemented to ensure rules are always up to date.

---

## How to Use:
#### Choose the appropriate version according to your mechanical power version↓

## Custom Rules and Configuration

- Log files are in the `log` folder, and each uploaded blueprint is saved in the `save` folder.
- Rule files generally do not need to be changed. If needed, you only need to fill in as required:
   ```toml
   # Core Configuration
   [check]
   # Check frequency, default is 0.5 seconds
   check_frequency = 0.5
   # Whether to automatically clear prohibited blocks
   fast_handle = false
   # Whether to count block information in blueprints, which may occupy some performance but can be visualized
   count_block = false
   # Whether to remove all entities in blueprints, this will prevent blueprints from containing entities, but can eliminate all entity-related duplication vulnerabilities
   kill_entity = true
   # Prohibited entities, filling in will remove this entity from the blueprint
   ban_entity = [
   "minecraft:armor_stand"
   ]
   # Prohibited tags, due to the recursive hidden mechanism of nbt, if the filled tag is detected in the blueprint, the blueprint will be cleared because the nbt data structure cannot be repaired for tag removal
   ban_tags = [
   "AttributeModifiers",
   "Enchantments",  # Enchantment tag, this will prevent creative blueprints, but will also prevent blueprints from having enchantment features because their structures are similar
   "using_converts_to",  # Food tag, prevent duplication features
   "bundle_contents"  # Storage bag tag, prevent duplication features
   ]
   # Prohibited blocks, filling in will remove this type of block from the blueprint, if not completely removed, the blueprint will be cleared
   ban_block = [
   "create:creative_crate",
   "create:creative_fluid_tank",
   "create:creative_motor",
   "create:creative_blaze_cake",
   "create:handheld_worldshaper",
   "minecraft:command_block",  # Need I say more, this thing is a command block
   "minecraft:kelp"  # This can prevent the vast majority of gt machines, they are extremely laggy!
   ]
   
   # Experimental feature, can send smtp emails after finding abnormal blueprints, free and easy to use, and can also use free push services!
   [smtp]
   # Whether to enable, true or false
   enable = false
   # Email to receive alerts, all alert information will be sent to this email!
   email_receive = "example@qq.com"
   # Default root server for smtp, generally no need to change
   smtp_server = "smtp.qq.com"
   # Default server port for smtp, generally no need to change
   smtp_port = 587
   # Which email to use for sending, alert information will be sent from this email
   smtp_sender_email = "<EMAIL>"
   # Smtp password for this email, need to be obtained on the qq email web version
   smtp_password = "<PASSWORD>"
   ```

---

## Fixes for Known Issues

- **1. Fixed** Malicious bug using lecterns, clipboards to print creative items       
  [Video Link](https://www.bilibili.com/video/BV1sDp4ePEVp)

- **2. Fixed** Variable cache overflow caused by valve data tampering, will increase server cache slightly (infinite valve rotation feature)  
  [Video Link](https://www.bilibili.com/video/BV1UdC9YjET5)

- **3. Fixed** Clipboard duplication feature, enchantment tag removal | This can duplicate creative items, print creative clipboards  
  [Video Link](https://www.bilibili.com/video/BV1SXC9YEEeW)

- **4. Fixed** Prevent conveyor belt modification exceeding the printing limit of one thousand blocks | This will cause renderer overflow and crash the client, weak loading blocks of the conveyor belt will lag the server

- **5. Fixed** Prevent printing -1 length conveyor belts | This will crash Forge native servers in old versions.  
  [Video Link](https://www.bilibili.com/video/BV1u9ytY2E8R)

- **6. Fixed** Malicious modification of conveyor belt length leading to massive conveyor belt breaking blocks | This will cause severe server lag, CPU damage, server crashes, and a probability of archive damage  
  [Video Link](https://www.bilibili.com/video/BV1NwybY3ERY)

- **7. Fixed** Infinite GT feature of the Jinzhi Decorative Weng in old versions, can infinitely open treasure chests | Using GT will cause severe server lag  
  [Video Link](https://www.bilibili.com/video/BV1LUS9YCEk1)

- **8. Fixed** Malicious modification of the launching set to produce the dragon slayer cannon | This will directly crash Forge servers, extremely fast launch speed will cause the server to freeze, all blocks in the launch path will be generated extremely quickly  
  [Video Link](https://www.bilibili.com/video/BV1itXDY3EwJ)  
  [Video Link](https://www.bilibili.com/video/BV13RKneEEFG)

- **9. Fixed** Filter NBT bug causing high game lag when placed in a hopper

- **10. Fixed** Malicious modification of the chain drive wheel matching to a very large number causing severe server lag issues  
  [Video Link](https://www.bilibili.com/video/BV1vz9bY7EW5)

- **11. Fixed** Adding the tag use convert to will return any creative item bug  
  [Video Link](https://www.bilibili.com/video/BV1c19tYsEBL)

- **12. Fixed** Tampering with power arm distance to unreasonable distances causing long-distance transmission | In some cases, it may cause server crashes due to distance issues  
  [Video Link](https://www.bilibili.com/video/BV1XpXYYDEt7)

- **13. Fixed** Tampering with the matching target of the chain drive wheel to the void causing a 90° chain lock | This can crash the server in some cases  
  [Video Link](https://www.bilibili.com/video/BV1nddcYSEWQ)

- **14. Fixed** Conveyor belt's strange twisted form causing conveyor belt and transmission rod duplication  
  [Video Link](https://www.bilibili.com/video/BV1omdmYrE3S)

- **15. Fixed** Malicious modification of the matching objects of the chain drive wheel to thousands causing severe server lag and chain duplication features  
  [Video Link](https://www.bilibili.com/video/BV1Ze5Wz7EB2)

- **16. Fixed** Malicious modification of the retrievable items of the mechanical arm causing infinite output bug  
  [Video Link](https://www.bilibili.com/video/BV1udtYzwEQN)

- **17. Fixed** Item duplication feature of the mechanical arm under the camouflage board mod  
  [Video Link](https://www.bilibili.com/video/BV1dubezLEp7)

- **18. Fixed** Bug where filters can be returned in Anshan hoppers

- **19. Fixed** Bug where burning tags can be used to obtain creative items  
  [Video Link](https://www.bilibili.com/video/BV1UrEGzmEDU)

- **20. Fixed** Bug using entities (armor stands) causing any duplication features and obtaining creative items | Use entity removal feature  
  [Video Link](https://www.bilibili.com/video/BV1wtRNYaE5m)

- **21. Fixed** Rolling mill's malicious item acquisition bug in the latest version  
  [Video Link](https://www.bilibili.com/video/BV1b4eyzGEoj)

- **22. Fixed** Enchantment Industry's printing of creative tag malicious sub-bug  
  [Video Link](https://www.bilibili.com/video/BV1GKemzWEKm)

- **23. Fixed** Integrated agriculture's printing of chicken coop to achieve GT machine lag feature  
  [Video Link](https://www.bilibili.com/video/BV13nh2z5EvT)

- **24. Fixed** Exploiting NBT bug to run command malicious bug

  [Video Link](https://www.bilibili.com/video/BV1rZY5z1Eo6)

## Acknowledgements
- Special thanks to crackun24
    - Reference to some Mixin code.
- Special Thanks:
    - Rising Rose, Terrified Fish, Air, crackun24, Runner, CTR Server Owner, and a total of 14 mechanical power public welfare server owners, who provided inspection samples and subsequent auxiliary processing for this script.
---
- Special Special Thanks to Bilibili Uploader A Disdainful Spider
    - **If not for this annoying kid intentionally crashing the author's public welfare server several times with bug blueprints, destroying servers with bug blueprints on who knows how many servers, blacklisting the author when the author posted fixes on Bilibili, and spreading rumors defaming the author on Bilibili, there would not be this project, and there would not be such a good solution to blueprint bugs so quickly!**


## Dependencies
- Minecraft
- Create

---

### Pros:
- Contains all the advantages of the Python project.
### Cons:
- Incomplete version coverage support, not supported for versions 1.18 and lower, these versions need to use the Python version

---

This concludes the English translation of the provided text. If you need further assistance or have any more questions, feel free to let me know.