package net.earthcomputer.vimapi.core.classfinder;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class FinderItem implements IFinder {

	private static final List<String> STRINGS = Arrays.asList("iron_shovel", "feather", "bread", "sign", "milk_bucket",
			"clock", "cake", "melon_seeds", "blaze_rod", "spawn_egg");

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (constants.getStringRefs().containsAll(STRINGS) && node.fields.size() < 64) {
			UsefulNames.found("vim:Item", className);

			for (MethodNode method : node.methods) {
				if ((method.access & Opcodes.ACC_STATIC) != 0
						&& method.desc.equals("(Ljava/lang/String;)L" + className + ";")) {
					UsefulNames.found("vim:Item.getByName", method.name);
				}
			}

			FieldNode itemRegistryField = node.fields.get(0);
			UsefulNames.found("vim:Item.itemRegistry", itemRegistryField.name);
			UsefulNames.found("vim:RegistryNamespaced", Type.getType(itemRegistryField.desc).getInternalName());
		}
	}

}
