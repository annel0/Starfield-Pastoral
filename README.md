
Installation information
=======

This template repository can be directly cloned to get you started with a new
mod. Simply create a new repository cloned from this one, by following the
instructions provided by [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The usual recommendation for an IDE is either IntelliJ IDEA or Eclipse.

If at any point you are missing libraries in your IDE, or you've run into problems you can
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
============
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

Additional Resources: 
==========
Community Documentation: https://docs.neoforged.net/  
NeoForged Discord: https://discord.neoforged.net/

Stardew Valley Map Bootstrap
==========
- Required: place your map schematic at one of these paths:
	- `src/main/resources/data/stardewcraft/structures/stardew_valley/main.schem`
	- `src/main/resources/data/stardewcraft/structures/mine/main.schem`
- Required: provide prebuilt region files under:
	- `src/main/resources/pregen/stardew_valley/region_manifest.txt`
	- `src/main/resources/pregen/stardew_valley/region/*.mca`
- Runtime chunk-by-chunk generation is disabled for Stardew Valley.
- If prebuilt regions are missing or invalid, travel to Stardew Valley is blocked.

Fast First-Load (Near Instant New Save)
==========
1. Export or prepare your Stardew Valley dimension `region` folder (contains `*.mca`).
2. Run:
	- `powershell -ExecutionPolicy Bypass -File .\scripts\setup_valley_pregen.ps1 -SourceRegionDir "D:\path\to\region" -CleanTarget`
3. Build and run the game normally.

The script will copy all `.mca` files into `src/main/resources/pregen/stardew_valley/region/` and regenerate `region_manifest.txt` automatically.
