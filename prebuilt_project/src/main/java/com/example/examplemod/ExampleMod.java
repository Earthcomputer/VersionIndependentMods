package com.example.examplemod;

import static net.earthcomputer.vimapi.LifecycleEventType.*;

import net.earthcomputer.vimapi.ItemStack;
import net.earthcomputer.vimapi.LifecycleHandler;
import net.earthcomputer.vimapi.Registry;
import net.earthcomputer.vimapi.VimMod;

@VimMod(id = "example", name = "Example Mod", version = "1.0")
public class ExampleMod {

	@LifecycleHandler(PREINIT)
	public void preinit() {
		
		// Add a shaped crafting recipe
		Registry.addShapedRecipe(
			// output
			new ItemStack("minecraft:diamond", 0, 64),
			// input shape
			"dg",
			"gd",
			// input definitions
			'd', "minecraft:dirt",
			'g', "minecraft:gravel"
		);
		
		// Add a shapeless crafting recipe
		Registry.addShapelessRecipe(
			// output
			new ItemStack("minecraft:gold_ingot", 0, 64),
			// input items
			new ItemStack("minecraft:stone", 1), // granite
			"minecraft:cobblestone"
		);
		
	}

}
