package net.earthcomputer.vimapi.nbt;

public class NBTFloat extends NBTPrimitive {

	private float data;

	public NBTFloat(float val) {
		this.data = val;
	}

	@Override
	public byte getType() {
		return TYPE_FLOAT;
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
		return (long) data;
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
