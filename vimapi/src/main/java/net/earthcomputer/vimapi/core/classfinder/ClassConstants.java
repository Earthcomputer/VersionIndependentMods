package net.earthcomputer.vimapi.core.classfinder;

import java.util.Collections;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import com.google.common.collect.Sets;

/**
 * A data structure containing info from a class' constant pool
 */
public class ClassConstants {

	private Set<String> classes = Sets.newHashSet();
	private Set<CnstMemberRef> fields = Sets.newHashSet();
	private Set<CnstMemberRef> methods = Sets.newHashSet();
	private Set<CnstMemberRef> interfaceMethods = Sets.newHashSet();
	private Set<String> strings = Sets.newHashSet();
	private Set<Integer> integers = Sets.newHashSet();
	private Set<Float> floats = Sets.newHashSet();
	private Set<Long> longs = Sets.newHashSet();
	private Set<Double> doubles = Sets.newHashSet();
	private Set<CnstNameAndType> nameAndTypes = Sets.newHashSet();
	private Set<String> utf8s = Sets.newHashSet();
	private Set<CnstMethodHandle> methodHandles = Sets.newHashSet();
	private Set<String> methodTypes = Sets.newHashSet();

	private ClassConstants() {
	}

	/**
	 * Reads the constant pool from the bytecode
	 */
	public static ClassConstants readFromBytes(byte[] bytes) {
		if (readUnsignedShort(bytes, 6) > Opcodes.V1_8) {
			throw new IllegalArgumentException();
		}

		int constantCount = readUnsignedShort(bytes, 8) - 1;

		Object[] constants = new Object[constantCount];

		int index = 10;
		for (int i = 0; i < constantCount; i++) {
			int tag = readUnsignedByte(bytes, index);
			index++;
			switch (tag) {
			case 7:
			case 8:
			case 16:
				constants[i] = new RefConstant(tag, readUnsignedShort(bytes, index) - 1);
				index += 2;
				break;
			case 9:
			case 10:
			case 11:
			case 12:
				constants[i] = new RefConstant(tag, readUnsignedShort(bytes, index) - 1,
						readUnsignedShort(bytes, index + 2) - 1);
				index += 4;
				break;
			case 3:
				constants[i] = readInt(bytes, index);
				index += 4;
				break;
			case 4:
				constants[i] = Float.intBitsToFloat(readInt(bytes, index));
				index += 4;
				break;
			case 5:
				constants[i] = ((long) readInt(bytes, index) << 32) | readInt(bytes, index + 4);
				index += 8;
				i++;
				break;
			case 6:
				constants[i] = Double
						.longBitsToDouble(((long) readInt(bytes, index) << 32) | readInt(bytes, index + 4));
				index += 8;
				i++;
				break;
			case 1:
				int utfLen = readUnsignedShort(bytes, index);
				char[] buf = new char[utfLen];
				index += 2;
				int endIndex = index + utfLen;
				int strLen = 0;
				int c;
				int st = 0;
				char cc = 0;
				while (index < endIndex) {
					c = bytes[index++];
					switch (st) {
					case 0:
						c = c & 0xFF;
						if (c < 0x80) {
							buf[strLen++] = (char) c;
						} else if (c < 0xE0 && c > 0xBF) {
							cc = (char) (c & 0x1F);
							st = 1;
						} else {
							cc = (char) (c & 0x0F);
							st = 2;
						}
						break;

					case 1:
						buf[strLen++] = (char) ((cc << 6) | (c & 0x3F));
						st = 0;
						break;

					case 2:
						cc = (char) ((cc << 6) | (c & 0x3F));
						st = 1;
						break;
					}
				}
				constants[i] = new String(buf, 0, strLen);
				break;
			case 15:
				constants[i] = new RefConstant(tag, readUnsignedByte(bytes, index),
						readUnsignedShort(bytes, index + 1) - 1);
				index += 3;
				break;
			case 17:
				index += 4;
				break;
			default:
				throw new IllegalArgumentException("Encountered unknown constant type " + tag);
			}
		}

		ClassConstants classConstants = new ClassConstants();
		for (Object constant : constants) {
			if (constant instanceof Integer) {
				classConstants.integers.add((Integer) constant);
			} else if (constant instanceof Float) {
				classConstants.floats.add((Float) constant);
			} else if (constant instanceof Long) {
				classConstants.longs.add((Long) constant);
			} else if (constant instanceof Double) {
				classConstants.doubles.add((Double) constant);
			} else if (constant instanceof String) {
				classConstants.utf8s.add((String) constant);
			} else if (constant != null) {
				RefConstant ref = (RefConstant) constant;
				switch (ref.type) {
				case 7:
					classConstants.classes.add((String) constants[ref.field1]);
					break;
				case 9:
					RefConstant clazz = (RefConstant) constants[ref.field1];
					RefConstant nameAndType = (RefConstant) constants[ref.field2];
					classConstants.fields.add(new CnstMemberRef((String) constants[clazz.field1],
							(String) constants[nameAndType.field1], (String) constants[nameAndType.field2]));
					break;
				case 10:
					RefConstant clazz1 = (RefConstant) constants[ref.field1];
					RefConstant nameAndType1 = (RefConstant) constants[ref.field2];
					classConstants.methods.add(new CnstMemberRef((String) constants[clazz1.field1],
							(String) constants[nameAndType1.field1], (String) constants[nameAndType1.field2]));
					break;
				case 11:
					RefConstant clazz2 = (RefConstant) constants[ref.field1];
					RefConstant nameAndType2 = (RefConstant) constants[ref.field2];
					classConstants.interfaceMethods.add(new CnstMemberRef((String) constants[clazz2.field1],
							(String) constants[nameAndType2.field1], (String) constants[nameAndType2.field2]));
					break;
				case 8:
					classConstants.strings.add((String) constants[ref.field1]);
					break;
				case 12:
					classConstants.nameAndTypes
							.add(new CnstNameAndType((String) constants[ref.field1], (String) constants[ref.field2]));
					break;
				case 15:
					RefConstant memberRef = (RefConstant) constants[ref.field2];
					RefConstant nameAndType3 = (RefConstant) constants[memberRef.field2];
					classConstants.methodHandles
							.add(new CnstMethodHandle((byte) ((Integer) constants[ref.field1]).intValue(),
									new CnstMemberRef((String) constants[memberRef.field1],
											(String) constants[nameAndType3.field1],
											(String) constants[nameAndType3.field2])));
					break;
				case 16:
					classConstants.methodTypes.add((String) constants[ref.field1]);
					break;
				default:
					throw new AssertionError();
				}
			}
		}
		return classConstants;
	}

