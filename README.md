<div align="center">
  
<a href="">![GalaxyBoxZ](https://github.com/user-attachments/assets/883a4640-cacf-4516-a2dc-e8ddb8f9f909)</a>
<h1>Combat Animations</h1>

Standalone client-side combat animation mod for Fabric.  
Inspired by Better Combat's presentation style — does not alter combat logic, cooldown rules, hit detection, damage, packets, or server behavior.

![Minecraft 1.21.4 ~ 1.21.11](https://img.shields.io/badge/Minecraft-1.21.4_~_1.21.11-4caf50)
![Environment: Client](https://img.shields.io/badge/Environment-Client-1976d2)
![Java 21](https://img.shields.io/badge/Java-21-ee9258?logo=coffeescript&logoColor=ee9258)
![PlayerAnimator](https://img.shields.io/badge/PlayerAnimator-c2185b)
[![Discord](https://img.shields.io/discord/816385202711560203.svg?label=Discord&logo=discord&logoColor=ffffff&color=7389D8)](https://discord.gg/KN9b3pjFTM)

</div>

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | `1.21.4` – `1.21.11` |
| Fabric Loader | `≥ 0.16.10` |
| Fabric API | `0.119.2+1.21.4` |
| Fabric Language Kotlin | `≥ 1.13.2+kotlin.2.1.20` |
| PlayerAnimator | `2.0.5+1.21.4` |
| Java | `21` |

---

## How It Works

The mod reads the item model id of the player's held item every tick and maps it to a weapon animation category. Two animation layers run per player:

- **Idle** — looping hold pose, blends when the weapon category changes. Disabled in first person.
- **Attack** — high-priority layer triggered on swing, fades in and interrupts cleanly on rapid attacks.

Playback speed is dynamic — derived from `ATTACK_SPEED`, synchronized against vanilla cooldown timing (`20 / attackSpeed`), then scaled by a per-category multiplier. This keeps daggers fast and greataxes slow without hardcoded durations.

---

## Adding Weapon Mappings

Edit `client/src/main/resources/assets/gbzcombat/animations.json`.  
This file is **bundled inside the jar** — no external config is created or read at runtime.

Each entry supports one or multiple match patterns:

```json
{
  "default": "fist",
  "globalSpeedMultiplier": 1.0,
  "interpolationTicks": 3,
  "mappings": [
    {
      "animation": "sword",
      "match": [ "minecraft:*_sword", "gbz:weapon/coal" ]
    },
    {
      "animation": "warglaive",
      "match": [ "gbz:weapon/cosmetic/admin" ]
    }
  ]
}
```

### Match Rules

Patterns are matched in this priority order:

| Priority | Example | Description |
|---|---|---|
| 1 | `gbz:weapon/coal` | Exact match |
| 2 | `gbz:weapon/*` | Prefix wildcard |
| 3 | `minecraft:*_sword` | Full wildcard |
| 4 | *(none)* | Default fallback |

Items that fall back to default are **not cached** and re-evaluated every tick.

---

## Adding Animations

Place PlayerAnimator-compatible `.json` files in:

```
client/src/main/resources/assets/gbzcombat/player_animations/
```

Then:

1. Add a new entry to `WeaponAnimationType.kt`
2. Register a profile in `AnimationProfileRegistry.kt`
3. Add a mapping entry in `animations.json`

---

## Architecture

```
client/src/main/kotlin/com/gbz/combat/client/
├── animation/   — playback, layers, speed scaling, animation profiles
├── config/      — bundled JSON loading
├── matcher/     — exact/prefix/wildcard rule compilation and resolver cache
├── player/      — held-item model resolution, attack and tick hooks
├── registry/    — animation profile registry, client commands
└── util/        — constants, debug overlay, helpers
```

---

## Debug Overlay

Run `/combatanim debug` in-game to toggle the HUD overlay showing:

- Current weapon type and model key
- Matched rule and priority
- Attack speed, cooldown ticks, and playback speed
- Resolver cache size
