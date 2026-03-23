# CSC Create: Schematic Checker  

---

### ~~A countermeasure against griefers who use bugged schematics (just kidding)~~  

1. Welcome to CSC. CSC (formerly known as Create:SchematicChecker | Create: Schematic Validation) is a dedicated schematic-scanning mod tailored for Create and all its addons. It can block any recorded potential/malicious vulnerabilities, including but not limited to: item duplication, server lag or even crashes, obtaining creative items, and gaining control of the server. Triggering these vulnerabilities only requires uploading schematics with fixed modified parameters, which makes sabotaging the server extremely easy. As long as the server does not disable schematic cannons, anyone can randomly sabotage the server, crash it, obtain any creative items, and permanently damage any Create survival server—with extremely low costs and minimal time. This mod is designed specifically to address this issue, providing a permanent solution to all problems related to Create schematics!
CSC allows users to customize blacklisted tags and blocks, define custom removal rules, and configure and customize the detailed results of schematic validation. Thanks to the universality of schematics, as long as the configuration file is properly designed, CSC can detect any cheating schematics from Create and all its addon mods.

2. If the user agrees, CSC can even optionally connect to the internet to automatically sync rules to the local server. Whenever the CSC team discovers a new vulnerability, the configuration file will be updated automatically—enabling the automatic removal of potential vulnerabilities without the need to restart the server.



<img width="1920" height="1080" alt="439597651-6bdcd06c-c454-41f0-aa1d-7f8b882064f8" src="https://github.com/user-attachments/assets/85848717-c13d-4c70-8049-6c136a387021" />



## Q&A / Bug Reports / Support | QQ Group: 1061133894  
---

## Overview  
This is a Java-based addon (mod) for Create (Create: Mechanics of Engineering).  
All of this mod’s detection features run **completely asynchronously**, so they do not consume any of the server’s main thread resources!  

It supports custom rules and automatically scans all schematics in the schematic folder for bugs and anomalies, preventing any maliciously tampered NBT schematics from entering your Minecraft server.  

