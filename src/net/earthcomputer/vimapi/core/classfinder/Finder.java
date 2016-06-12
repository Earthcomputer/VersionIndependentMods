package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.tree.ClassNode;

public abstract class Finder {

	public abstract void accept(String className, ClassConstants constants, ClassNode node);

}
