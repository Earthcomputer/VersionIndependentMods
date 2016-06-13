package net.earthcomputer.vimapi.nbt;

public class NBTByte extends NBTPrimitive {

	private byte data;

	public NBTByte(byte data) {
		this.data = data;
	}

	@Override
	public byte getType() {
		return TYPE_BYTE;
	}

	@Override
	public byte getByte() {
		return data;
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