It fixes a vast number of Create schematic-related vulnerabilities—including numerous potential blueprint bugs in the latest versions. All relevant fixes and sources of issues are listed at the end.  
![Fixed Create Vulnerabilities](https://github.com/duckgun13476/CreateSchematicsChecker-Python?tab=readme-ov-file#%E9%92%88%E5%AF%B9%E5%B7%B2%E7%9F%A5%E9%97%AE%E9%A2%98%E7%9A%84%E4%BF%AE%E5%A4%8D)  
![Usage Guide](https://github.com/duckgun13476/CreateSchematicsChecker-Python?tab=readme-ov-file#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95)  


## Features  
- **Automatic Scanning**: Automatically checks all schematics in the schematic folder, identifies and filters out potential bugged or anomalous schematics to ensure server security.  
- **Highly Customizable Rules**: Allows users to define custom rules for targeted NBT scanning of specific mods.  
- **Conveyor Belt Tampering Validation**: Uses a full-validation algorithm to verify conveyor belts in versions 0.5.1 and 6.0.0, blocking bugged conveyor schematics (e.g., duplication, server lag, or crash exploits).  
- **Gear/Cogwheel Pairing Validation**: Implements basic matching logic for gear/cogwheel pairs in versions 6.0.0+, preventing known schematic bugs and duplication exploits.  
- **Malicious NBT Scanning**: Detects various types of maliciously tampered NBT values to ensure schematic safety.  
- **Automatic Cloud Sync**: Automatically updates NBT check rules to block newly discovered bugged schematics.  
- **Auto-Rule Updates**: Fully implements automatic rule updates to keep protection up-to-date.  


---

## Usage:  
#### Select the appropriate version based on your Create mod version ↓  


## Custom Rules & Configuration  

- Log files are stored in the `log` folder; all uploaded schematics are saved in the `save` folder.  
- Rule files generally do not need modification, but if customization is required, simply edit the following fields as needed:  
   ```toml
   # Core Configuration
   [check]
   # Scanning frequency (default: 0.5 seconds)
   check_frequency = 0.5
   # Whether to automatically remove banned blocks
   fast_handle = false
   # Whether to count block data in schematics (uses minor resources but enables visualization)
   count_block = false
   # Whether to remove all entities from schematics (disables entity spawning in creative schematics but blocks all entity-related duplication exploits)
   kill_entity = true
   # Banned entities (entities listed here will be removed from schematics)
   ban_entity = [
   "minecraft:armor_stand"
   ]
   # Banned tags: Due to NBT’s recursive hiding mechanism, if a listed tag is detected, the entire schematic will be cleared (NBT structures cannot be partially fixed for tag removal)
   ban_tags = [
   "AttributeModifiers",
   "Enchantments",  # Enchantment tags (blocks creative schematics but also prevents enchanted properties in schematics, as their structures are identical)
   "using_converts_to",  # Food tags (blocks return/duplication exploits)
   "bundle_contents"  # Bundle tags (blocks duplication exploits)
   ]
   # Banned blocks (blocks listed here will be removed from schematics; if removal fails, the entire schematic will be cleared)
   ban_block = [
   "create:creative_crate",
   "create:creative_fluid_tank",
   "create:creative_motor",
   "create:creative_blaze_cake",
   "create:handheld_worldshaper",
   "minecraft:command_block",  # Speaks for itself—this is a command block
   "minecraft:kelp"  # Blocks most GT machines (they cause severe lag!)
   ]
   ```  


---

## Fixes for Known Issues  

- **1. Fixed**: Exploit allowing creative items to be printed via Lecterns or Clipboards.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1sDp4ePEVp)  

- **2. Fixed**: Valve data tampering causing variable cache overflow (leads to uncollectible valve entity memory and memory leaks, i.e., "infinite valve spinning" exploit).  
  - [X] [Video Link](https://www.bilibili.com/video/BV1UdC9YjET5)  

- **3. Fixed**: Clipboard duplication exploit (enchantment tag removal) | Allows duplication of creative items and printing of clipboards with creative properties.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1SXC9YEEeW)  

- **4. Fixed**: Blocked conveyor belt length modifications exceeding 1000 blocks | Causes renderer overflow (client crashes) and lag from weakly loaded conveyor chunks.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1SXC9YEEeW)  

- **5. Fixed**: Blocked printing of conveyor belts with -1 length | Crashes vanilla Forge servers in older versions.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1u9ytY2E8R)  

- **6. Fixed**: Malicious conveyor length modifications causing massive conveyor "chunk breaking" | Leads to severe server lag, CPU damage, server crashes, and potential world corruption.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1NwybY3ERY)  

- **7. Fixed**: Infinite GT (Game Tick) exploit with Kintsugi Decor’s Urns in older versions (allows infinite treasure opening) | GT exploits cause extreme server lag.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1LUS9YCEk1)  

- **8. Fixed**: Maliciously modified Ejector Plate launch force creating "dragon-slaying cannons" | Instantly crashes Forge servers; ultra-fast launches cause immediate server freezes and rapid chunk generation along the launch path.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1itXDY3EwJ)  
  - [X] [Video Link](https://www.bilibili.com/video/BV13RKneEEFG)  

- **9. Fixed**: Maliciously modified Filters with excessively large NBT data | Causes extreme game tick lag when placed in Hoppers.  
  - [X] No video available yet  

- **10. Fixed**: Maliciously modified超长 chain drive wheel pairings | Causes severe server lag.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1vz9bY7EW5)  

- **11. Fixed**: Exploit adding the "using_converts_to" tag to obtain arbitrary creative items.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1c19tYsEBL)  

- **12. Fixed**: Tampered Mechanical Arm distances causing ultra-long-range item transport | Crashes servers in certain scenarios.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1XpXYYDEt7)  

- **13. Fixed**: Tampered chain drive wheels targeting the void causing 90° chain glitches | Crashes servers in certain scenarios.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1nddcYSEWQ)  

- **14. Fixed**: Oddly distorted conveyor belts causing conveyor/rod duplication.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1omdmYrE3S)  

