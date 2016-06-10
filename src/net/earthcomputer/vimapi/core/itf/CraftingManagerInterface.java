package net.earthcomputer.vimapi.core.itf;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.Bytecode;
import net.earthcomputer.vimapi.core.BytecodeMethod;
import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;

public class CraftingManagerInterface {

	private CraftingManagerInterface() {
	}

	@BytecodeMethod
	@ChangeType("L{CRAFTING_MANAGER};")
	private static Object getInstance() {
		Bytecode.field(Opcodes.GETSTATIC, "{CRAFTING_MANAGER}", "{CRAFTING_MANAGER_INSTANCE}", "L{CRAFTING_MANAGER};");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@ContainsInlineBytecode
	private static void addShapedRecipe(@ChangeType("L{ITEM_STACK};") Object result, Object[] args) {
		Object craftingManager = getInstance();
		InlineOps.method(Opcodes.INVOKEVIRTUAL, "{CRAFTING_MANAGER}", "{CRAFTING_MANAGER_ADDSHAPEDRECIPE}")
				.returnType("L{SHAPED_RECIPE};").param("L{ITEM_STACK};").param(Object[].class).arg(craftingManager)
				.arg(result).arg(args).invokeObject();
	}

	@ContainsInlineBytecode
	private static void addShapelessRecipe(@ChangeType("L{ITEM_STACK};") Object result, Object[] args) {
		Object craftingManager = getInstance();
		InlineOps.method(Opcodes.INVOKEVIRTUAL, "{CRAFTING_MANAGER}", "{CRAFTING_MANAGER_ADDSHAPELESSRECIPE}")
				.param("L{ITEM_STACK};").param(Object[].class).arg(craftingManager).arg(result).arg(args).invokeVoid();
	}

}
