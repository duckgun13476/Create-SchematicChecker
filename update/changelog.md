## 2.27.37-release
1. Rework abnormal-schematic reporting around the authenticated v2 API, with retry retention after upload failures.
2. Add a runtime-configurable, cross-version switch for the Quark fluidlogged-lava duplication repair. (#11)
3. Restore Sable/Create Aeronautics cross-sublevel fluid pumping on NeoForge 1.21.1. (#12)
4. Fix Create radial-wrench, chain-conveyor rider, and filter-item packet exploits.
5. Prevent CSC scan/report tasks from being submitted while the server is stopping; fix the NeoForge chain-rider startup crash.
6. Harden malformed schematic palette handling and preserve valid air/Copycats data.
7. Add modern Create Big Cannons fuze-component compatibility, including valid empty components.
8. Refresh migrated runtime configuration, complete translations, and make Create packet guards configurable.
9. Improve legacy dependency caching and idempotent/streaming release uploads.

## 1.21.27-release
1. Fix Quark fluidlogged grate lava duplication on Create 6 / NeoForge 1.21.1.
2. Fail closed when a schematic cannot be read or checked, and validate uploaded schematic paths before accessing files.
3. Close schematic NBT input and output streams reliably.

## 1.21.26-release
1. Add detection for tripwire string duper schematics.
2. Sanitize tripwire and tripwire hook powered states after schematic checks.
3. Refactor independent NBT check modules into separate checker classes.
4. Stop packaging and publishing the unmaintained Forge 1.14.4 build.

## 1.21.25-release
1. Fix data structure can't be read in the new version.(>1.20.1)
2. Full fix for all version clipboards.

## 1.21.24-release
1. Fix config overwrite mistake when launch game. (#10)

## 1.21.23-release
1. Fix unknown entity crash when schematic entities have no id.
2. Remove invalid entities without id and output a warning.
3. Add missing entity id warning translations.

## 1.21.22-release
1. Add config migration after version updates.
2. Regenerate config files after version updates and notify operators about the migration.
3. Add missing Create: Aeronautics compatibility entries.

## 1.21.21-release
1. Add whitelist mode scan-complete notice and config/command switch.
2. Fix the console format mistake.
3. Fix the reload delay problem.
4. Fix low version refmap problem.
5. Fix some block can't print in Create:Aeronautics. All blocks can be printed now.

## 1.21.20-release-hotfix
1. Fix fabric map mistake (crash).
2. Add support mark for Quilt (need fabric api and choose ignore java versions).
3. Fix script problem (1.15.2)

## 1.21.19-release-hotfix
1. Fix layer not in the release version.

## 1.21.18-release
1. Reworked the project structure for multi-version support.
2. Added support for Forge 1.14.4, 1.15.2, 1.16.5, 1.17.1, 1.18.2, 1.19.2, and 1.20.1.
3. Added support for Fabric 1.18.2, 1.19.2, 1.20.1, and 1.21.1.
4. Added support for NeoForge 1.21.1.
5. Fixed an issue in the Create filter removal logic.
6. Added an experimental checker for Create: Aeronautics


## 0.21.18-release
1. Fix the wrong remove in Create filter.

## 0.21.17-release
1. Fix Mekanism structure destroyed in upload.

## 0.21.16-release
1. Modify translate.
2. Add support for fluid tank.
3. Fix config register method.
4. Add Japanese language support.

## 0.21.15-release
1. Fix 1.21.1 neoforge clipboard problem.
2. Fix config crush problem.

## 0.21.14-release
1. Add compat for pattern schematic.
2. Fix check logic to maintain thread robust.

## 0.21.13-release
1.Add supportive to Axiom copycats place.

## 0.21.12-release
1. Add a degree config para to allow some blueprint.
2. Fix neoforge blueprint can't check in forge.
3. Add a degree show when conveyor more than 45°.

## 0.21.11-release
1. Fix a check problem in CopyCats.

## 0.21.10-release
1. Fixes ban entity do not work.

## 0.21.9-release
1. Fix a wrong check on copycats.
2. Add a broken schematic fix.
3. Add problem schematic upload.

## 0.21.8-beta
1. Fix a crush problem.
2. Add problem uploaded hook.

## 0.21.7-beta
1. Add a new method; now you can choose a whitelist of which mod should save nbt. All other mods will not print with nbt, which can prevent all unknown mod bugs like palette data.
2. Fix a bug about block output.
3. Supply missing translating.

## 0.21.6-beta
1. Remove junk Output.
2. Add a new function. Now you can use /csc help to know what's new.
3. Fix a bug when checking copycats.

## 0.21.5-beta
1. Fix Neoforge clipboard check problem and also strength another version.

## 0.21.4-beta
1. Fix a copyCats error print problem.

## 0.21.3-beta
1. Change Default language for a problem.
2. Fix when change track shape causes a render problem and crash potential risk in 0.5.1j and before.

## 0.21.2-alpha
1. Fix copycats check bug in the first rule.
2. Fix many problems at base\core.

## 0.21.1-alpha
1. Fix some bugs in the online method.
2. Add more check method from twilight forest.
3. Add more capability when using 6.0.+ versions schematic upload checking in low Create versin.

## 0.21.0-alpha
1. Add a stable version.
2. Fix many bugs.

## 0.20-demo
1. Plant core.

