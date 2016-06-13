package net.earthcomputer.vimapi.nbt;

import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

public class NBTCompound extends NBTBase implements Iterable<Map.Entry<String, NBTBase>> {

	private Map<String, NBTBase> data = Maps.newHashMap();

	public NBTBase get(String key) {
		return data.get(key);
	}

	public byte[] getByteArray(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTByteArray) {
			return ((NBTByteArray) nbt).get();
		}
		return new byte[0];
	}

	public byte getByte(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getByte();
		}
		return 0;
	}

	public short getShort(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getShort();
		}
		return 0;
	}

	public int getInt(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getInt();
		}
		return 0;
	}

	public long getLong(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getLong();
		}
		return 0;
	}

	public float getFloat(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getFloat();
		}
		return 0;
	}

	public double getDouble(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTPrimitive) {
			return ((NBTPrimitive) nbt).getDouble();
		}
		return 0;
	}

	public int[] getIntArray(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTIntArray) {
			return ((NBTIntArray) nbt).get();
		}
		return new int[0];
	}

	public String getString(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTString) {
			return ((NBTString) nbt).get();
		}
		return "";
	}

	public NBTList getList(String key, int tagType) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTList) {
			NBTList nbtList = (NBTList) nbt;
			if (nbtList.size() == 0 || nbtList.getTagType() == tagType) {
				return nbtList;
			}
		}
		return new NBTList();
	}

	public NBTCompound getCompound(String key) {
		NBTBase nbt = get(key);
		if (nbt instanceof NBTCompound) {
			return (NBTCompound) nbt;
		}
		return new NBTCompound();
	}

	public void set(String key, NBTBase val) {
		data.put(key, val);
	}

	public void setByteArray(String key, byte[] val) {
		set(key, new NBTByteArray(val));
	}

	public void setByte(String key, byte val) {
		set(key, new NBTByte(val));
	}

	public void setShort(String key, short val) {
		set(key, new NBTShort(val));
	}

	public void setInt(String key, int val) {
		set(key, new NBTInt(val));
	}

	public void setLong(String key, long val) {
		set(key, new NBTLong(val));
	}

	public void setFloat(String key, float val) {
		set(key, new NBTFloat(val));
	}

	public void setDouble(String key, double val) {
		set(key, new NBTDouble(val));
	}

	public void setIntArray(String key, int[] val) {
		set(key, new NBTIntArray(val));
	}

	public void setString(String key, String val) {
		set(key, new NBTString(val));
	}

	public void remove(String key) {
		data.remove(key);
	}

	public boolean has(String key) {
		return data.containsKey(key);
	}

	public boolean has(String key, byte type) {
		return has(key) && get(key).getType() == type;
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
		return TYPE_COMPOUND;
	}

}
