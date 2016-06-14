package net.earthcomputer.vimapi.nbt;

public class NBTInt extends NBTPrimitive {

	private int data;
	
	public NBTInt(int val) {
		this.data = val;
	}

	@Override
	public byte getType() {
		return TYPE_INT;
	}

	@Override
	public byte getByte() {
		return (byte) data;
	}

	@Override
	public short getShort() {
		return (short) data;
	}

	@Override
	public int getInt() {
		return data;
	}

	@Override
	public long getLong() {
		return data;
	}

	@Override
	public float getFloat() {
		return data;
	}

	@Override
	public double getDouble() {
		return data;
	}

	@Override
	public NBTInt copy() {
		return new NBTInt(data);
	}
	
}
