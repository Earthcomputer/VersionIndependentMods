package net.earthcomputer.vimapi.nbt;

public class NBTShort extends NBTPrimitive {

	private short data;

	public NBTShort(short data) {
		this.data = data;
	}

	@Override
	public byte getType() {
		return TYPE_SHORT;
	}

	@Override
	public byte getByte() {
		return (byte) data;
	}

	@Override
	public short getShort() {
		return data;
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

}
