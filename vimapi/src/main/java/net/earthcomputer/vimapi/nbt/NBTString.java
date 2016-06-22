package net.earthcomputer.vimapi.nbt;

public class NBTString extends NBTBase {

	private String data;

	public NBTString(String val) {
		if (val.isEmpty()) {
			throw new IllegalArgumentException("NBTString cannot contain an empty string");
		}
		this.data = val;
	}

	public String get() {
		return data;
	}

	@Override
	public byte getType() {
		return TYPE_STRING;
	}

	@Override
	public NBTString copy() {
		return new NBTString(data);
	}

}
