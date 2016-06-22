package net.earthcomputer.vimapi.core.classfinder;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.VIM;

/**
 * A class which contains a reference to all the vanilla obfuscated names, with
 * useful keys which should have the format <code>modid:key</code>. In order to
 * add your own useful name, you must first call {@link #expect(String)} before
 * {@link VIM#findClasses(java.net.URL[], String)} is called. Then, an
 * {@link IFinder} can call {@link #found(String, String)}. If the useful name
 * is not found by any <code>IFinder</code>, then an error is thrown and the
 * game will not start.
 */
public class UsefulNames {

	private static final Map<String, UsefulName> usefulNames = Maps.newHashMap();

	private UsefulNames() {
	}

	public static void expect(String key) {
		expect(key, null);
	}

	public static void expect(String key, EnumSide onSide) {
		usefulNames.put(key, new UsefulName(onSide));
	}

	public static void found(String key, String name) {
		if (!usefulNames.containsKey(key)) {
			throw new IllegalArgumentException("There is no such useful name as \"" + key + "\"");
		}
		usefulNames.get(key).name = name;
	}

	public static String get(String key) {
		if (!usefulNames.containsKey(key)) {
			throw new IllegalArgumentException("There is no such useful name as \"" + key + "\"");
		}
		return usefulNames.get(key).name;
	}

	public static Set<String> getUnfoundEntries() {
		Set<String> notFound = Sets.newHashSet();
		for (Map.Entry<String, UsefulName> entry : usefulNames.entrySet()) {
			UsefulName usefulName = entry.getValue();
			if (usefulName.name == null) {
				if (usefulName.side == null || usefulName.side == VIM.getSide()) {
					notFound.add(entry.getKey());
				}
			}
		}
		return notFound;
	}

	private static class UsefulName {
		private String name;
		private EnumSide side;

		public UsefulName(EnumSide side) {
			this.side = side;
		}
	}

	static {
		expect("vim:Minecraft", EnumSide.CLIENT);
		expect("vim:Minecraft.startGame", EnumSide.CLIENT);

		expect("vim:DedicatedServer", EnumSide.SERVER);
		expect("vim:DedicatedServer.startServer", EnumSide.SERVER);

		expect("vim:NBTBase");
		expect("vim:NBTPrimitive");
		expect("vim:NBTByte");
		expect("vim:NBTShort");
		expect("vim:NBTInt");
		expect("vim:NBTLong");
		expect("vim:NBTFloat");
		expect("vim:NBTDouble");
		expect("vim:NBTByteArray");
		expect("vim:NBTString");
		expect("vim:NBTList");
		expect("vim:NBTCompound");
		expect("vim:NBTIntArray");
		expect("vim:NBTBase.getType");
		expect("vim:NBTBase.createNewByType");
		expect("vim:NBTByte.data");
		expect("vim:NBTShort.data");
		expect("vim:NBTInt.data");
		expect("vim:NBTLong.data");
		expect("vim:NBTFloat.data");
		expect("vim:NBTDouble.data");
		expect("vim:NBTByteArray.data");
		expect("vim:NBTString.data");
		expect("vim:NBTList.data");
		expect("vim:NBTList.tagType");
		expect("vim:NBTCompound.data");
		expect("vim:NBTIntArray.data");

		expect("vim:RegistryNamespaced");
		expect("vim:RegistryNamespaced.inverseObjectRegistry");

		expect("vim:Item");
		expect("vim:Item.getByName");
		expect("vim:Item.itemRegistry");

		expect("vim:ItemStack");
		expect("vim:ItemStack.item");
		expect("vim:ItemStack.damage");
		expect("vim:ItemStack.stackSize");
		expect("vim:ItemStack.tag");

		expect("vim:CraftingManager");
		expect("vim:CraftingManager.instance");
		expect("vim:CraftingManager.addShapedRecipe");
		expect("vim:CraftingManager.addShapelessRecipe");
		expect("vim:CraftingManager.addRecipe");

		expect("vim:ShapedRecipe");

		expect("vim:IRecipe");
	}

}
