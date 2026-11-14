# Issue #14: TorqueAPI discovery on Forge 1.16.5

## Scope and cause

Source baseline: `dd0e00aa27fe17cfef4c31b1efc7544441b65c11` on `forge-combined-main`.
Issue #14 reports that the latest 1.16.5 build still asks for TorqueAPI 1.2.2; it does not identify an exact artifact. Reproduction therefore used the current published Modrinth 1.16.5 build, version `2.27.47-0.3.2g` (version ID `xmb6zcjN`). Its SHA-256 is `13071a015a5ee966074c119247ec717b1533bb8135fb4fbb1f2669b3f99f6d76`.

Forge 36 does not discover the nested mod through the old `ContainedDeps` manifest attribute. The original artifact contains TorqueAPI but its mod does not appear in Forge's discovered mod list. Removing the mandatory dependency would hide the failure, not supply the used API.

The fix registers a Forge 36 `IModLocator`, returning both CSC itself (which Forge excludes from the ordinary mods-folder locator once it supplies a locator) and its bundled API. Only this 1.16.5 variant uses the new packaging script. An external `torqueapi` mod takes precedence, regardless of filename; Forge still enforces its version. The extracted jar is content-addressed, integrity-checked, and never overwrites a different cached version.

## Verification performed (2026-11-14)

Environment: isolated Linux amd64, Java 16.0.2, Minecraft 1.16.5 / Forge 36.2.42, Create v0.3.2g, Flywheel 1.16-0.2.5. No existing world or user workspace was used.

Real client launch with `-Dfml.earlyprogresswindow=false` reached Forge mod discovery:

| Case | Observed result |
| --- | --- |
| Original published CSC, only nested API | CSC discovered; TorqueAPI absent; 1 mandatory requirement missing |
| Original CSC + identical nested API copied to mods | TorqueAPI 1.2.2 discovered; 0 requirements missing |
| Patched fixture, no external API | CSC and bundled TorqueAPI 1.2.2 discovered; 0 requirements missing |
| Patched fixture + externally renamed API jar | Exactly the external TorqueAPI discovered alongside CSC; 0 requirements missing, no duplicate mod |

The patched fixture was the published jar with the independently compiled locator/service added and the unchanged nested API moved to the new fixed path. It was **not** a complete source-built release artifact. No test binaries are included in this change.

The bootstrap source and test harness compiled using Java 16 and the official installed Forge libraries. `TorqueApiLocatorTest: all checks passed`. Coverage includes embedded-only behavior, misleading filenames/comments, renamed external jars, multi-mod jars, older external versions, extraction byte equality, cache reuse, damaged-cache recovery and preservation and missing-entry rejection. The harness is also wired to Gradle `check` as `testTorqueApiLocator`.

## Not yet verified

- Complete Gradle build/reobfuscation: downloading Gradle 7.4 was blocked by the execution environment's network route.
- Title screen, world entry and CSC gameplay: the client subsequently failed to initialize GLFW because this cloud environment has no usable display. Discovery success is not full gameplay acceptance.
- Server startup: stopped at the EULA gate; no agreement was accepted and no world was created.
- Other Minecraft/Forge variants are unchanged and were not retested.

Before release, run `:forge-1.16.5-0.3.2g:build` from the FG5 runner, then repeat both embedded-only and external-API client cases with the actual built jar on a graphical client, including world entry and CSC functionality.

## Cache recovery follow-up

The locator now serializes same-JVM callers and uses a per-content-hash file lock across processes. It validates a synced temporary jar before isolating a damaged regular cache file under a unique `.corrupt-*` name, then publishes with a same-directory atomic move. Quarantined bytes and other versions are preserved. Symbolic-link cache directories/targets and non-regular targets are rejected rather than followed or replaced. Atomic-move or I/O failures stop with a diagnostic; there is no partial-copy fallback. This does not claim crash-durability guarantees across all filesystems.

The expanded Java 16 harness passed recovery plus repeated reuse, eight concurrent threads, two child JVMs sharing a damaged cache, retained quarantine bytes, unrelated temporary-file preservation, invalid embedded identity before replacement, non-regular targets, and symbolic-link refusal. These tests are isolated fixtures, not user files.

A real Forge client discovery run with the updated patched fixture and an intentionally damaged isolated cache also passed: TorqueAPI 1.2.2 discovered, zero missing requirements, original damaged bytes retained under `.corrupt-*`, rebuilt bytes matching the embedded API. Complete source build/reobfuscation and GUI/world acceptance remain unverified as described above.
