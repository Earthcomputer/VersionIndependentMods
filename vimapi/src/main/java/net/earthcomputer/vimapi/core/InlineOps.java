package net.earthcomputer.vimapi.core;

/**
 * A class which allows for a small number low-level operations within Java
 * code. This class both has support for the obfuscation format (see
 * {@link net.earthcomputer.vimapi.core.tweaker.BytecodeTransformer#obfuscate(String)
 * BytecodeTransformer.obfuscate(String)}) and accessing private fields and
 * methods, the latter of which the {@link Bytecode} alternative cannot do. Also
 * unlike the {@link Bytecode} counterpart, using <code>InlineOps</code> does
 * not delete other Java code within the same method, which makes the code
 * easier to both read and write.
 */
public class InlineOps {

	private InlineOps() {
	}

	/**
	 * Allows for type-safety of obfuscated classes at runtime
	 */
	public static Object checkcast(Object obj, String cst) {
		return null;
	}

	/**
	 * Invokes a method
	 * 
	 * @see #method(int, String, String)
	 */
	public static MethodOp method(int opcode, Class<?> owner, String name) {
		return null;
	}

	/**
	 * Invokes a method. Both <code>owner</code> and <code>name</code> can be in
	 * the obfuscation format. This method returns a {@link MethodOp} object
	 * which must be used to specify additional details and finally invoke the
	 * method. A typical use would be:<br/>
	 * <code> (String) // cast to String, required by the compiler<br/>
	 * InlineOps.method(Opcodes.INVOKEVIRTUAL, "{vim:Foo}", "{vim:Foo.bar}")
	 * <br/>
	 * &nbsp;&nbsp;&nbsp;.returnType(String.class) // not required for primitive types<br/>
	 * &nbsp;&nbsp;&nbsp;.param("{vim:baz}") // required parameter 1<br/>
	 * &nbsp;&nbsp;&nbsp;.param(int.class) // required parameter 2<br/>
	 * &nbsp;&nbsp;&nbsp;.argObject(instanceOfFoo) // instances also use the arg
	 * method (not required for static invocations)<br/>
	 * &nbsp;&nbsp;&nbsp;.argObject(instanceOfBaz) // required argument 1<br/>
	 * &nbsp;&nbsp;&nbsp;.argInt(666) // required argument 2<br/>
	 * &nbsp;&nbsp;&nbsp;.invokeObject(); // finally invoke the method. The
	 * method returns a String, which is a subclass of java.lang.Object, so we
	 * use invokeObject</code>
	 */
	public static MethodOp method(int opcode, String owner, String name) {
		return null;
	}

	/**
	 * Gets or sets a field
	 * 
	 * @see #field(int, String, String)
	 */
	public static FieldOp field(int opcode, Class<?> owner, String name) {
		return null;
	}

	/**
	 * Gets or sets a field. Both <code>owner</code> and <code>name</code> can
	 * be in the obfuscation format. This method returns a {@link FieldOp}
	 * object which must be used to specify additional details and finally
	 * get/set the field. A typical use would be:<br/>
	 * <code> InlineOps.field(Opcodes.PUTFIELD, "{vim:Foo}", "{vim:Foo.bar}")
	 * <br/>
	 * &nbsp;&nbsp;&nbsp;.type(String.class) // not required for primitive types
	 * <br/>
	 * &nbsp;&nbsp;&nbsp;.instance(instanceOfFoo) // not required for static
	 * fields<br/>
	 * &nbsp;&nbsp;&nbsp;.setObject("baz"); // finally set the field</code>
	 */
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
