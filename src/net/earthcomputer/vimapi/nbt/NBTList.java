package net.earthcomputer.vimapi.nbt;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class NBTList extends NBTBase implements Iterable<NBTBase> {

	private List<NBTBase> data = Lists.newArrayList();
	private byte tagType;

	public NBTBase get(int ind) {
		return data.get(ind);
	}

	public byte getTagType() {
		return size() == 0 ? 0 : tagType;
	}

	public byte[] getByteArray(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTByteArray) {
			return ((NBTByteArray) nbt).get();
		}
		return new byte[0];
	}

	public byte getByte(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getByte();
		}
		return 0;
	}

	public short getShort(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getShort();
		}
		return 0;
	}

	public int getInt(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getInt();
		}
		return 0;
	}

	public long getLong(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getLong();
		}
		return 0;
	}

	public float getFloat(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getFloat();
		}
		return 0;
	}

	public double getDouble(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getDouble();
		}
		return 0;
	}

	public int[] getIntArray(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTIntArray) {
			return ((NBTIntArray) nbt).get();
		}
		return new int[0];
	}

	public String getString(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTString) {
			return ((NBTString) nbt).get();
		}
		return "";
	}

	public NBTList getList(int ind, byte tagType) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTList) {
			NBTList nbtList = (NBTList) nbt;
			if (nbtList.size() == 0 || nbtList.getTagType() == tagType) {
				return nbtList;
			}
		}
		return new NBTList();
	}

	public NBTCompound getCompound(int ind) {
		NBTBase nbt = get(ind);
		if (nbt instanceof NBTCompound) {
			return (NBTCompound) nbt;
		}
		return new NBTCompound();
	}

	// TODO: Lots and lots of setters

	public void set(int ind, NBTBase val) {
		data.set(ind, val);
	}

	public void add(NBTBase val) {
		if (size() == 0) {
			tagType = val.getType();
		} else {
			if (val.getType() != tagType) {
				throw new IllegalArgumentException("Adding wrong type to NBTList");
			}
		}
		data.add(val);
	}

	public void add(int ind, NBTBase val) {
		if (size() == 0) {
			tagType = val.getType();
		} else {
			if (val.getType() != tagType) {
				throw new IllegalArgumentException("Adding wrong type to NBTList");
			}
		}
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
		return TYPE_LIST;
	}

}
