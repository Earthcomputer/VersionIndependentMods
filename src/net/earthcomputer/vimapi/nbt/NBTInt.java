package net.earthcomputer.vimapi.nbt;

public class NBTInt extends NBTBase {

	private int data;
	
	public NBTInt(int val) {
		this.data = val;
	}
	
	public int get() {
		return data;
	}

	@Override
	public byte getType() {
		return 3;
	}
	
}
