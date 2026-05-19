<div align="center">

![GalaxyBoxZ](https://github.com/user-attachments/assets/883a4640-cacf-4516-a2dc-e8ddb8f9f909)

# GBZ Complementary

Client-side companion mod for the GBZ server on Fabric.

It adds custom combat presentation for GBZ weapons without changing vanilla or server-side gameplay. No damage edits, no hitbox changes, no cooldown manipulation, no packet logic, and no combat advantage over other players.

[![Discord](https://img.shields.io/discord/816385202711560203.svg?label=Discord&logo=discord&logoColor=ffffff&color=7389D8)](https://discord.gg/KN9b3pjFTM)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4_~_1.21.11-4caf50)
![Java](https://img.shields.io/badge/Java-21-ee9258?logo=openjdk&logoColor=white)
![Fabric Loader](https://img.shields.io/badge/Fabric_Loader-%3E%3D_0.16.10-000000)
![Fabric API](https://img.shields.io/badge/Fabric_API-0.119.2%2B1.21.4-000000)
![Fabric Kotlin](https://img.shields.io/badge/Fabric_Language_Kotlin-1.13.2%2Bkotlin.2.1.20-000000)
![PlayerAnimator](https://img.shields.io/badge/PlayerAnimator-2.0.5%2B1.21.4-c2185b)

</div>

## Overview

GBZ Complementary is a standalone client mod that reads the held item's model id and maps it to an animation profile. This lets custom GBZ weapons feel distinct while preserving vanilla timing and server authority.

The mod is designed for server-specific polish:

- Different idle stances per weapon family
- Distinct attack animations for custom GBZ items
- Playback speed derived from `ATTACK_SPEED`
- Smooth transitions when the held item changes
- Optional debug overlay for mapping and timing inspection

## What It Does Not Do

This mod is presentation-only.

- It does not change combat logic
- It does not change damage, reach, hit detection, or knockback
- It does not alter packets or server behavior
- It does not create a gameplay requirement for joining the server

## Requirements

- Minecraft `1.21.4` to `1.21.11`
- Java `21`
- Fabric Loader `0.16.10+`
- Fabric API `0.119.2+1.21.4`
- Fabric Language Kotlin `1.13.2+kotlin.2.1.20`
- PlayerAnimator `2.0.5+1.21.4`

## How It Works

Every client tick, the mod resolves the model id of the held item and matches it against a bundled animation mapping file.

Each player uses two animation layers:

- `Idle`: looping hold pose, blended when the weapon type changes. Disabled in first person.
- `Attack`: high-priority swing animation triggered on attack. It fades in quickly and interrupts cleanly during rapid swings.

Playback speed is dynamic:

- Base timing comes from vanilla cooldown math: `20 / attackSpeed`
- The result is scaled by the weapon category multiplier
- A global speed multiplier can be applied from config

This keeps daggers fast, heavier weapons slower, and the overall feel synchronized with vanilla attack rhythm instead of hardcoded animation lengths.

## Current Weapon Categories

The current animation types are:

- `fist`
- `pickaxe`
- `axe`
- `dagger`
- `sword`
- `katana`
- `longsword`
- `broadsword`
- `scythe`
- `warglaive`
- `greataxe`

## Installation

1. Install Fabric for a supported Minecraft version.
2. Add Fabric API, Fabric Language Kotlin, and PlayerAnimator to the client.
3. Place the GBZ Complementary mod jar in the client's `mods` folder.
4. Launch the game and join the server normally.

## Weapon Mapping Configuration

Weapon mappings are defined in:

```text
client/src/main/resources/assets/gbz/animations.json
```

This file is bundled inside the jar. The mod does not create or read an external config file at runtime.

Example:

```json
{
  "default": "fist",
  "debugLogging": false,
  "debugOverlay": false,
  "interpolationTicks": 3,
  "globalSpeedMultiplier": 1.0,
  "mappings": [
    {
      "animation": "sword",
      "match": [
        "minecraft:*_sword",
        "gbz:weapon/coal"
      ]
    },
    {
      "animation": "warglaive",
      "match": [
        "gbz:weapon/cosmetic/admin"
      ]
    }
  ]
}
```

### Match Priority

Patterns are matched in the following order:

| Priority | Example | Meaning |
|---|---|---|
| 1 | `gbz:weapon/coal` | Exact match |
| 2 | `gbz:weapon/*` | Prefix wildcard |
| 3 | `minecraft:*_sword` | Full wildcard |
| 4 | default | Fallback animation |

Items that fall back to the default animation are not cached and are checked again on later ticks.

## Adding a New Weapon Mapping

To map a new GBZ item to an existing animation type:

1. Open `client/src/main/resources/assets/gbz/animations.json`
2. Add the item model id to an existing `match` list
3. Rebuild the mod

Example:

```json
{
  "animation": "katana",
  "match": [
    "gbz:weapon/cosmetic/rank3",
    "gbz:weapon/cosmetic/dev",
    "gbz:weapon/my_new_katana"
  ]
}
```

## Adding a New Animation Type

Animation files live in:

```text
client/src/main/resources/assets/gbz/player_animations/
```

To introduce a completely new weapon family:

1. Add the PlayerAnimator-compatible animation json files
2. Add the new type in `WeaponAnimationType.kt`
3. Register its profile in `AnimationProfileRegistry.kt`
4. Map items to it in `animations.json`

## Debug Overlay

Run the following command in-game:

```text
/combatanim debug
```

This toggles a HUD overlay showing:

- Current weapon type
- Held item model key
- Matched rule and priority
- Attack speed
- Cooldown ticks
- Final playback speed
- Resolver cache size

## Project Structure

```text
client/src/main/kotlin/com/gbz/combat/client/
|-- animation/   playback, layers, speed scaling, animation profiles
|-- config/      bundled config loading
|-- matcher/     exact, prefix, and wildcard resolver logic
|-- player/      held-item resolution, attack hooks, per-player state
|-- registry/    animation profile registry and client commands
`-- util/        constants, debug overlay, helpers
```

## Development Notes

- The mod is client-only
- The mapping file is bundled with the jar
- Server-specific item model ids are the main extension point
- The safest feature direction is visual feedback, not gameplay logic

## License

Add your license information here if you plan to publish the project outside the server community.