	private static int readUnsignedByte(byte[] bytes, int idx) {
		return bytes[idx] & 0xff;
	}

	private static int readUnsignedShort(byte[] bytes, int idx) {
		return (readUnsignedByte(bytes, idx) << 8) | readUnsignedByte(bytes, idx + 1);
	}

	private static int readInt(byte[] bytes, int idx) {
		return (readUnsignedByte(bytes, idx) << 24) | (readUnsignedByte(bytes, idx + 1) << 16)
				| (readUnsignedByte(bytes, idx + 2) << 8) | readUnsignedByte(bytes, idx + 3);
	}

	public Set<String> getClassRefs() {
		return Collections.unmodifiableSet(classes);
	}

	public Set<CnstMemberRef> getFieldRefs() {
		return Collections.unmodifiableSet(fields);
	}

	public Set<CnstMemberRef> getMethodRefs() {
		return Collections.unmodifiableSet(methods);
	}

	public Set<CnstMemberRef> getInterfaceMethodRefs() {
		return Collections.unmodifiableSet(interfaceMethods);
	}

	public Set<String> getStringRefs() {
		return Collections.unmodifiableSet(strings);
	}

	public Set<Integer> getIntRefs() {
		return Collections.unmodifiableSet(integers);
	}

	public Set<Float> getFloatRefs() {
		return Collections.unmodifiableSet(floats);
	}

	public Set<Long> getLongRefs() {
		return Collections.unmodifiableSet(longs);
	}

	public Set<Double> getDoubleRefs() {
		return Collections.unmodifiableSet(doubles);
	}

	public Set<CnstNameAndType> getNameAndTypeRefs() {
		return Collections.unmodifiableSet(nameAndTypes);
	}

	public Set<String> getUtf8Refs() {
		return Collections.unmodifiableSet(utf8s);
	}

	public Set<CnstMethodHandle> getMethodHandleRefs() {
		return Collections.unmodifiableSet(methodHandles);
	}

	public Set<String> getMethodTypeRefs() {
		return Collections.unmodifiableSet(methodTypes);
	}

	private static class RefConstant {
		private int type;
		private int field1;
		private int field2;

		private RefConstant(int type, int field) {
			this(type, field, 0);
		}

		private RefConstant(int type, int field1, int field2) {
			this.type = type;
			this.field1 = field1;
			this.field2 = field2;
		}
	}

	public static class CnstMemberRef {
		private CnstMemberRef(String className, String name, String type) {
			this.className = className;
			this.name = name;
			this.type = type;
		}

		public final String className;
		public final String name;
		public final String type;
	}

	public static class CnstNameAndType {
		private CnstNameAndType(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public final String name;
		public final String type;
	}

	public static class CnstMethodHandle {
		public CnstMethodHandle(byte referenceKind, CnstMemberRef reference) {
			this.referenceKind = referenceKind;
			this.reference = reference;
		}

		public final byte referenceKind;
		public final CnstMemberRef reference;
	}

	// No support for invokedynamic

}
