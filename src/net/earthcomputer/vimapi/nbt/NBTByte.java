package net.earthcomputer.vimapi.nbt;

public class NBTByte extends NBTBase {

	private byte data;

	public NBTByte(byte data) {
		this.data = data;
	}

	public byte get() {
		return data;
	}

	@Override
	public byte getType() {
		return 1;
	}

}