- **15. Fixed**: Maliciously modified chain drive wheels with thousands of targets causing severe server lag and chain duplication.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1Ze5Wz7EB2)  

- **16. Fixed**: Maliciously modified Mechanical Arms with infinite item output exploits (e.g., adding "safenbt" lists to Arms).  
  - [X] [Video Link](https://www.bilibili.com/video/BV1udtYzwEQN)  

- **17. Fixed**: Mechanical Arm item duplication with the Camouflage Panels mod.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1dubezLEp7)  

- **18. Fixed**: Exploit allowing Filters to be retrieved via Andesite Hoppers.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1UrEGzmEDU)  

- **19. Fixed**: Exploit using "burn" tags to obtain creative items.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1UrEGzmEDU)  

- **20. Fixed**: Any duplication or creative item exploits involving entities (Armor Stands) | Uses entity removal feature.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1wtRNYaE5m)  

- **21. Fixed**: Exploit allowing creative item acquisition via Rollers in specific versions.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1b4eyzGEoj)  

- **22. Fixed**: Malicious addon exploit in Enigmatica: Industrialization allowing creative tag printing.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1GKemzWEKm)  

- **23. Fixed**: Create: Crafts & Additions exploit allowing infinite energy cell printing.  
  - [X] [Video Link](https://www.bilibili.com/video/BV193vAzNEJ2)  

- **24. Fixed**: Integrated Dynamics exploit allowing Chicken Coop printing (causes GT machine lag).  
  - [X] [Video Link](https://www.bilibili.com/video/BV13nh2z5EvT)  

- **25. Fixed**: Malicious NBT exploit allowing command execution.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1rZY5z1Eo6)  

- **26. Fixed**: Exploit in version 6.0.+ allowing fluid tank capacity tampering.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1hSnMzCE6A/)  

- **27. Fixed**: Quark Wire Mesh infinite lava exploit.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1hSnMzCE6A/)  

- **28. Fixed**: Exploit allowing item teleportation to the void via modified Mechanical Synthesizers.  
  - [X] [Video Link](https://www.bilibili.com/video/BV1hSnMzCE6A/)  

- **29. Fixed**: Exploit allowing item teleportation to the void via modified Mechanical Synthesizers.  
  - [X] [No sample video found]()  

- **30. Fixed**: Maliciously modified Super Glue causing excessive adhesion checks (leads to severe server lag or crashes).  
  - [X] [No sample video found]()  

- **31. Fixed**: Maliciously modified Casing Chassis/Angled Chassis causing excessive adhesion checks (leads to severe server lag or crashes).  
  - [X] [No sample video found]()  

- **32. Detected**: Exploit allowing arbitrary creative item acquisition via modified Create: Cannons (tampered fuse data).  
  - [X] [No sample video found]()  

- **33. Detected**: Fixed a bug where printing giant blocks from the Twilight Forest mod would ignore the terrain and break bedrock.
  - [X] [No sample video found]()

## Acknowledgments  
 - Special thanks to **crackun24**  
   - For reference Mixin code.  
 - Additional thanks to:  
   - Qifei de Meigui (Rose of Takeoff), Kong Yu (Fearful Fish), air, crackun24, runner, CTR Server Owner, and 17 other Create public server owners—who provided test samples and post-fix support for this project.  

---

 - **Extra Special Thanks** to Bilibili UP OWNER (content creator) **一只不屑的屑蜘蛛** (A Disdainful Trash Spider)  
   - *If this griefer hadn’t intentionally crashed the author’s public server multiple times with bugged schematics, spread bugged schematics across countless servers, blocked the author on Bilibili, and spread rumors to slander the author when the fix was posted—this project would never have existed, and Create schematic bugs would not have been resolved so quickly!*  


## Dependencies  
- Minecraft  
- Create  


---

### Pros:  
- Inherits all advantages of the Python version of the project.  

### Cons:  
- Incomplete version support: Does not support versions 1.18 or lower. Use the Python version for these versions.
