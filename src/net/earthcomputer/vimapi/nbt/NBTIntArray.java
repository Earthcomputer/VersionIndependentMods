package net.earthcomputer.vimapi.nbt;

public class NBTIntArray extends NBTBase {

	private int[] data;

	public NBTIntArray(int[] val) {
		this.data = val;
	}

	public int[] get() {
		return data;
	}

	@Override
	public byte getType() {
		return TYPE_INT_ARRAY;
	}

}
