# Twilight Spark's Delight

> **Forge 1.20.1 port.** This branch contains the current Forge edition of
> Twilight Spark's Delight.

![Twilight Spark's Delight](src/main/resources/logo_mini.png)

## Overview

**Twilight Spark's Delight** is an original Farmer's Delight addon built around the world of The Twilight Forest. This workspace contains its **Minecraft 1.20.1 Forge port**.

The forest's beasts, beetles, boss trophies, and mysterious experiments can all find their way into new recipes. Hunt, cut, pickle, and cook your ingredients, then serve dishes large enough to share with the whole table. Study Experiment 250, brew with the Glory Crucible, or build a kitchen fit for a giant.

Farmer's Delight and The Twilight Forest are required. Unlike the 1.21.1 edition, this port needs addon-owned replacements for the Pocket Watch and Mason Jar, which are absent from the 1.20.1 Twilight Forest dependency. Their migration status is tracked separately.

## New Ingredients

<details>
<summary><strong>Click to view the full ingredient list</strong></summary>

**Liveroot Flour** - Process Liveroot or Root blocks on a Cutting Board.

**Liveroot Dough** - Craft Liveroot Flour with water. The water container is retained.

**Liveroot Bread** - Bake Liveroot Dough in a Furnace or over a Campfire.

**Liveroot Pie Crust** - Craft Liveroot Flour with milk.

**Raw and Cooked Wild Boar Meat** - Hunt Wild Boars; burning targets drop cooked meat.

**Raw and Cooked Wild Boar Meat Cubes** - Cut raw Wild Boar Meat with a knife, then cook the cubes.

**Raw and Cooked Bighorn Mutton** - Hunt Bighorn Sheep; burning targets drop cooked mutton.

**Raw and Cooked Bighorn Mutton Chops** - Cut raw Bighorn Mutton with a knife, then cook the chops.

**Helmet Crab Legs and Beetle Legs** - Hunt the corresponding creatures. Burning targets drop cooked legs.

**Helmet Crab** - Defeat a Helmet Crab with a knife to bring home the whole crab, which can also be processed on a Cutting Board.

**Fire Beetle Flame Sac** - Defeat a Fire Beetle with a knife.

**Slime Beetle Honey Gland** - Defeat a Slime Beetle with a knife.

**Quest Ram Milk** - Use a Glass Bottle on the Quest Ram.

**Quest Ram Cheese** - Heat Quest Ram Milk, process it in a Cooking Pot, or use Twilight Forest's Drying Rack.

**Gelid Crystal** - Defeat the Snow Queen, or process materials with her trophy on a Cutting Board.

**Labyrinth Mushroom** - Defeat the Minoshroom, then cultivate renewable colonies on Rich Soil.

**Bracken** - Harvest Twilight Forest Fiddleheads with a knife, or grow Twilight Bracken Colonies.

**Pickled Bracken** - Prepare a jar of Bracken and let it pickle. A Pocket Watch or Peacock Feather Fan can help.

**Mino Mince** - Cut Raw Meef with a knife. When Twilight Flavors & Delight supplies Raw Meef Slices, its slices replace the whole-meat input.

**Mino Patty** - Cook Mino Mince in a Furnace or over a Campfire.

**Redcap Spice** - Defeat Redcaps or Redcap Sappers with a knife, then prepare larger batches in a Cooking Pot.

**Torchberry Sauce** - Simmer Torchberries and Onion together in a Cooking Pot.

**Experiment Prototype and Experiment 000** - Every Knight Phantom drops Experiment Prototypes, which can be processed on a Cutting Board.

**Experiment 234 and Experiment 250** - Find Experiment 234 among Dark Tower boss loot, then cook it with Experiment 000 to cultivate Experiment 250.

</details>

## New Feasts

**Twilight Cheese Fondue** - Light the pot, bring its dedicated Companion, and gather around bubbling Quest Ram Cheese.

**Salt-Roasted Helmet Crab** - A whole armored crab, with tender meat and a hefty claw waiting inside.

**Twilight Borscht** - A rich forest stew made to sit at the center of the table on a cold evening.

**Twilight Boar Knuckle** - A huge roasted knuckle stretching across its platter, with every serving cut thick.

**Naga Mixed Rice** - A centerpiece filling a 3x3 space with Naga, mushrooms, and forest produce.

**Abyss Pie** - Once the eye in the middle starts staring back, the tentacles somehow become a little easier to eat.

**Pickled Bracken Jar** - Crisp forest greens for sharing, with a reusable Mason Jar left behind.

## New Potion Effects

**Charge** - Improves movement and step height, with an attack bonus for a fast approach.

**Symbiosis** - Reduces incoming damage. Players pay extra hunger; other creatures gradually heal.

**Shrink** - Shrinks the player and adjusts the camera, with a chance to evade attacks.

**Enlarge** - Enlarges the player and adjusts the camera, jump height, and step height.

**Abyss Call** - Causes attacks to spread Symbiosis to hostile creatures.

**Sorrow** - Softens incoming damage and can transform into a higher level of Grief.

**Grief** - Turns accumulated sorrow into strength, increasing the player's damage.

## New Cookware

**Glory Crucible** - Fill it with Fiery Blood or Fiery Tears to process Furnace and Campfire recipes without consuming the heated fluid. Its liquid capacity is configurable.

The crucible can also brew a whole batch of matching regular, splash, or lingering potions. Add a brewing ingredient and keep a heat source below it, then collect the finished potions one bottle at a time. With Fruits Delight installed, it also supports that mod's cauldron recipes: more liquid produces more jam or jello without multiplying the fruit requirement.

**Giant Stove and Cooking Pot** - Matching 2x2x2 cookware. The stove cooks six ingredients at once, and the pot prepares up to eight recipe batches per cycle by default.

**Giant's Stove and Cooking Pot** - Matching 4x4x4 cookware, supporting up to sixteen ingredients or recipe batches by default. Batch limits are configurable.

## Advancements

The mod has **27 advancements**, including **11 hidden advancements**. Their triggers are for you, the person seated at this table, to discover...

## Extended Hunger and Saturation

The extended food system gradually raises both caps from 20 to 40. Ten selected Twilight Forest progression advancements each unlock two additional points; ordinary food fills the extra capacity.

The system and progressive unlocking are enabled by default. Configuration can grant full capacity immediately, change respawn behavior, adjust the extra-range consumption and benefits, or disable the system entirely. AppleSkin integration includes saturation outlines and food previews above 20.

## Optional Integrations

- **JEI:** recipes, hunting, harvesting, pickling, crucible processing, and Experiment 250.
- **AppleSkin:** extended hunger, saturation, and eating previews.
- **Curios:** Pocket Watch and Experiment 250 charm equipment.
- **Fruits Delight:** Glory Crucible cauldron processing.
- **Twilight Flavors & Delight:** ingredient alternatives and Sorrow/Grief interaction.
- **Miner's Delight:** serve the two soup cup variants from ordinary or large FD Cooking Pots with two Copper Cups. Corresponding JEI views and native cup data maps are included. The native Copper Pot has four input slots, so the two five-ingredient soups still need an FD Cooking Pot.

## Development And Verification

Use **Java 17** and the included Gradle wrapper. Fixed development dependencies
are downloaded automatically from Forge, Modrinth and CurseMaven; third-party
mod JARs are not stored in this repository.

```powershell
.\gradlew.bat build
```

On Linux or macOS, use `./gradlew build`.

Target output: `build/libs/twilight_spark_delight-1.20.1-1.0.0.jar`.

`build.gradle` lists the pinned dependency versions and development run options.
Dependency JARs are not uploaded. `-PwithoutOptionalMods` leaves
optional integrations out of the development runtime, not the compiled mod.

This branch contains the production sources and build files. It is not an
automatic converter for Forge 1.12.2 worlds.

## Credits And License

By **xy177**, with artwork and models by [**FallenSnow21**](https://www.curseforge.com/members/fallensnow21/projects), [**L_CangS**](https://www.curseforge.com/members/l_cangs/projects), and [**thousvillages**](https://www.curseforge.com/members/thousvillages/projects).

Source code: **CC BY-NC-SA 4.0**. Original artwork and models: **All Rights Reserved**, subject to the permissions in [license/](license/). Reused upstream art retains its original owners' rights.
