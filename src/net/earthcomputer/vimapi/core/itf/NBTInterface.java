package net.earthcomputer.vimapi.core.itf;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.earthcomputer.vimapi.core.Bytecode;
import net.earthcomputer.vimapi.core.BytecodeMethod;
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

	@BytecodeMethod
	private static byte getNBTType(@ChangeType("L{vim:NBTBase};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.method(Opcodes.INVOKEVIRTUAL, "{vim:NBTBase}", "{vim:NBTBase.getType}", "()B", false);
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	@ChangeType("L{vim:NBTBase};")
	private static Object createNewNBTByType(byte type) {
		Bytecode.var(Opcodes.ILOAD, 0);
		Bytecode.insn(Opcodes.I2B);
		Bytecode.method(Opcodes.INVOKESTATIC, "{vim:NBTBase}", "{vim:NBTBase.createNewByType}", "(B)L{vim:NBTBase};", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static byte[] getNBTByteArrayData(@ChangeType("L{vim:NBTByteArray};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTByteArray}", "{vim:NBTByteArray.data}", "[B");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTByteArrayData(@ChangeType("L{vim:NBTByteArray};") Object nbt, byte[] newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTByteArray}", "{vim:NBTByteArray.data}", "[B");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static byte getNBTByteData(@ChangeType("L{vim:NBTByte};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTByte}", "{vim:NBTByte.data}", "B");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTByteData(@ChangeType("L{vim:NBTByte};") Object nbt, byte newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.insn(Opcodes.I2B);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTByte}", "{vim:NBTByte.data}", "B");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static short getNBTShortData(@ChangeType("L{vim:NBTShort};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTShort}", "{vim:NBTShort.data}", "S");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTShortData(@ChangeType("L{vim:NBTShort};") Object nbt, short newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.insn(Opcodes.I2S);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTShort}", "{vim:NBTShort.data}", "S");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static int getNBTIntData(@ChangeType("L{vim:NBTInt};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTInt}", "{vim:NBTInt.data}", "I");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTIntData(@ChangeType("L{vim:NBTInt};") Object nbt, int newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTInt}", "{vim:NBTInt.data}", "I");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static long getNBTLongData(@ChangeType("L{vim:NBTLong};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTLong}", "{vim:NBTLong.data}", "J");
		Bytecode.insn(Opcodes.LRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTLongData(@ChangeType("L{vim:NBTLong};") Object nbt, long newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.LLOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTLong}", "{vim:NBTLong.data}", "J");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static float getNBTFloatData(@ChangeType("L{vim:NBTFloat};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTFloat}", "{vim:NBTFloat.data}", "F");
		Bytecode.insn(Opcodes.FRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTFloatData(@ChangeType("L{vim:NBTFloat};") Object nbt, float newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.FLOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTFloat}", "{vim:NBTFloat.data}", "F");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static double getNBTDoubleData(@ChangeType("L{vim:NBTDouble};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTDouble}", "{vim:NBTDouble.data}", "D");
		Bytecode.insn(Opcodes.DRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTDoubleData(@ChangeType("L{vim:NBTDouble};") Object nbt, double newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.DLOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTDouble}", "{vim:NBTDouble.data}", "D");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static List<?> getNBTListData(@ChangeType("L{vim:NBTList};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTList}", "{vim:NBTList.data}", "Ljava/util/List;");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTListData(@ChangeType("L{vim:NBTList};") Object nbt, List<?> newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTList}", "{vim:NBTList.data}", "Ljava/util/List;");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static byte getNBTListType(@ChangeType("L{vim:NBTList};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTList}", "{vim:NBTList.tagType}", "B");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTListType(@ChangeType("L{vim:NBTList};") Object nbt, byte newType) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.insn(Opcodes.I2B);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTList}", "{vim:NBTList.tagType}", "B");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static Map<String, ?> getNBTCompoundData(@ChangeType("L{vim:NBTCompound};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTCompound}", "{vim:NBTCompound.data}", "Ljava/util/Map;");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTCompoundData(@ChangeType("L{vim:NBTCompound};") Object nbt, Map<String, ?> newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTCompound}", "{vim:NBTCompound.data}", "Ljava/util/Map;");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static int[] getNBTIntArrayData(@ChangeType("L{vim:NBTIntArray};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTIntArray}", "{vim:NBTIntArray.data}", "[I");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTIntArrayData(@ChangeType("L{vim:NBTIntArray};") Object nbt, int[] newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTIntArray}", "{vim:NBTIntArray.data}", "[I");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static String getNBTStringData(@ChangeType("L{vim:NBTString};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{vim:NBTString}", "{vim:NBTString.data}", "Ljava/lang/String;");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTStringData(@ChangeType("L{vim:NBTString};") Object nbt, String newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{vim:NBTString}", "{vim:NBTString.data}", "Ljava/lang/String;");
		Bytecode.insn(Opcodes.RETURN);
	}

	@ContainsInlineBytecode
	private static NBTBase translateFromMC(@ChangeType("L{vim:NBTBase};") Object nbt) {
		if (nbt == null) {
			return null;
		}
		byte type = getNBTType(nbt);
		switch (type) {
		case 1:
			return new NBTByte(getNBTByteData(InlineOps.checkcast(nbt, "{vim:NBTByte}")));
		case 2:
			return new NBTShort(getNBTShortData(InlineOps.checkcast(nbt, "{vim:NBTShort}")));
		case 3:
			return new NBTInt(getNBTIntData(InlineOps.checkcast(nbt, "{vim:NBTInt}")));
		case 4:
			return new NBTLong(getNBTLongData(InlineOps.checkcast(nbt, "{vim:NBTLong}")));
		case 5:
			return new NBTFloat(getNBTFloatData(InlineOps.checkcast(nbt, "{vim:NBTFloat}")));
		case 6:
			return new NBTDouble(getNBTDoubleData(InlineOps.checkcast(nbt, "{vim:NBTDouble}")));
		case 7:
			return new NBTByteArray(getNBTByteArrayData(InlineOps.checkcast(nbt, "{vim:NBTByteArray}")));
		case 8:
			return new NBTString(getNBTStringData(InlineOps.checkcast(nbt, "{vim:NBTString}")));
		case 9:
			NBTList list = new NBTList();
			for (Object nbtInList : getNBTListData(InlineOps.checkcast(nbt, "{vim:NBTList}"))) {
				list.add(translateFromMC(InlineOps.checkcast(nbtInList, "{vim:NBTBase}")));
			}
			return list;
		case 10:
			NBTCompound compound = new NBTCompound();
			for (Map.Entry<String, ?> entry : getNBTCompoundData(InlineOps.checkcast(nbt, "{vim:NBTCompound}"))
					.entrySet()) {
				compound.set(entry.getKey(), translateFromMC(InlineOps.checkcast(entry.getValue(), "{vim:NBTBase}")));
			}
			return compound;
		case 11:
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
		case 1:
			setNBTByteData(InlineOps.checkcast(mcNBT, "{vim:NBTByte}"), ((NBTByte) nbt).get());
			break;
		case 2:
			setNBTShortData(InlineOps.checkcast(mcNBT, "{vim:NBTShort}"), ((NBTShort) nbt).get());
			break;
		case 3:
			setNBTIntData(InlineOps.checkcast(mcNBT, "{vim:NBTInt}"), ((NBTInt) nbt).get());
			break;
		case 4:
			setNBTLongData(InlineOps.checkcast(mcNBT, "{vim:NBTLong}"), ((NBTLong) nbt).get());
			break;
		case 5:
			setNBTFloatData(InlineOps.checkcast(mcNBT, "{vim:NBTFloat}"), ((NBTFloat) nbt).get());
			break;
		case 6:
			setNBTDoubleData(InlineOps.checkcast(mcNBT, "{vim:NBTDouble}"), ((NBTDouble) nbt).get());
			break;
		case 7:
			setNBTByteArrayData(InlineOps.checkcast(mcNBT, "{vim:NBTByteArray}"), ((NBTByteArray) nbt).get());
			break;
		case 8:
			setNBTStringData(InlineOps.checkcast(mcNBT, "{vim:NBTString}"), ((NBTString) nbt).get());
			break;
		case 9:
			List<Object> list = Lists.newArrayList();
			for (NBTBase element : (NBTList) nbt) {
				list.add(translateToMC(element));
			}
			setNBTListData(InlineOps.checkcast(mcNBT, "{vim:NBTList}"), list);
			break;
		case 10:
			Map<String, Object> compound = Maps.newHashMap();
			for (Map.Entry<String, NBTBase> entry : (NBTCompound) nbt) {
				compound.put(entry.getKey(), translateToMC(entry.getValue()));
			}
			setNBTCompoundData(InlineOps.checkcast(mcNBT, "{vim:NBTCompound}"), compound);
			break;
		case 11:
			setNBTIntArrayData(InlineOps.checkcast(mcNBT, "{vim:NBTIntArray}"), ((NBTIntArray) nbt).get());
			break;
		default:
			throw new RuntimeException("Unknown NBT type: " + type);
		}
		return mcNBT;
	}

}
