package net.earthcomputer.vimapi.core.classfinder;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FinderItemStack extends Finder {

	private static final List<String> STRINGS = Arrays.asList("x", "@", "id", "Count", "Damage", "tag", "ench",
			"display", "Name", "#%04d/%d%s");

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (constants.getStringRefs().containsAll(STRINGS)) {
			UsefulNames.found("vim:ItemStack", className);

			String itemDesc = "L" + UsefulNames.get("vim:Item") + ";";
			String nbtCompoundDesc = "L" + UsefulNames.get("vim:NBTCompound") + ";";
			// Clashes with animationsToGo
			boolean foundStackSize = false;
			// Likely to clash with something in the future
			boolean foundDamage = false;
			// Clashes with Forge's capNBT
			boolean foundTag = false;
			for (FieldNode field : node.fields) {
				if (field.desc.equals(itemDesc)) {
					UsefulNames.found("vim:ItemStack.item", field.name);
				} else if (!foundStackSize && field.desc.equals("I") && (field.access & Opcodes.ACC_PUBLIC) != 0) {
					foundStackSize = true;
					UsefulNames.found("vim:ItemStack.stackSize", field.name);
				} else if (!foundDamage && field.desc.equals("I")
						&& (field.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
					foundDamage = true;
					UsefulNames.found("vim:ItemStack.damage", field.name);
				} else if (!foundTag && field.desc.equals(nbtCompoundDesc)) {
					foundTag = true;
					UsefulNames.found("vim:ItemStack.tag", field.name);
				}
			}
		}
	}

}
