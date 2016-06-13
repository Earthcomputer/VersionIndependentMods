package net.earthcomputer.vimapi.nbt;

public abstract class NBTBase {

	public static final byte TYPE_BYTE = 1;
	public static final byte TYPE_SHORT = 2;
	public static final byte TYPE_INT = 3;
	public static final byte TYPE_LONG = 4;
	public static final byte TYPE_FLOAT = 5;
	public static final byte TYPE_DOUBLE = 6;
	public static final byte TYPE_BYTE_ARRAY = 7;
	public static final byte TYPE_STRING = 8;
	public static final byte TYPE_LIST = 9;
	public static final byte TYPE_COMPOUND = 10;
	public static final byte TYPE_INT_ARRAY = 11;
	
	public abstract byte getType();
	
}
