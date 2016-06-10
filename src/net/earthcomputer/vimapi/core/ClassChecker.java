package net.earthcomputer.vimapi.core;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

public class ClassChecker extends DetailClassVisitor {

	public ClassChecker() {
		// Must set checkDataFlow to false because maxs are not computed yet
		super(new CheckClassAdapter(null, false));
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new MethodChecker(super.visitMethod(access, name, desc, signature, exceptions));
	}

	private static class MethodChecker extends MethodVisitor {

		public MethodChecker(MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			// Frames not computed yet
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			// Locals not computed yet
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			// Maxs not computed yet
			// TODO: this causes checkEndCode() to return false positives
		}

	}

}
