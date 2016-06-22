package net.earthcomputer.vimapi.core.tweaker;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.earthcomputer.vimapi.core.classfinder.UsefulNames;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * This transformer is in charge of injecting into vanilla code
 */
public class InjectingTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		if (name.equals(UsefulNames.get("vim:Minecraft"))) {
			return transformMinecraft(bytes);
		}

		if (name.equals(UsefulNames.get("vim:DedicatedServer"))) {
			return transformDedicatedServer(bytes);
		}

		return bytes;
	}

	private static byte[] transformMinecraft(byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_FRAMES);

		for (MethodNode method : node.methods) {
			if (method.name.equals(UsefulNames.get("vim:Minecraft.startGame")) && method.desc.equals("()V")) {
				method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/earthcomputer/vimapi/Loader",
						"beginLoading", "()V", false));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}

	private static byte[] transformDedicatedServer(byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_FRAMES);

		for (MethodNode method : node.methods) {
			if (method.name.equals(UsefulNames.get("vim:DedicatedServer.startServer")) && method.desc.equals("()Z")) {
				method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/earthcomputer/vimapi/Loader",
						"beginLoading", "()V", false));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}

}
