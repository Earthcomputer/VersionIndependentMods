package net.earthcomputer.vimapi.nbt;

public class NBTString extends NBTBase {

	private String data;

	public NBTString(String val) {
		this.data = val;
	}

	public String get() {
		return data;
	}

	@Override
	public byte getType() {
		return 8;
	}

}
