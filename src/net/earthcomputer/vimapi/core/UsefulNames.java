package net.earthcomputer.vimapi.core;

import net.earthcomputer.vimapi.EnumSide;

public enum UsefulNames {

	// @formatter:off

	MINECRAFT(EnumSide.CLIENT),
	MINECRAFT_STARTGAME(EnumSide.CLIENT),
	
	DEDICATED_SERVER(EnumSide.SERVER),
	DEDICATED_SERVER_STARTSERVER(EnumSide.SERVER),
	
	NBT_BASE, NBT_PRIMITIVE, NBT_BYTE, NBT_SHORT, NBT_INT, NBT_LONG, NBT_FLOAT, NBT_DOUBLE, NBT_BYTE_ARRAY, NBT_STRING, NBT_LIST, NBT_COMPOUND, NBT_INT_ARRAY,
	NBT_BASE_GETTYPE, NBT_BASE_CREATENEWBYTYPE,
	NBT_BYTE_DATA, NBT_SHORT_DATA, NBT_INT_DATA, NBT_LONG_DATA, NBT_FLOAT_DATA, NBT_DOUBLE_DATA, NBT_BYTE_ARRAY_DATA, NBT_STRING_DATA, NBT_LIST_DATA, NBT_LIST_TAG_TYPE, NBT_COMPOUND_DATA, NBT_INT_ARRAY_DATA,
	
	REGISTRY_NAMESPACED,
	REGISTRY_NAMESPACED_INVERSEOBJECTREGISTRY,
	
	ITEM,
	ITEM_GETBYNAME, ITEM_ITEMREGISTRY,
	
	ITEM_STACK,
	ITEM_STACK_ITEM, ITEM_STACK_DAMAGE, ITEM_STACK_STACKSIZE, ITEM_STACK_TAG,
	
	CRAFTING_MANAGER,
	CRAFTING_MANAGER_INSTANCE, CRAFTING_MANAGER_ADDSHAPEDRECIPE, CRAFTING_MANAGER_ADDSHAPELESSRECIPE, CRAFTING_MANAGER_ADDRECIPE,
	
	SHAPED_RECIPE,
	IRECIPE
	
	;

	// @formatter:on

	private final EnumSide sideToWorkOn;

	private UsefulNames() {
		sideToWorkOn = null;
	}

	private UsefulNames(EnumSide sideToWorkOn) {
		this.sideToWorkOn = sideToWorkOn;
	}

	public EnumSide getWorkingSide() {
		return sideToWorkOn;
	}

}
