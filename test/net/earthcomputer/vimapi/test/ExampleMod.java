package net.earthcomputer.vimapi.test;

import net.earthcomputer.vimapi.ItemStack;
import net.earthcomputer.vimapi.LifecycleEventType;
import net.earthcomputer.vimapi.LifecycleHandler;
import net.earthcomputer.vimapi.Registry;
import net.earthcomputer.vimapi.VimMod;
import net.earthcomputer.vimapi.nbt.NBTCompound;
import net.earthcomputer.vimapi.nbt.NBTList;

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
		Registry.addShapedRecipe(new ItemStack("minecraft:diamond", 0, 2), "lll", "l l", "lll", 'l', "minecraft:log");
		Registry.addShapedRecipe(new ItemStack("minecraft:diamond", 0, 2), "lll", "l l", "lll", 'l', "minecraft:log2");
		NBTCompound nbt = new NBTCompound();
		NBTList ench = new NBTList();
		nbt.set("ench", ench);
		NBTCompound enchantment = new NBTCompound();
		enchantment.setInt("id", 34); // unbreaking
		enchantment.setInt("lvl", 2);
		ench.add(enchantment);
		enchantment = new NBTCompound();
		enchantment.setInt("id", 35); // fortune
		enchantment.setInt("lvl", 2);
		ench.add(enchantment);
		enchantment = new NBTCompound();
		enchantment.setInt("id", 16); // sharpness
		enchantment.setInt("lvl", 2);
		ench.add(enchantment);
		Registry.addShapedRecipe(new ItemStack("minecraft:diamond_sword", 0, 1, nbt), "ddd", "ddd", "sss", 'd',
				"minecraft:diamond", 's', "minecraft:stick");
	}

}
