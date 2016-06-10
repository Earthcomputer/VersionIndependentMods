package net.earthcomputer.vimapi.core;

public class Bytecode {

	private Bytecode() {
	}

	public static void insn(int opcode) {
	}

	public static void field(int opcode, String owner, String field, String desc) {
	}

	public static void iinc(int var, int amt) {
	}

	public static void intInsn(int opcode, int operand) {
	}

	public static void jump(int opcode, String labelName) {
	}

	public static void label(String labelName) {
	}

	public static void ldc(Object toLoad) {
	}

	public static void method(int opcode, String owner, String method, String desc, boolean itf) {
	}

	public static void multianewarray(String desc, int dims) {
	}

	public static void type(int opcode, String cst) {
	}

	public static void var(int opcode, int var) {
	}

}
