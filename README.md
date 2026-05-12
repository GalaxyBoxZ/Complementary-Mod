<div align="center">

<h1>GBZ Combat Animations</h1>

Standalone client-side combat animation mod for Fabric. It is inspired by Better Combat's presentation style, but it is not an addon and it does not alter combat logic, cooldown rules, hit detection, damage, packets, or server behavior.

  <a href="">![Minecraft 1.21.4 ~ 1.21.11](https://img.shields.io/badge/Minecraft-1.21.4_~_1.21.11-4caf50)</a>
  <a href="">![Environment: Client](https://img.shields.io/badge/environment-Client-1976d2)</a>
  
  <a href="">![Java 21](https://img.shields.io/badge/Java_21-ee9258?logo=coffeescript&logoColor=ffffff)</a>
  <a href="">![PlayerAnimator](https://img.shields.io/badge/PlayerAnimator-c2185b)</a>
  
  <a href="">[![Discord](https://img.shields.io/discord/816385202711560203.svg?label=&logo=discord&logoColor=ffffff&color=7389D8)](https://discord.gg/KN9b3pjFTM)</a>

</div>

## Version Support

- Compile target: Minecraft `1.21.4`
- Intended runtime support: `1.21.4` through `1.21.11`
- Loader strategy: stable Fabric APIs, public PlayerAnimator APIs, and item model component lookup instead of renderer internals

## Architecture

The client module is split into the requested packages:

- `animation`: playback, layer management, speed scaling, and animation profile metadata
- `config`: JSON config loading, defaults, hot reload, and live settings
- `matcher`: exact/prefix/wildcard rule compilation, matching, and resolver cache
- `player`: held-item model resolution and attack/tick integration
- `registry`: animation profile registry and client commands
- `util`: shared constants, debug overlay, and small helpers

## Matching System

The resolver reads the held `ItemStack`, then checks:

1. `DataComponentTypes.ITEM_MODEL`
2. The registry id of the backing item as a fallback

The resulting id string is matched with these rule priorities:

1. exact match
2. prefix match
3. wildcard match
4. default fallback

Examples:

- `gbz:weapon/cosmetic/admin` -> `warglaive`
- `gbz:weapon/coal` -> `sword`
- `gbz:weapon/enderstone` -> `katana`
- `minecraft:*_pickaxe` -> `pickaxe`
- `minecraft:*_sword` -> `sword`

## Config

The live config file is stored at:

- `config/gbzcombat/animations.json`

It is created automatically on first run and reloaded automatically when the file changes. Manual reload is also available with:

- `/combatanim reload`

Debug overlay toggle:

- `/combatanim debug`

A checked-in example copy also lives at:

- `examples/animations.json`

## Animation Playback

PlayerAnimator provides two layered channels per player:

- idle layer
- attack layer

Idle uses looping low-amplitude animations and blends when the resolved weapon category changes. Attack uses a higher-priority fading layer and interrupts cleanly when a new attack arrives before the previous one completes.

Playback speed is dynamic:

- based on `GENERIC_ATTACK_SPEED`
- synchronized against vanilla cooldown timing using `20 / attackSpeed`
- adjusted again by a per-weapon category speed multiplier
- multiplied by global config speed settings

This keeps daggers fast and greataxes slower without hardcoding one fixed duration.

## Cache Behavior

The animation resolver caches by resolved model key string:

- primary key: item model component id
- fallback key: item registry id

The cache is cleared automatically when:

- the config reloads
- debug reload is triggered manually

This keeps per-tick matching overhead low on large custom-item packs.

## Adding Weapon Mappings

Edit `config/gbzcombat/animations.json`:

```json
{
  "default": "fist",
  "mappings": [
    {
      "match": "minecraft:*_pickaxe",
      "animation": "pickaxe"
    },
    {
      "match": "gbz:weapon/cosmetic/admin",
      "animation": "warglaive"
    }
  ]
}
```

## Adding Animations

Animation assets live in:

- `client/src/main/resources/assets/gbzcombat/player_animation`

The mod uses standard PlayerAnimator animation json files. To add a new one:

1. Place the file in `assets/gbzcombat/player_animation/`
2. Register its identifier in `AnimationProfileRegistry.kt`
3. Point a `WeaponAnimationType` profile at it

## Forward-Compatibility Notes

The project is organized to make future updates cheap:

- compile-time Minecraft coupling is concentrated in the player integration and command/overlay bootstrap points
- model-id resolution uses the public item component API introduced in modern 1.21
- no renderer mixins are required for first-person because PlayerAnimator public first-person configuration is used
- no server-side entrypoints exist

## Sources Used For API/Version Pinning

- Fabric Maven indexes for `fabric-api`, `fabric-loader`, `yarn`, and `fabric-language-kotlin`
- PlayerAnimator README and package listings from the official repository / maven
- Fabric API docs for `ClientPreAttackCallback`
- Yarn docs for `DataComponentTypes.ITEM_MODEL`
