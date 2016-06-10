package net.earthcomputer.vimapi.nbt;

public class NBTDouble extends NBTBase {

	private double data;

	public NBTDouble(double val) {
		this.data = val;
	}

	public double get() {
		return data;
	}

	@Override
	public byte getType() {
		return 6;
	}

}
