package net.earthcomputer.vimapi.nbt;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class NBTCompound extends NBTBase implements Iterable<Map.Entry<String, NBTBase>> {

	private Map<String, NBTBase> data = Maps.newHashMap();

	public NBTBase get(String key) {
		return data.get(key);
	}

	public void set(String key, NBTBase val) {
		data.put(key, val);
	}

	public void remove(String key) {
		data.remove(key);
	}

	public boolean has(String key) {
		return data.containsKey(key);
	}

	public int size() {
		return data.size();
	}

	@Override
	public Iterator<Map.Entry<String, NBTBase>> iterator() {
		return data.entrySet().iterator();
	}

	@Override
	public byte getType() {
		return 10;
	}

}
