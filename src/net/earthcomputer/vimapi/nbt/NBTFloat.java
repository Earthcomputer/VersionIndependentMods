package net.earthcomputer.vimapi.nbt;

public class NBTFloat extends NBTBase {

	private float data;

	public NBTFloat(float val) {
		this.data = val;
	}

	public float get() {
		return data;
	}

	@Override
	public byte getType() {
		return 5;
	}

}
