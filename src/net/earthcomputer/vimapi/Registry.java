package net.earthcomputer.vimapi;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;
import net.earthcomputer.vimapi.core.itf.CraftingManagerInterface;
import net.earthcomputer.vimapi.core.itf.ItemInterface;
import net.earthcomputer.vimapi.core.itf.ItemStackInterface;

public class Registry {

	private static final Registry INSTANCE = new Registry();

	private Registry() {
	}

	// PUBLIC INTERFACE METHODS

	public static void addShapedRecipe(ItemStack output, Object... input) {
		INSTANCE.doAddShapedRecipe(output, input);
	}

	public static void addShapelessRecipe(ItemStack output, Object... input) {
		INSTANCE.doAddShapelessRecipe(output, input);
	}

	// PRIVATE INTERNAL METHODS

	@ContainsInlineBytecode
	private void doAddShapedRecipe(ItemStack output, Object[] input) {
		Object mcOutput = InlineOps.method(Opcodes.INVOKESTATIC, ItemStackInterface.class, "translateToMC")
				.returnType("L{vim:ItemStack};").param(ItemStack.class).argObject(output).invokeObject();
		int index = 0;
		while (index < input.length && input[index] instanceof String) {
			index++;
		}
		Object[] mcInput = input.clone();
		for (; index < input.length; index++) {
			if (input[index] instanceof String) {
				mcInput[index] = InlineOps.method(Opcodes.INVOKESTATIC, ItemInterface.class, "getItemByName")
						.returnType("L{vim:Item};").param(String.class).argObject((String) input[index]).invokeObject();
			} else if (input[index] instanceof ItemStack) {
				mcInput[index] = InlineOps.method(Opcodes.INVOKESTATIC, ItemStackInterface.class, "translateToMC")
						.returnType("L{vim:ItemStack};").param(ItemStack.class).argObject((ItemStack) input[index])
						.invokeObject();
			}
		}
		InlineOps.method(Opcodes.INVOKESTATIC, CraftingManagerInterface.class, "addShapedRecipe")
				.param("L{vim:ItemStack};").param(Object[].class).argObject(mcOutput).argObject(mcInput).invokeVoid();
	}

	@ContainsInlineBytecode
	private void doAddShapelessRecipe(ItemStack output, Object[] input) {
		Object mcOutput = InlineOps.method(Opcodes.INVOKESTATIC, ItemStackInterface.class, "translateToMC")
				.returnType("L{vim:ItemStack};").param(ItemStack.class).argObject(output).invokeObject();
		Object[] mcInput = input.clone();
		for (int i = 0; i < input.length; i++) {
			if (input[i] instanceof String) {
				mcInput[i] = InlineOps.method(Opcodes.INVOKESTATIC, ItemInterface.class, "getItemByName")
						.returnType("L{vim:Item};").param(String.class).argObject((String) input[i]).invokeObject();
			} else if (input[i] instanceof ItemStack) {
				mcInput[i] = InlineOps.method(Opcodes.INVOKESTATIC, ItemStackInterface.class, "translateToMC")
						.returnType("L{vim:ItemStack};").param(ItemStack.class).argObject((ItemStack) input[i])
						.invokeObject();
			}
		}
		InlineOps.method(Opcodes.INVOKESTATIC, CraftingManagerInterface.class, "addShapelessRecipe")
				.param("L{vim:ItemStack};").param(Object[].class).argObject(mcOutput).argObject(mcInput).invokeVoid();
	}

}
