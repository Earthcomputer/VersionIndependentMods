package net.earthcomputer.vimapi.core.classfinder;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class FinderNBTBase implements IFinder {

	private static final List<String> STRINGS = Arrays.asList("END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE",
			"BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]");

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (constants.getStringRefs().containsAll(STRINGS)) {
			UsefulNames.found("vim:NBTBase", className);
			for (MethodNode method : node.methods) {
				if ((method.access & Opcodes.ACC_ABSTRACT) != 0 && method.desc.endsWith(")B")) {
					UsefulNames.found("vim:NBTBase.getType", method.name);
				} else if ((method.access & Opcodes.ACC_STATIC) != 0 && method.desc.startsWith("(B)")) {
					UsefulNames.found("vim:NBTBase.createNewByType", method.name);
				}
			}
		}
	}

}
