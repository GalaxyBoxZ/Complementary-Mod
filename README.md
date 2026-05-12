<div align="center">

# GBZ Combat

Visual-only synchronized combat animations for mixed Paper/Fabric multiplayer.

  <a href="">![Minecraft 1.21.4](https://img.shields.io/badge/Minecraft-1.21.4-4caf50?style=flat-square)</a>
  <a href="">![Java 21](https://img.shields.io/badge/Java%2021-ee9258?logo=coffeescript&logoColor=ffffff&labelColor=606060&style=flat-square)</a>
  
  <a href="">![Platform: Fabric Client](https://img.shields.io/badge/platform-Fabric%20Client-1976d2?style=flat-square)</a>
  <a href="">![PlayerAnimator](https://img.shields.io/badge/Animation-PlayerAnimator-c2185b?style=flat-square)</a>
  
  <a href="">[![Discord](https://img.shields.io/discord/816385202711560203.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2&style=flat-square)](https://discord.gg/KN9b3pjFTM)</a>

</div>

> [!IMPORTANT]
> GBZ Combat does **not** replace vanilla combat.
> It only synchronizes custom attack animations between players using the client mod.

## Overview

GBZ Combat is built for servers that want smoother, stylized melee visuals without forcing a combat overhaul on every player.

The plugin detects attack swings on the server, classifies the active weapon, and sends a lightweight sync packet only to nearby players that support the client mod. Vanilla players can join normally, fight normally, and keep seeing default Minecraft combat.

## Core goals

- Keep combat mechanics fully vanilla.
- Add synchronized custom attack animations for modded clients.
- Support mixed vanilla/modded servers cleanly.
- Keep the networking lightweight and expandable.
- Leave room for future sci-fi RPG presentation features without coupling them to combat logic.

## Non-goals

These systems are intentionally out of scope:

- Combos
- Stamina
- Cooldown rewrites
- Attack canceling
- Custom hit detection
- Heavy/light attack systems
- Dual wield logic
- Directional attacks
- Weapon abilities
- RPG combat mechanics

## Monorepo layout

| Module | Purpose |
| --- | --- |
| `plugin/` | Paper plugin for attack detection, client detection, weapon classification, commands, and packet fanout |
| `client/` | Fabric client mod that receives sync packets and plays local PlayerAnimator animations |
| `shared/` | Shared packet definitions, enums, channel ids, and binary serializers |

## How it works

1. A Fabric client with the GBZ Combat mod joins a normal Paper server.
2. The client sends a `gbzcombat:client_hello` payload during the play session.
3. The plugin marks that player as animation-capable.
4. PacketEvents listens for incoming arm swing packets without changing vanilla processing.
5. The plugin classifies the weapon category from the player’s held item.
6. The plugin sends an `attack_sync` packet only to nearby modded clients.
7. Each receiving client plays the matching animation locally with PlayerAnimator.

## Networking flow

### Client -> Server

Channel: `gbzcombat:client_hello`

Payload:

- `protocolVersion`
- `firstPersonEnabled`
- `smoothTransitionsEnabled`

### Server -> Client

Channel: `gbzcombat:attack_sync`

Payload:

- `entityId`
- `animationType`
- `timestamp`
- `weaponCategory`

Serialization lives in `shared/`:

- `AttackAnimationPacketCodec`
- `ClientHelloPacketCodec`

## Mixed vanilla/modded behavior

### Vanilla players

- Do not need the client mod
- Do not send the hello payload
- Do not receive GBZ animation packets
- Keep vanilla combat visuals and mechanics

### Modded players

- Receive synchronized attack animations from other modded players
- Keep normal server-side combat behavior
- Benefit from distance-limited visual sync only

## Features

### Implemented architecture

- PacketEvents-based swing detection on the Paper side
- Custom payload handshake for mod detection
- Lightweight shared packet layer
- Registry-style weapon classification
- Config-driven weapon category mapping
- Original per-category animation resources
- First-person and smooth-transition capability flags in handshake
- Runtime admin commands for debug and reload

### Weapon categories

- `FIST`
- `PICKAXE`
- `AXE`
- `DAGGER`
- `SWORD`
- `KATANA`
- `LONGSWORD`
- `BROADSWORD`
- `SCYTHE`
- `WARGLAIVE`
- `GREATAXE`

## Commands

### `/combat debug`

Shows runtime sync information such as:

- modded client count
- packet distance
- animation speed
- smooth transition state
- first-person state

### `/combat reload`

Reloads `plugin/src/main/resources/config.yml`.

## Configuration

Main plugin config keys:

- `enable-debug`
- `animation-speed`
- `smooth-transitions`
- `enable-first-person`
- `packet-distance`
- `weapon-categories`

Example:

```yaml
enable-debug: false
animation-speed: 1.0
smooth-transitions: true
enable-first-person: true
packet-distance: 48.0

weapon-categories:
  DIAMOND_SWORD: SWORD
  NETHERITE_AXE: GREATAXE
  mymod:plasma_katana: KATANA
```

Weapon mapping supports:

- Bukkit material names like `DIAMOND_SWORD`
- namespaced ids like `minecraft:diamond_sword`
- custom ids via item PDC key `gbzcombat:weapon_id`

## Animation resources

Animation files live in:

`client/src/main/resources/assets/gbzcombat/player_animation/`

Current animation files:

- `fist.json`
- `pickaxe.json`
- `axe.json`
- `dagger.json`
- `sword.json`
- `katana.json`
- `longsword.json`
- `broadsword.json`
- `scythe.json`
- `warglaive.json`
- `greataxe.json`

## Adding or replacing animations

1. Edit the matching JSON file in `assets/gbzcombat/player_animation/`.
2. Keep the resource id stable unless you also update the client animation lookup.
3. Rebuild the Fabric client module.

## Adding weapon categories

1. Add the enum in `shared/src/main/kotlin/com/gbz/combat/shared/WeaponCategory.kt`.
2. Add a matching animation resource in `client/.../player_animation/`.
3. Extend classification rules in `plugin/.../WeaponClassifier.kt`.
4. Map real items to the new category through config or custom item metadata.

## Registering custom weapons

You can register custom weapons in two ways:

1. Add direct mappings in `weapon-categories`.
2. Write a custom string id into the item PDC using `gbzcombat:weapon_id`.

That keeps the networking protocol stable while allowing future integration with custom item pipelines and server-specific tooling.

## Build stack

- Minecraft `1.21.1`
- Java `21`
- Gradle Kotlin DSL
- Paper plugin module
- Fabric client module
- PacketEvents
- Fabric API
- PlayerAnimator API
- Kotlin

## Build note

Gradle wrapper binaries were not generated in this workspace because there is no local Gradle installation available and external downloads are restricted in the current environment.

The Gradle project files are already structured for normal development environments where wrapper generation and dependency resolution are available.
