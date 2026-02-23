# Easy Villager Reset

A simple, lightweight Fabric mod that allows you to reset villager trades effortlessly—even after they've been locked in—by breaking and replacing their workstations.

## Features

- **Reset Anytime (Even after Trading)**: In vanilla Minecraft, trades are locked forever once you trade. This mod breaks that rule! You can reset a villager's profession, trades, and experience even if they have already been traded with. Just break their workstation.
- **Nametag Protection**: To prevent accidental resets of your important villagers, the mod only affects villagers named with a specific nametag (Default: `easy-villager`).
- **Break to Reset**: Breaking a workstation instantly resets the closest named villager (within a configurable radius), wiping their data so they can start fresh.
- **Improved AI Targeting**: When you place a workstation, the mod ensures the nearest jobless named villager is prioritized to pick it up, preventing other villagers from "stealing" the profession from afar.
- **Configurable**: Adjust the search radius and the required nametag in the config menu.
- **Lightweight**: Optimized to run only on specific block/item events with minimal impact on performance.

## Prerequisites

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
   - Place a workstation near the named villager.
   - If the villager has trades you don't like, simply **break the workstation**.
   - **Crucially**, this works even if you have already traded with them and locked their trades!
   - The closest named villager within range will have their profession and trades wiped.
3. **Assigning a Job**:
   - Place a workstation near a jobless named villager.
   - The mod will trigger the AI to re-evaluate the workstation immediately.

## Configuration

You can configure the mod via **ModMenu**. 

- **Reset Radius**: The distance (in blocks) the mod searches for a villager when a workstation is broken or placed. (Default: `5`, Max: `32`)
- **Villager Nametag**: The exact name a villager must have to be eligible for resetting. (Default: `easy-villager`)

---

**License**: All Rights Reserved (unless otherwise specified in the project).
