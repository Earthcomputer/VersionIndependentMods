package net.earthcomputer.vimapi.nbt;

public class NBTLong extends NBTPrimitive {

	private long data;

	public NBTLong(long data) {
		this.data = data;
	}

	@Override
	public byte getType() {
		return TYPE_LONG;
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
		return (int) data;
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
