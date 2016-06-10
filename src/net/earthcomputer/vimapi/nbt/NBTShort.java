package net.earthcomputer.vimapi.nbt;

public class NBTShort extends NBTBase {

	private short data;

	public NBTShort(short data) {
		this.data = data;
	}

	public short get() {
		return data;
	}

	@Override
	public byte getType() {
		return 2;
	}

}
