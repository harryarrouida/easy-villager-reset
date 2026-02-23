# Easy Villager Reset

A simple, lightweight Fabric mod that allows you to reset villager trades after trade locks effortlessly by breaking and replacing their workstations.

## Features

- **Reset Anytime (Even after Trading)**: Unlike vanilla Minecraft, where trades are locked forever once you trade, this mod allows you to reset a villager's profession, trades, and experience even if they have already been traded with. Just break their workstation!
- **Break to Reset**: Breaking a workstation instantly resets the closest villager (within a configurable radius), wiping their data so they can start fresh.
- **Improved AI Targeting**: When you place a workstation, the mod ensures the nearest jobless villager is prioritized to pick it up, preventing other villagers from "stealing" the profession from afar.
- **Configurable Radius**: Adjust how far the mod searches for villagers in the config menu.
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

1. **Resetting a Villager**:
   - Place a workstation.
   - If the villager has trades you don't like, simply **break the workstation**.
   - The closest villager within range (default 5 blocks) will have their profession and trades wiped.
2. **Assigning a Job**:
   - Place a workstation near a jobless villager.
   - The mod will trigger the AI to re-evaluate the workstation immediately.

## Configuration

You can configure the reset radius via **ModMenu**. 
- **Reset Radius**: The distance (in blocks) the mod searches for a villager when a workstation is broken or placed. (Default: 5, Max: 32)

---

**License**: All Rights Reserved (unless otherwise specified in the project).
