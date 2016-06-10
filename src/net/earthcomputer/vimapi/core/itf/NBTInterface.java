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
	private static byte getNBTType(@ChangeType("L{NBT_BASE};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.method(Opcodes.INVOKEVIRTUAL, "{NBT_BASE}", "{NBT_BASE_GETTYPE}", "()B", false);
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	@ChangeType("L{NBT_BASE};")
	private static Object createNewNBTByType(byte type) {
		Bytecode.var(Opcodes.ILOAD, 0);
		Bytecode.insn(Opcodes.I2B);
		Bytecode.method(Opcodes.INVOKESTATIC, "{NBT_BASE}", "{NBT_BASE_CREATENEWBYTYPE}", "(B)L{NBT_BASE};", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static byte[] getNBTByteArrayData(@ChangeType("L{NBT_BYTE_ARRAY};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_BYTE_ARRAY}", "{NBT_BYTE_ARRAY_DATA}", "[B");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTByteArrayData(@ChangeType("L{NBT_BYTE_ARRAY};") Object nbt, byte[] newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_BYTE_ARRAY}", "{NBT_BYTE_ARRAY_DATA}", "[B");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static byte getNBTByteData(@ChangeType("L{NBT_BYTE};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_BYTE}", "{NBT_BYTE_DATA}", "B");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTByteData(@ChangeType("L{NBT_BYTE};") Object nbt, byte newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.insn(Opcodes.I2B);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_BYTE}", "{NBT_BYTE_DATA}", "B");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static short getNBTShortData(@ChangeType("L{NBT_SHORT};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_SHORT}", "{NBT_SHORT_DATA}", "S");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTShortData(@ChangeType("L{NBT_SHORT};") Object nbt, short newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.insn(Opcodes.I2S);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_SHORT}", "{NBT_SHORT_DATA}", "S");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static int getNBTIntData(@ChangeType("L{NBT_INT};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_INT}", "{NBT_INT_DATA}", "I");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTIntData(@ChangeType("L{NBT_INT};") Object nbt, int newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_INT}", "{NBT_INT_DATA}", "I");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static long getNBTLongData(@ChangeType("L{NBT_LONG};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_LONG}", "{NBT_LONG_DATA}", "J");
		Bytecode.insn(Opcodes.LRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTLongData(@ChangeType("L{NBT_LONG};") Object nbt, long newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.LLOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_LONG}", "{NBT_LONG_DATA}", "J");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static float getNBTFloatData(@ChangeType("L{NBT_FLOAT};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_FLOAT}", "{NBT_FLOAT_DATA}", "F");
		Bytecode.insn(Opcodes.FRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTFloatData(@ChangeType("L{NBT_FLOAT};") Object nbt, float newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.FLOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_FLOAT}", "{NBT_FLOAT_DATA}", "F");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static double getNBTDoubleData(@ChangeType("L{NBT_DOUBLE};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_DOUBLE}", "{NBT_DOUBLE_DATA}", "D");
		Bytecode.insn(Opcodes.DRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTDoubleData(@ChangeType("L{NBT_DOUBLE};") Object nbt, double newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.DLOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_DOUBLE}", "{NBT_DOUBLE_DATA}", "D");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static List<?> getNBTListData(@ChangeType("L{NBT_LIST};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_LIST}", "{NBT_LIST_DATA}", "Ljava/util/List;");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTListData(@ChangeType("L{NBT_LIST};") Object nbt, List<?> newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_LIST}", "{NBT_LIST_DATA}", "Ljava/util/List;");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static byte getNBTListType(@ChangeType("L{NBT_LIST};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_LIST}", "{NBT_LIST_TAG_TYPE}", "B");
		Bytecode.insn(Opcodes.IRETURN);
		return 0;
	}

	@BytecodeMethod
	private static void setNBTListType(@ChangeType("L{NBT_LIST};") Object nbt, byte newType) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ILOAD, 1);
		Bytecode.insn(Opcodes.I2B);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_LIST}", "{NBT_LIST_TAG_TYPE}", "B");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static Map<String, ?> getNBTCompoundData(@ChangeType("L{NBT_COMPOUND};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_COMPOUND}", "{NBT_COMPOUND_DATA}", "Ljava/util/Map;");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTCompoundData(@ChangeType("L{NBT_COMPOUND};") Object nbt, Map<String, ?> newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_COMPOUND}", "{NBT_COMPOUND_DATA}", "Ljava/util/Map;");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static int[] getNBTIntArrayData(@ChangeType("L{NBT_INT_ARRAY};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_INT_ARRAY}", "{NBT_INT_ARRAY_DATA}", "[I");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTIntArrayData(@ChangeType("L{NBT_INT_ARRAY};") Object nbt, int[] newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_INT_ARRAY}", "{NBT_INT_ARRAY_DATA}", "[I");
		Bytecode.insn(Opcodes.RETURN);
	}

	@BytecodeMethod
	private static String getNBTStringData(@ChangeType("L{NBT_STRING};") Object nbt) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.field(Opcodes.GETFIELD, "{NBT_STRING}", "{NBT_STRING_DATA}", "Ljava/lang/String;");
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static void setNBTStringData(@ChangeType("L{NBT_STRING};") Object nbt, String newData) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.field(Opcodes.PUTFIELD, "{NBT_STRING}", "{NBT_STRING_DATA}", "Ljava/lang/String;");
		Bytecode.insn(Opcodes.RETURN);
	}

	@ContainsInlineBytecode
	private static NBTBase translateFromMC(@ChangeType("L{NBT_BASE};") Object nbt) {
		if (nbt == null) {
			return null;
		}
		byte type = getNBTType(nbt);
		switch (type) {
		case 1:
			return new NBTByte(getNBTByteData(InlineOps.checkcast(nbt, "{NBT_BYTE}")));
		case 2:
			return new NBTShort(getNBTShortData(InlineOps.checkcast(nbt, "{NBT_SHORT}")));
		case 3:
			return new NBTInt(getNBTIntData(InlineOps.checkcast(nbt, "{NBT_INT}")));
		case 4:
			return new NBTLong(getNBTLongData(InlineOps.checkcast(nbt, "{NBT_LONG}")));
		case 5:
			return new NBTFloat(getNBTFloatData(InlineOps.checkcast(nbt, "{NBT_FLOAT}")));
		case 6:
			return new NBTDouble(getNBTDoubleData(InlineOps.checkcast(nbt, "{NBT_DOUBLE}")));
		case 7:
			return new NBTByteArray(getNBTByteArrayData(InlineOps.checkcast(nbt, "{NBT_BYTE_ARRAY}")));
		case 8:
			return new NBTString(getNBTStringData(InlineOps.checkcast(nbt, "{NBT_STRING}")));
		case 9:
			NBTList list = new NBTList();
			for (Object nbtInList : getNBTListData(InlineOps.checkcast(nbt, "{NBT_LIST}"))) {
				list.add(translateFromMC(InlineOps.checkcast(nbtInList, "{NBT_BASE}")));
			}
			return list;
		case 10:
			NBTCompound compound = new NBTCompound();
			for (Map.Entry<String, ?> entry : getNBTCompoundData(InlineOps.checkcast(nbt, "{NBT_COMPOUND}"))
					.entrySet()) {
				compound.set(entry.getKey(), translateFromMC(InlineOps.checkcast(entry.getValue(), "{NBT_BASE}")));
			}
			return compound;
		case 11:
			return new NBTIntArray(getNBTIntArrayData(InlineOps.checkcast(nbt, "{NBT_INT_ARRAY}")));
		default:
			throw new RuntimeException("Unknown NBT type: " + type);
		}
	}

	@ContainsInlineBytecode
	@ChangeType("L{NBT_BASE};")
	private static Object translateToMC(NBTBase nbt) {
		if (nbt == null) {
			return null;
		}
		byte type = nbt.getType();
		Object mcNBT = createNewNBTByType(type);
		switch (type) {
		case 1:
			setNBTByteData(InlineOps.checkcast(mcNBT, "{NBT_BYTE}"), ((NBTByte) nbt).get());
			break;
		case 2:
			setNBTShortData(InlineOps.checkcast(mcNBT, "{NBT_SHORT}"), ((NBTShort) nbt).get());
			break;
		case 3:
			setNBTIntData(InlineOps.checkcast(mcNBT, "{NBT_INT}"), ((NBTInt) nbt).get());
			break;
		case 4:
			setNBTLongData(InlineOps.checkcast(mcNBT, "{NBT_LONG}"), ((NBTLong) nbt).get());
			break;
		case 5:
			setNBTFloatData(InlineOps.checkcast(mcNBT, "{NBT_FLOAT}"), ((NBTFloat) nbt).get());
			break;
		case 6:
			setNBTDoubleData(InlineOps.checkcast(mcNBT, "{NBT_DOUBLE}"), ((NBTDouble) nbt).get());
			break;
		case 7:
			setNBTByteArrayData(InlineOps.checkcast(mcNBT, "{NBT_BYTE_ARRAY}"), ((NBTByteArray) nbt).get());
			break;
		case 8:
			setNBTStringData(InlineOps.checkcast(mcNBT, "{NBT_STRING}"), ((NBTString) nbt).get());
			break;
		case 9:
			List<Object> list = Lists.newArrayList();
			for (NBTBase element : (NBTList) nbt) {
				list.add(translateToMC(element));
			}
			setNBTListData(InlineOps.checkcast(mcNBT, "{NBT_LIST}"), list);
			break;
		case 10:
			Map<String, Object> compound = Maps.newHashMap();
			for (Map.Entry<String, NBTBase> entry : (NBTCompound) nbt) {
				compound.put(entry.getKey(), translateToMC(entry.getValue()));
			}
			setNBTCompoundData(InlineOps.checkcast(mcNBT, "{NBT_COMPOUND}"), compound);
			break;
		case 11:
			setNBTIntArrayData(InlineOps.checkcast(mcNBT, "{NBT_INT_ARRAY}"), ((NBTIntArray) nbt).get());
			break;
		default:
			throw new RuntimeException("Unknown NBT type: " + type);
		}
		return mcNBT;
	}

}
