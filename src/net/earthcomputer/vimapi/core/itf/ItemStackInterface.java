package net.earthcomputer.vimapi.core.itf;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.ItemStack;
import net.earthcomputer.vimapi.core.Bytecode;
import net.earthcomputer.vimapi.core.BytecodeMethod;
import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;
import net.earthcomputer.vimapi.nbt.NBTBase;
import net.earthcomputer.vimapi.nbt.NBTCompound;

public class ItemStackInterface {

	private ItemStackInterface() {
	}

	@BytecodeMethod
	@ChangeType("L{ITEM};")
	private static Object getItem(@ChangeType("L{ITEM_STACK};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{ITEM_STACK}", "{ITEM_STACK_ITEM}", "L{ITEM};");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static int getStackSize(@ChangeType("L{ITEM_STACK};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{ITEM_STACK}", "{ITEM_STACK_STACKSIZE}", "I");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static int getDamage(@ChangeType("L{ITEM_STACK};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{ITEM_STACK}", "{ITEM_STACK_DAMAGE}", "I");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	@ChangeType("L{NBT_COMPOUND};")
	private static Object getTag(@ChangeType("L{ITEM_STACK};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{ITEM_STACK}", "{ITEM_STACK_TAG}", "L{NBT_COMPOUND};");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setTag(@ChangeType("L{ITEM_STACK};") Object itemStack,
			@ChangeType("L{NBT_COMPOUND};") Object newTag) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{ITEM_STACK}", "{ITEM_STACK_TAG}", "L{NBT_COMPOUND};");
		Bytecode.insn(Opcodes.RETURN);
	}

	@ContainsInlineBytecode
	private static ItemStack translateFromMC(@ChangeType("L{ITEM_STACK};") Object itemStack) {
		return new ItemStack(
				(String) InlineOps.method(Opcodes.INVOKESTATIC, ItemInterface.class, "getNameFromItem")
						.returnType(String.class).param("L{ITEM};").arg(getItem(itemStack)).invokeObject(),
				getDamage(itemStack), getStackSize(itemStack),
				(NBTCompound) InlineOps.method(Opcodes.INVOKESTATIC, NBTInterface.class, "translateFromMC")
						.returnType(NBTBase.class).param("L{NBT_BASE};").arg(getTag(itemStack)).invokeObject());
	}

	@BytecodeMethod
	@ChangeType("L{ITEM_STACK};")
	private static Object newItemStack(@ChangeType("L{ITEM};") Object item, int stackSize, int damage) {
		Bytecode.type(Opcodes.NEW, "{ITEM_STACK}");
		Bytecode.insn(Opcodes.DUP);
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.var(Opcodes.ILOAD, 2);
		Bytecode.method(Opcodes.INVOKESPECIAL, "{ITEM_STACK}", "<init>", "(L{ITEM};II)V", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@ContainsInlineBytecode
	@ChangeType("L{ITEM_STACK};")
	private static Object translateToMC(ItemStack itemStack) {
		Object mcItemStack = newItemStack(
				InlineOps.method(Opcodes.INVOKESTATIC, ItemInterface.class, "getItemByName").returnType("L{ITEM};")
						.param(String.class).arg(itemStack.getItemName()).invokeObject(),
				itemStack.getStackSize(), itemStack.getDamage());
		setTag(mcItemStack,
				InlineOps.checkcast(InlineOps.method(Opcodes.INVOKESTATIC, NBTInterface.class, "translateToMC")
						.returnType("L{NBT_BASE};").param(NBTBase.class).arg(itemStack.getTagCompound()).invokeObject(),
				"{NBT_COMPOUND}"));
		return mcItemStack;
	}

}
