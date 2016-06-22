package net.earthcomputer.vimapi.nbt;

public class NBTDouble extends NBTPrimitive {

	private double data;

	public NBTDouble(double val) {
		this.data = val;
	}

	@Override
	public byte getType() {
		return TYPE_DOUBLE;
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
		return (float) data;
	}

	@Override
	public double getDouble() {
		return data;
	}

	@Override
	public NBTDouble copy() {
		return new NBTDouble(data);
	}

}
