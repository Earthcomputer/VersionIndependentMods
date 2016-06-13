package net.earthcomputer.vimapi.core.itf;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;
import net.earthcomputer.vimapi.nbt.NBTBase;
import net.earthcomputer.vimapi.nbt.NBTByte;
import net.earthcomputer.vimapi.nbt.NBTByteArray;
import net.earthcomputer.vimapi.nbt.NBTCompound;
import net.earthcomputer.vimapi.nbt.NBTDouble;
import net.earthcomputer.vimapi.nbt.NBTFloat;
import net.earthcomputer.vimapi.nbt.NBTInt;
import net.earthcomputer.vimapi.nbt.NBTIntArray;
import net.earthcomputer.vimapi.nbt.NBTList;
import net.earthcomputer.vimapi.nbt.NBTLong;
import net.earthcomputer.vimapi.nbt.NBTShort;
import net.earthcomputer.vimapi.nbt.NBTString;

public class NBTInterface {

	@ContainsInlineBytecode
	private static byte getNBTType(@ChangeType("L{vim:NBTBase};") Object nbt) {
		return InlineOps.method(Opcodes.INVOKEVIRTUAL, "{vim:NBTBase}", "{vim:NBTBase.getType}").argObject(nbt)
				.invokeByte();
	}

	@ContainsInlineBytecode
	@ChangeType("L{vim:NBTBase};")
	private static Object createNewNBTByType(byte type) {
		return InlineOps.method(Opcodes.INVOKESTATIC, "{vim:NBTBase}", "{vim:NBTBase.createNewByType}")
				.returnType("L{vim:NBTBase};").param(byte.class).argByte(type).invokeObject();
	}

	@ContainsInlineBytecode
	private static byte[] getNBTByteArrayData(@ChangeType("L{vim:NBTByteArray};") Object nbt) {
		return (byte[]) InlineOps.field(Opcodes.GETFIELD, "{vim:NBTByteArray}", "{vim:NBTByteArray.data}")
				.type(byte[].class).instance(nbt).getObject();
	}

