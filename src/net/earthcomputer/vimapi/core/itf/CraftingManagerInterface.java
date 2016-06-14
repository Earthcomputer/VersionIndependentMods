package net.earthcomputer.vimapi.core.itf;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;

/**
 * This class interfaces with the vanilla CraftingManager class
 */
public class CraftingManagerInterface {

	private CraftingManagerInterface() {
	}

	@ContainsInlineBytecode
	@ChangeType("L{vim:CraftingManager};")
	private static Object getInstance() {
		return InlineOps.field(Opcodes.GETSTATIC, "{vim:CraftingManager}", "{vim:CraftingManager.instance}")
				.type("L{vim:CraftingManager};").getObject();
	}

	@ContainsInlineBytecode
	private static void addShapedRecipe(@ChangeType("L{vim:ItemStack};") Object result, Object[] args) {
		Object craftingManager = getInstance();
		InlineOps.method(Opcodes.INVOKEVIRTUAL, "{vim:CraftingManager}", "{vim:CraftingManager.addShapedRecipe}")
				.returnType("L{vim:ShapedRecipe};").param("L{vim:ItemStack};").param(Object[].class)
				.argObject(craftingManager).argObject(result).argObject(args).invokeObject();
	}

	@ContainsInlineBytecode
	private static void addShapelessRecipe(@ChangeType("L{vim:ItemStack};") Object result, Object[] args) {
		Object craftingManager = getInstance();
		InlineOps.method(Opcodes.INVOKEVIRTUAL, "{vim:CraftingManager}", "{vim:CraftingManager.addShapelessRecipe}")
				.param("L{vim:ItemStack};").param(Object[].class).argObject(craftingManager).argObject(result)
				.argObject(args).invokeVoid();
	}

}
