package net.earthcomputer.vimapi.core.tweaker;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class AllPublicTransformer implements IClassTransformer {

	private static final int REMOVE_PRIVATE_PROTECTED = ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		for (FieldNode field : node.fields) {
			field.access &= REMOVE_PRIVATE_PROTECTED;
			field.access |= Opcodes.ACC_PUBLIC;
		}

		for (MethodNode method : node.methods) {
			method.access &= REMOVE_PRIVATE_PROTECTED;
			method.access |= Opcodes.ACC_PUBLIC;
		}

		ClassWriter writer = new ClassWriter(0);
		node.accept(writer);
		return writer.toByteArray();
	}

}
