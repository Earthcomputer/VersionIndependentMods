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
	@ChangeType("L{vim:Item};")
	private static Object getItem(@ChangeType("L{vim:ItemStack};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:ItemStack}", "{vim:ItemStack.item}", "L{vim:Item};");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static int getStackSize(@ChangeType("L{vim:ItemStack};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:ItemStack}", "{vim:ItemStack.stackSize}", "I");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static int getDamage(@ChangeType("L{vim:ItemStack};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:ItemStack}", "{vim:ItemStack.damage}", "I");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	@ChangeType("L{vim:NBTCompound};")
	private static Object getTag(@ChangeType("L{vim:ItemStack};") Object itemStack) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:ItemStack}", "{vim:ItemStack.tag}", "L{vim:NBTCompound};");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setTag(@ChangeType("L{vim:ItemStack};") Object itemStack,
			@ChangeType("L{vim:NBTCompound};") Object newTag) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:ItemStack}", "{vim:ItemStack.tag}", "L{vim:NBTCompound};");
		Bytecode.insn(Opcodes.RETURN);
	}

	@ContainsInlineBytecode
	private static ItemStack translateFromMC(@ChangeType("L{vim:ItemStack};") Object itemStack) {
		return new ItemStack(
				(String) InlineOps.method(Opcodes.INVOKESTATIC, ItemInterface.class, "getNameFromItem")
						.returnType(String.class).param("L{vim:Item};").arg(getItem(itemStack)).invokeObject(),
				getDamage(itemStack), getStackSize(itemStack),
				(NBTCompound) InlineOps.method(Opcodes.INVOKESTATIC, NBTInterface.class, "translateFromMC")
						.returnType(NBTBase.class).param("L{vim:NBTBase};").arg(getTag(itemStack)).invokeObject());
	}

	@BytecodeMethod
	@ChangeType("L{vim:ItemStack};")
	private static Object newItemStack(@ChangeType("L{vim:Item};") Object item, int stackSize, int damage) {
		Bytecode.type(Opcodes.NEW, "{vim:ItemStack}");
		Bytecode.insn(Opcodes.DUP);
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.var(Opcodes.ILOAD, 2);
		Bytecode.method(Opcodes.INVOKESPECIAL, "{vim:ItemStack}", "<init>", "(L{vim:Item};II)V", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@ContainsInlineBytecode
	@ChangeType("L{vim:ItemStack};")
	private static Object translateToMC(ItemStack itemStack) {
		Object mcItemStack = newItemStack(
				InlineOps.method(Opcodes.INVOKESTATIC, ItemInterface.class, "getItemByName").returnType("L{vim:Item};")
						.param(String.class).arg(itemStack.getItemName()).invokeObject(),
				itemStack.getStackSize(), itemStack.getDamage());
		setTag(mcItemStack,
				InlineOps.checkcast(InlineOps.method(Opcodes.INVOKESTATIC, NBTInterface.class, "translateToMC")
						.returnType("L{vim:NBTBase};").param(NBTBase.class).arg(itemStack.getTagCompound()).invokeObject(),
				"{vim:NBTCompound}"));
		return mcItemStack;
	}

}
