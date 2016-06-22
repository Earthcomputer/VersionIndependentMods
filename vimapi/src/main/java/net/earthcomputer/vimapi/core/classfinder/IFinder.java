package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.tree.ClassNode;

/**
 * A class which looks for one vanilla class or a logical set of vanilla
 * classes, and maybe some of its members. Typically, an implementation this
 * class calls {@link UsefulNames#found(String, String)} when it finds something
 * it's looking for
 */
public interface IFinder {

	/**
	 * Visit a vanilla class. <code>className</code> is obfuscated as it is in
	 * the JAR
	 */
	public abstract void accept(String className, ClassConstants constants, ClassNode node);

}
