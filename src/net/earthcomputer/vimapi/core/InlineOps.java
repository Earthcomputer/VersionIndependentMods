package net.earthcomputer.vimapi.core;

public class InlineOps {

	private InlineOps() {
	}

	public static Object checkcast(Object obj, String cst) {
		return null;
	}

	public static MethodOp method(int opcode, Class<?> owner, String name) {
		return null;
	}

	public static MethodOp method(int opcode, String owner, String name) {
		return null;
	}

	public static class MethodOp {
		private MethodOp() {
		}

		public MethodOp param(Class<?> type) {
			return null;
		}

		public MethodOp param(String type) {
			return null;
		}

		public MethodOp returnType(Class<?> type) {
			return null;
		}

		public MethodOp returnType(String type) {
			return null;
		}

		public MethodOp arg(Object arg) {
			return null;
		}

		public boolean invokeBoolean() {
			return false;
		}

		public byte invokeByte() {
			return 0;
		}

		public char invokeChar() {
			return 0;
		}

		public double invokeDouble() {
			return 0;
		}

		public float invokeFloat() {
			return 0;
		}

		public int invokeInt() {
			return 0;
		}

		public long invokeLong() {
			return 0;
		}

		public short invokeShort() {
			return 0;
		}

		public void invokeVoid() {
		}

		public Object invokeObject() {
			return null;
		}
	}

}
