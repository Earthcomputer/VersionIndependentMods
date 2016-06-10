package net.earthcomputer.vimapi.nbt;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class NBTList extends NBTBase implements Iterable<NBTBase> {

	private List<NBTBase> data = Lists.newArrayList();

	public NBTBase get(int ind) {
		return data.get(ind);
	}

	public void set(int ind, NBTBase val) {
		data.set(ind, val);
	}

	public void add(NBTBase val) {
		data.add(val);
	}

	public void add(int ind, NBTBase val) {
		data.add(ind, val);
	}

	public void remove(int ind) {
		data.remove(ind);
	}

	public int indexOf(NBTBase val) {
		return data.indexOf(val);
	}

	public int size() {
		return data.size();
	}

	@Override
	public Iterator<NBTBase> iterator() {
		return data.iterator();
	}

	@Override
	public byte getType() {
		return 9;
	}

}
