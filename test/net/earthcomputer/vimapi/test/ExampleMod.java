package net.earthcomputer.vimapi.test;

import net.earthcomputer.vimapi.ItemStack;
import net.earthcomputer.vimapi.LifecycleEventType;
import net.earthcomputer.vimapi.LifecycleHandler;
import net.earthcomputer.vimapi.Registry;
import net.earthcomputer.vimapi.VimMod;

@VimMod(id = "example", version = "1.0")
public class ExampleMod {

	@LifecycleHandler(LifecycleEventType.PREINIT)
	public void preinit() {
		Registry.addShapedRecipe(new ItemStack("minecraft:diamond", 0, 64), "cg", "gc", 'c',
				new ItemStack("minecraft:dirt", 2), 'g', "minecraft:gravel");
		Registry.addShapedRecipe(new ItemStack("minecraft:diamond_sword"), "#", "#", "|", '#', "minecraft:stone", '|',
				"minecraft:stick");
		Registry.addShapelessRecipe(new ItemStack("minecraft:cobblestone", 0, 4), "minecraft:stone", "minecraft:stone",
				"minecraft:stone", "minecraft:stone");
	}

}
