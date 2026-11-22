# Easy Villager Reset

A simple, lightweight Fabric mod that allows you to reset villager trades effortlessly—even after they've been locked in—by breaking and replacing their workstations.

## Features

- **Reset Anytime (Even after Trading)**: In vanilla Minecraft, trades are locked forever once you trade. This mod breaks that rule! You can reset a villager's profession, trades, and experience even if they have already been traded with. Just break their workstation.
- **Nametag Protection**: To prevent accidental resets of your important villagers, the mod only affects villagers named with a specific nametag (Default: `easy-villager`).
- **Break to Reset**: Breaking a workstation resets the eligible named villager that has it recorded as their job site, within the configured search radius.
- **Improved AI Targeting**: When you try to place a workstation, the mod refreshes the nearest jobless named villager’s AI. Minecraft still decides which villager claims the workstation.
- **Configurable**: Adjust the search radius and the required nametag in the config menu.
- **Lightweight**: Optimized to run only on specific block/item events with minimal impact on performance.

## Prerequisites

Version 1.5-beta+mc26.2 targets Minecraft Java Edition 26.2 on Fabric and requires Java 25.

This mod requires the following dependencies:

- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
- [Cloth Config API](https://modrinth.com/mod/cloth-config)
- [ModMenu](https://modrinth.com/mod/modmenu) (Recommended for configuration)

## Installation

1. Download the latest version of the mod for your Minecraft version.
2. Ensure you have all the required dependencies listed above.
3. Drop the `.jar` files into your Minecraft `mods` folder.
4. Launch the game!

## How to Use

1. **Prepare the Villager**:
   - Use a nametag to name your villager `easy-villager` (case-sensitive, default value).
   - Only villagers with this name will be affected by the mod's reset logic.
2. **Resetting a Villager**:
   - Place a workstation near the named villager and wait for them to claim it.
   - If the villager has trades you don't like, simply **break the workstation**.
   - **Crucially**, this works even if you have already traded with them and locked their trades!
   - The named villager within range who claimed that workstation will have their profession and trades wiped. A workstation without an eligible claimant will leave nearby villagers alone.
3. **Assigning a Job**:
   - Place a workstation near a jobless named villager.
   - The mod refreshes the nearest jobless named villager’s AI so they can look for a job site.

## Configuration

You can configure the mod via **ModMenu**. 

- **Reset Radius**: The distance (in blocks) the mod searches for a villager when a workstation is broken or placed. (Default: `5`, Max: `32`)
- **Villager Nametag**: The exact name a villager must have to be eligible for resetting. (Default: `easy-villager`)

---

**License**: All Rights Reserved (unless otherwise specified in the project).
