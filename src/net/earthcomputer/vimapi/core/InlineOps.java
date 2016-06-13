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

	public static FieldOp field(int opcode, Class<?> owner, String name) {
		return null;
	}

	public static FieldOp field(int opcode, String owner, String name) {
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

		public MethodOp argBoolean(boolean arg) {
			return null;
		}

		public MethodOp argByte(byte arg) {
			return null;
		}

		public MethodOp argChar(char arg) {
			return null;
		}

		public MethodOp argDouble(double arg) {
			return null;
		}

		public MethodOp argFloat(float arg) {
			return null;
		}

		public MethodOp argInt(int arg) {
			return null;
		}

		public MethodOp argLong(long arg) {
			return null;
		}

		public MethodOp argShort(short arg) {
			return null;
		}

		public MethodOp argObject(Object arg) {
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

	public static class FieldOp {
		private FieldOp() {
		}

		public FieldOp type(Class<?> type) {
			return null;
		}

		public FieldOp type(String type) {
			return null;
		}

		public FieldOp instance(Object instance) {
			return null;
		}

		public boolean getBoolean() {
			return false;
		}

		public byte getByte() {
			return 0;
		}

		public char getChar() {
			return 0;
		}

		public double getDouble() {
			return 0;
		}

		public float getFloat() {
			return 0;
		}

		public int getInt() {
			return 0;
		}

		public long getLong() {
			return 0;
		}

		public short getShort() {
			return 0;
		}

		public Object getObject() {
			return null;
		}

		public void setBoolean(boolean val) {
		}

		public void setByte(byte val) {
		}

		public void setChar(char val) {
		}

		public void setDouble(double val) {
		}

		public void setFloat(float val) {
		}

		public void setInt(int val) {
		}

		public void setLong(long val) {
		}

		public void setShort(short val) {
		}

		public void setObject(Object val) {
		}
	}

}
