package net.earthcomputer.vimapi.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A class visitor which wraps another class visitor and reports the
 * field/method name and descriptor and possibly line number when an exception
 * is thrown.
 */
public class DetailClassVisitor extends ClassVisitor {

	private String currentMemberName;
	private String currentMemberDesc;
	private int lineNumber;

	public DetailClassVisitor(ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
	}

	private void throwException(Throwable cause, boolean withLineNumber) {
		if (withLineNumber) {
			throw new ClassVisitFailedException(currentMemberName, currentMemberDesc, lineNumber, cause);
		} else {
			throw new ClassVisitFailedException(currentMemberName, currentMemberDesc, cause);
		}
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		currentMemberName = name;
		currentMemberDesc = desc;
		try {
			return new DetailFieldVisitor(super.visitField(access, name, desc, signature, value));
		} catch (Exception e) {
			throwException(e, false);
			return null;
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		currentMemberName = name;
		currentMemberDesc = desc;
		try {
			return new DetailMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
		} catch (Exception e) {
			throwException(e, false);
			return null;
		}
	}

	private class DetailFieldVisitor extends FieldVisitor {

		public DetailFieldVisitor(FieldVisitor fv) {
			super(Opcodes.ASM5, fv);
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
			currentMemberName = null;
			currentMemberDesc = null;
		}

	}

	private class DetailMethodVisitor extends MethodVisitor {

		public DetailMethodVisitor(MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitInsn(int opcode) {
			try {
				super.visitInsn(opcode);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			try {
				super.visitIntInsn(opcode, operand);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			try {
				super.visitVarInsn(opcode, var);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			try {
				super.visitTypeInsn(opcode, type);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			try {
				super.visitFieldInsn(opcode, owner, name, desc);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
			try {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
			try {
				super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			try {
				super.visitJumpInsn(opcode, label);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitLabel(Label label) {
			try {
				super.visitLabel(label);
			} catch (Exception e) {
				throwException(e, false);
			}
		}

		@Override
		public void visitLdcInsn(Object cst) {
			try {
				super.visitLdcInsn(cst);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			try {
				super.visitIincInsn(var, increment);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
			try {
				super.visitTableSwitchInsn(min, max, dflt, labels);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			try {
				super.visitLookupSwitchInsn(dflt, keys, labels);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			try {
				super.visitMultiANewArrayInsn(desc, dims);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
			try {
				super.visitTryCatchBlock(start, end, handler, type);
			} catch (Exception e) {
				throwException(e, false);
			}
		}

		@Override
		public void visitLineNumber(int line, Label start) {
			lineNumber = line;
			try {
				super.visitLineNumber(line, start);
			} catch (Exception e) {
				throwException(e, true);
			}
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			try {
				super.visitMaxs(maxStack, maxLocals);
			} catch (Exception e) {
				throwException(e, false);
			}
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
			currentMemberName = null;
			currentMemberDesc = null;
		}

	}

	public static class ClassVisitFailedException extends RuntimeException {
		private static final long serialVersionUID = 5486989557862241268L;
		private String currentMemberName;
		private String currentMemberDesc;
		private int lineNumber;

		public ClassVisitFailedException(String currentMemberName, String currentMemberDesc, Throwable cause) {
			this(currentMemberName, currentMemberDesc, -1, cause);
		}

		public ClassVisitFailedException(String currentMemberName, String currentMemberDesc, int lineNumber,
				Throwable cause) {
			super(cause);
			this.currentMemberName = currentMemberName;
			this.currentMemberDesc = currentMemberDesc;
			this.lineNumber = lineNumber;
		}

		public String getCurrentMemberName() {
			return currentMemberName;
		}

		public String getCurrentMemberDesc() {
			return currentMemberDesc;
		}

		public int getLineNumber() {
			return lineNumber;
		}
	}

}
