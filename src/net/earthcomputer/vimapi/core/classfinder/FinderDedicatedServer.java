package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class FinderDedicatedServer extends Finder {

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (node.superName.equals("net/minecraft/server/MinecraftServer")) {
			boolean isIntegratedServer = false;
			String mcDesc = UsefulNames.get("vim:Minecraft");
			if (mcDesc != null) {
				mcDesc = "L" + mcDesc + ";";
				for (FieldNode field : node.fields) {
					if (field.desc.equals(mcDesc)) {
						isIntegratedServer = true;
						break;
					}
				}
			}

			if (!isIntegratedServer) {
				UsefulNames.found("vim:DedicatedServer", className);
				for (MethodNode method : node.methods) {
					if (!method.name.equals("<init>")) {
						UsefulNames.found("vim:DedicatedServer.startServer", method.name);
						break;
					}
				}
			}
		}
	}

}
