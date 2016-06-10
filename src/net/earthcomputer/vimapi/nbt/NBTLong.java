package net.earthcomputer.vimapi.nbt;

public class NBTLong extends NBTBase {

	private long data;

	public NBTLong(long data) {
		this.data = data;
	}

	public long get() {
		return data;
	}

	@Override
	public byte getType() {
		return 4;
	}

}
