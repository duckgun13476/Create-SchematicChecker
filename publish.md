# Create: Schematic Checker (CSC)

Create: Schematic Checker is a server-side protection mod for Create
schematics. It scans
uploaded schematics and blocks or sanitizes known malicious NBT patterns before
they can be printed into a world.

CSC is designed for public and survival servers that allow schematic uploads.
It protects against known Create schematic exploits such as item duplication,
creative-item acquisition, server lag, crashes, malformed contraptions, and
dangerous addon NBT.

<p align="center">
<a href="https://discord.gg/rQV5JPauY7"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/social/discord-singular_vector.svg" alt="Discord Server"></a>
<a href="https://github.com/duckgun13476/Create-SchematicChecker"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/social/github-singular_vector.svg" alt="GitHub Repository"></a>
</p>

![CSC preview](https://github.com/user-attachments/assets/85848717-c13d-4c70-8049-6c136a387021)

## Support

- Discord: https://discord.gg/rQV5JPauY7

## What It Does

- Scans schematics asynchronously, without blocking the Minecraft server main thread.
- Detects and blocks known malicious Create schematic exploits.
- Sanitizes configurable NBT fields, tags, blocks, items, and entities.
- Validates Create belts, chain drives, mechanical arms, filters, fluid tanks, clipboards, and other high-risk block entities.
- Supports custom local rules through `config/CSC/user_rule.json`.
- Can optionally sync online rule updates when enabled by the server owner.
- Keeps logs and optional schematic backups for auditing and later investigation.

## Supported Versions

Choose the build that matches both your Minecraft version and Create version.

| Loader | Minecraft | Create target |
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

## Dependencies

- Minecraft
- Create

Some builds may require loader-specific Create dependencies such as Flywheel,
Ponder, or Registrate, depending on the target Minecraft/Create version.

## Installation

1. Download the CSC jar for your loader, Minecraft version, and Create version.
2. Put the jar into the server `mods` folder.
3. Start the server once to generate `config/CSC/config.toml`.
4. Review the generated configuration before opening schematic uploads to players.
5. Use `/csc reload` after editing the config.

## Configuration

Main config file:

```text
config/CSC/config.toml
```

Important options:

| Option | Purpose |
| --- | --- |
| `core.Enable` | Enables or disables CSC checks. |
| `core.BanBlock` | Blocks or sanitizes high-risk blocks and items. |
| `core.BanTag` | Removes or blocks dangerous NBT tags. |
| `core.KillEntity` | Removes entities from schematics to prevent entity-based exploits. |
| `core.WhiteListModEnable` | Allows only listed mod IDs to keep NBT. Recommended for public servers. |
| `debug.EnableBackup` | Saves uploaded schematics for later auditing. Recommended. |
| `online.enableAutoUpdate` | Allows CSC to fetch cloud rule updates. Disabled by default. |
| `online.enableManualConfig` | Enables custom rules from `config/CSC/user_rule.json`. |

Manual custom rule file:

```text
config/CSC/user_rule.json
```

Online cached rules:

```text
config/CSC/online/
```

## Commands

| Command | Description |
| --- | --- |
| `/csc` | Shows basic status. |
| `/csc help` | Shows help and feedback links. |
| `/csc reload` | Reloads CSC configuration and rules. |
| `/csc list` | Shows rule-list help. |
| `/csc list IdMatchRuleAll` | Lists all ID match rules. |
| `/csc list OperateMatchRuleAll` | Lists all NBT operation rules. |
| `/csc DisableTemp` | Temporarily disables checks for 5 minutes. |
| `/csc Enable` | Restores CSC after temporary disable. |
| `/csc notice whitelistid on|off` | Toggles whitelist-mode scan notice. |
| `/csc mode whitelistid on|off` | Toggles mod ID whitelist mode. |

## Known Exploit Coverage

CSC covers many reported Create schematic vulnerabilities and addon-specific
NBT exploits.

| # | Status | Issue |
| --- | --- | --- |
| 1 | Fixed | Creative items printed through Lecterns or Clipboards. |
| 2 | Fixed | Valve data tampering causing cache overflow and memory leaks. |
| 3 | Fixed | Clipboard duplication and creative-property clipboards. |
| 4 | Fixed | Conveyor belt length tampering beyond 1000 blocks. |
| 5 | Fixed | Conveyor belts with `-1` length crashing older Forge servers. |
| 6 | Fixed | Massive malicious conveyor chains causing lag, crashes, or world damage. |
| 7 | Fixed | Kintsugi Decor Urn infinite game-tick exploit. |
| 8 | Fixed | Weighted Ejector launch-force tampering. |
| 9 | Fixed | Oversized Filter NBT causing extreme tick lag. |
| 10 | Fixed | Overlong chain-drive pairings causing server lag. |
| 11 | Fixed | `using_converts_to` tag creative-item exploit. |
| 12 | Fixed | Mechanical Arm distance tampering. |
| 13 | Fixed | Chain drives targeting invalid positions. |
| 14 | Fixed | Distorted conveyor belts causing belt or shaft duplication. |
| 15 | Fixed | Chain drives with thousands of targets causing lag and duplication. |
| 16 | Fixed | Mechanical Arm infinite output through tampered safe-NBT data. |
| 17 | Fixed | Mechanical Arm duplication with camouflage panel mods. |
| 18 | Fixed | Filter retrieval through Andesite Hoppers. |
| 19 | Fixed | Creative-item acquisition through burn tags. |
| 20 | Fixed | Entity-based duplication and creative-item exploits, including Armor Stands. |
| 21 | Fixed | Roller-related creative item exploit in specific versions. |
| 22 | Fixed | Create: Enchantment Industry creative-tag printing. |
| 23 | Fixed | Create Crafts & Additions infinite energy-cell printing. |
| 24 | Fixed | Integrated Farming Chicken Coop GT lag exploit. |
| 25 | Fixed | Command-execution NBT exploit. |
| 26 | Fixed | Create 6.0+ fluid tank capacity tampering. |
| 27 | Fixed | Quark Wire Mesh infinite lava exploit. |
| 28 | Fixed | Item teleportation to the void through modified mechanical crafters/synthesizers. |
| 29 | Fixed | Additional void item-teleport exploit variants. |
| 30 | Fixed | Super Glue adhesion-check abuse causing heavy lag or crashes. |
| 31 | Fixed | Chassis and angled-chassis adhesion-check abuse causing heavy lag or crashes. |
| 32 | Detected | Create: Big Cannons fuse-data tampering for creative-item acquisition. |
| 33 | Fixed | Twilight Forest giant block printing bypassing terrain and breaking bedrock. |

## Acknowledgments

Special thanks to:

- **crackun24** for Mixin reference code.
- Qifei de Meigui, Kong Yu, air, crackun24, runner, CTR server owner, and other Create public server owners for test samples and post-fix support.
- Some CMS blueprint-site partner creators. Their decision to block the author after seeing schematic bug fixes also became part of the motivation to keep this project updated.
- Bilibili creator **一只不屑的屑蜘蛛**. The repeated public-server schematic attacks and spread of exploit samples directly motivated this project and helped accelerate fixes for Create schematic vulnerabilities.

## License

This project is licensed under GNU LGPL 3.0.