	@ContainsInlineBytecode
	private static void setNBTByteArrayData(@ChangeType("L{vim:NBTByteArray};") Object nbt, byte[] newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTByteArray}", "{vim:NBTByteArray.data}").type(byte[].class)
				.instance(nbt).setObject(newData);
	}

	@ContainsInlineBytecode
	private static byte getNBTByteData(@ChangeType("L{vim:NBTByte};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTByte}", "{vim:NBTByte.data}").instance(nbt).getByte();
	}

	@ContainsInlineBytecode
	private static void setNBTByteData(@ChangeType("L{vim:NBTByte};") Object nbt, byte newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTByte}", "{vim:NBTByte.data}").instance(nbt).setByte(newData);
	}

	@ContainsInlineBytecode
	private static short getNBTShortData(@ChangeType("L{vim:NBTShort};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTShort}", "{vim:NBTShort.data}").instance(nbt).getShort();
	}

	@ContainsInlineBytecode
	private static void setNBTShortData(@ChangeType("L{vim:NBTShort};") Object nbt, short newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTShort}", "{vim:NBTShort.data}").instance(nbt).setShort(newData);
	}

	@ContainsInlineBytecode
	private static int getNBTIntData(@ChangeType("L{vim:NBTInt};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTInt}", "{vim:NBTInt.data}").instance(nbt).getInt();
	}

	@ContainsInlineBytecode
	private static void setNBTIntData(@ChangeType("L{vim:NBTInt};") Object nbt, int newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTInt}", "{vim:NBTInt.data}").instance(nbt).setInt(newData);
	}

	@ContainsInlineBytecode
	private static long getNBTLongData(@ChangeType("L{vim:NBTLong};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTLong}", "{vim:NBTLong.data}").instance(nbt).getLong();
	}

	@ContainsInlineBytecode
	private static void setNBTLongData(@ChangeType("L{vim:NBTLong};") Object nbt, long newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTLong}", "{vim:NBTLong.data}").instance(nbt).setLong(newData);
	}

	@ContainsInlineBytecode
	private static float getNBTFloatData(@ChangeType("L{vim:NBTFloat};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTFloat}", "{vim:NBTFloat.data}").instance(nbt).getFloat();
	}

	@ContainsInlineBytecode
	private static void setNBTFloatData(@ChangeType("L{vim:NBTFloat};") Object nbt, float newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTFloat}", "{vim:NBTFloat.data}").instance(nbt).setFloat(newData);
	}

	@ContainsInlineBytecode
	private static double getNBTDoubleData(@ChangeType("L{vim:NBTDouble};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTDouble}", "{vim:NBTDouble.data}").instance(nbt).getDouble();
	}

	@ContainsInlineBytecode
	private static void setNBTDoubleData(@ChangeType("L{vim:NBTDouble};") Object nbt, double newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTDouble}", "{vim:NBTDouble.data}").instance(nbt).setDouble(newData);
	}

	@ContainsInlineBytecode
	private static List<?> getNBTListData(@ChangeType("L{vim:NBTList};") Object nbt) {
		return (List<?>) InlineOps.field(Opcodes.GETFIELD, "{vim:NBTList}", "{vim:NBTList.data}").type(List.class)
				.instance(nbt).getObject();
	}

	@ContainsInlineBytecode
	private static void setNBTListData(@ChangeType("L{vim:NBTList};") Object nbt, List<?> newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTList}", "{vim:NBTList.data}").type(List.class).instance(nbt)
				.setObject(newData);
	}

	@ContainsInlineBytecode
	private static byte getNBTListType(@ChangeType("L{vim:NBTList};") Object nbt) {
		return InlineOps.field(Opcodes.GETFIELD, "{vim:NBTList}", "{vim:NBTList.tagType}").instance(nbt).getByte();
	}

	@ContainsInlineBytecode
	private static void setNBTListType(@ChangeType("L{vim:NBTList};") Object nbt, byte newType) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTList}", "{vim:NBTList.tagType}").instance(nbt).setByte(newType);
	}

	@ContainsInlineBytecode
	@SuppressWarnings("unchecked")
	private static Map<String, ?> getNBTCompoundData(@ChangeType("L{vim:NBTCompound};") Object nbt) {
		return (Map<String, ?>) InlineOps.field(Opcodes.GETFIELD, "{vim:NBTCompound}", "{vim:NBTCompound.data}")
				.type(Map.class).instance(nbt).getObject();
	}

	@ContainsInlineBytecode
	private static void setNBTCompoundData(@ChangeType("L{vim:NBTCompound};") Object nbt, Map<String, ?> newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTCompound}", "{vim:NBTCompound.data}").type(Map.class).instance(nbt)
				.setObject(newData);
	}

	@ContainsInlineBytecode
	private static int[] getNBTIntArrayData(@ChangeType("L{vim:NBTIntArray};") Object nbt) {
		return (int[]) InlineOps.field(Opcodes.GETFIELD, "{vim:NBTIntArray}", "{vim:NBTIntArray.data}")
				.type(int[].class).instance(nbt).getObject();
	}

	@ContainsInlineBytecode
	private static void setNBTIntArrayData(@ChangeType("L{vim:NBTIntArray};") Object nbt, int[] newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTIntArray}", "{vim:NBTIntArray.data}").type(int[].class).instance(nbt)
				.setObject(newData);
	}

	@ContainsInlineBytecode
	private static String getNBTStringData(@ChangeType("L{vim:NBTString};") Object nbt) {
		return (String) InlineOps.field(Opcodes.GETFIELD, "{vim:NBTString}", "{vim:NBTString.data}").type(String.class)
				.instance(nbt).getObject();
	}

	@ContainsInlineBytecode
	private static void setNBTStringData(@ChangeType("L{vim:NBTString};") Object nbt, String newData) {
		InlineOps.field(Opcodes.PUTFIELD, "{vim:NBTString}", "{vim:NBTString.data}").type(String.class).instance(nbt)
				.setObject(newData);
	}

	@ContainsInlineBytecode
	private static NBTBase translateFromMC(@ChangeType("L{vim:NBTBase};") Object nbt) {
		if (nbt == null) {
			return null;
		}
		byte type = getNBTType(nbt);
		switch (type) {
		case NBTBase.TYPE_BYTE:
			return new NBTByte(getNBTByteData(InlineOps.checkcast(nbt, "{vim:NBTByte}")));
		case NBTBase.TYPE_SHORT:
			return new NBTShort(getNBTShortData(InlineOps.checkcast(nbt, "{vim:NBTShort}")));
		case NBTBase.TYPE_INT:
			return new NBTInt(getNBTIntData(InlineOps.checkcast(nbt, "{vim:NBTInt}")));
		case NBTBase.TYPE_LONG:
			return new NBTLong(getNBTLongData(InlineOps.checkcast(nbt, "{vim:NBTLong}")));
		case NBTBase.TYPE_FLOAT:
			return new NBTFloat(getNBTFloatData(InlineOps.checkcast(nbt, "{vim:NBTFloat}")));
		case NBTBase.TYPE_DOUBLE:
			return new NBTDouble(getNBTDoubleData(InlineOps.checkcast(nbt, "{vim:NBTDouble}")));
		case NBTBase.TYPE_BYTE_ARRAY:
			return new NBTByteArray(getNBTByteArrayData(InlineOps.checkcast(nbt, "{vim:NBTByteArray}")));
		case NBTBase.TYPE_STRING:
			return new NBTString(getNBTStringData(InlineOps.checkcast(nbt, "{vim:NBTString}")));
		case NBTBase.TYPE_LIST:
			NBTList list = new NBTList();
			for (Object nbtInList : getNBTListData(InlineOps.checkcast(nbt, "{vim:NBTList}"))) {
				list.add(translateFromMC(InlineOps.checkcast(nbtInList, "{vim:NBTBase}")));
			}
			return list;
		case NBTBase.TYPE_COMPOUND:
			NBTCompound compound = new NBTCompound();
			for (Map.Entry<String, ?> entry : getNBTCompoundData(InlineOps.checkcast(nbt, "{vim:NBTCompound}"))
					.entrySet()) {
				compound.set(entry.getKey(), translateFromMC(InlineOps.checkcast(entry.getValue(), "{vim:NBTBase}")));
			}
			return compound;
		case NBTBase.TYPE_INT_ARRAY:
			return new NBTIntArray(getNBTIntArrayData(InlineOps.checkcast(nbt, "{vim:NBTIntArray}")));
		default:
			throw new RuntimeException("Unknown NBT type: " + type);
		}
	}

	@ContainsInlineBytecode
	@ChangeType("L{vim:NBTBase};")
	private static Object translateToMC(NBTBase nbt) {
		if (nbt == null) {
			return null;
		}
		byte type = nbt.getType();
		Object mcNBT = createNewNBTByType(type);
		switch (type) {
		case NBTBase.TYPE_BYTE:
			setNBTByteData(InlineOps.checkcast(mcNBT, "{vim:NBTByte}"), ((NBTByte) nbt).getByte());
			break;
		case NBTBase.TYPE_SHORT:
			setNBTShortData(InlineOps.checkcast(mcNBT, "{vim:NBTShort}"), ((NBTShort) nbt).getShort());
			break;
		case NBTBase.TYPE_INT:
			setNBTIntData(InlineOps.checkcast(mcNBT, "{vim:NBTInt}"), ((NBTInt) nbt).getInt());
			break;
		case NBTBase.TYPE_LONG:
			setNBTLongData(InlineOps.checkcast(mcNBT, "{vim:NBTLong}"), ((NBTLong) nbt).getLong());
			break;
		case NBTBase.TYPE_FLOAT:
			setNBTFloatData(InlineOps.checkcast(mcNBT, "{vim:NBTFloat}"), ((NBTFloat) nbt).getFloat());
			break;
		case NBTBase.TYPE_DOUBLE:
			setNBTDoubleData(InlineOps.checkcast(mcNBT, "{vim:NBTDouble}"), ((NBTDouble) nbt).getDouble());
			break;
		case NBTBase.TYPE_BYTE_ARRAY:
			setNBTByteArrayData(InlineOps.checkcast(mcNBT, "{vim:NBTByteArray}"), ((NBTByteArray) nbt).get());
			break;
		case NBTBase.TYPE_STRING:
			setNBTStringData(InlineOps.checkcast(mcNBT, "{vim:NBTString}"), ((NBTString) nbt).get());
			break;
		case NBTBase.TYPE_LIST:
			List<Object> list = Lists.newArrayList();
			for (NBTBase element : (NBTList) nbt) {
				list.add(translateToMC(element));
			}
			setNBTListData(InlineOps.checkcast(mcNBT, "{vim:NBTList}"), list);
			break;
		case NBTBase.TYPE_COMPOUND:
			Map<String, Object> compound = Maps.newHashMap();
			for (Map.Entry<String, NBTBase> entry : (NBTCompound) nbt) {
				compound.put(entry.getKey(), translateToMC(entry.getValue()));
			}
			setNBTCompoundData(InlineOps.checkcast(mcNBT, "{vim:NBTCompound}"), compound);
			break;
		case NBTBase.TYPE_INT_ARRAY:
			setNBTIntArrayData(InlineOps.checkcast(mcNBT, "{vim:NBTIntArray}"), ((NBTIntArray) nbt).get());
			break;
		default:
			throw new RuntimeException("Unknown NBT type: " + type);
		}
		return mcNBT;
	}

}
