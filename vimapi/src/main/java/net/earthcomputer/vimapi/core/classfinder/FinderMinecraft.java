package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class FinderMinecraft implements IFinder {

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (constants.getStringRefs().contains("textures/gui/title/mojang.png")) {
			UsefulNames.found("vim:Minecraft", className);

			boolean foundStartGame = false;
			for (MethodNode method : node.methods) {
				if (!foundStartGame && method.exceptions.contains("org/lwjgl/LWJGLException")) {
					foundStartGame = true;
					UsefulNames.found("vim:Minecraft.startGame", method.name);
				}
			}
		}
	}

}
