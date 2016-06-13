package net.earthcomputer.vimapi.nbt;

public class NBTByteArray extends NBTBase {

	private byte[] data;

	public NBTByteArray(byte[] data) {
		this.data = data;
	}

	public byte[] get() {
		return data;
	}

	@Override
	public byte getType() {
		return TYPE_BYTE_ARRAY;
	}

}
