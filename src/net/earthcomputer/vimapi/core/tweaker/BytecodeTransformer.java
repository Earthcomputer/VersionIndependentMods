package net.earthcomputer.vimapi.core.tweaker;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.earthcomputer.vimapi.VIM;
import net.earthcomputer.vimapi.core.Bytecode;
import net.earthcomputer.vimapi.core.BytecodeMethod;
import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ClassChecker;
import net.earthcomputer.vimapi.core.ClassFinder;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.DetailClassVisitor;
import net.earthcomputer.vimapi.core.DetailClassVisitor.ClassVisitFailedException;
import net.earthcomputer.vimapi.core.InlineOps;
import net.earthcomputer.vimapi.core.UsefulNames;
import net.minecraft.launchwrapper.IClassTransformer;

public class BytecodeTransformer implements IClassTransformer {

	private static final String BYTECODE_TRANSFORMED_ANNOTATION_DESC = Type.getDescriptor(BytecodeMethod.class);
	private static final String CHANGE_TYPE_ANNOTATION_DESC = Type.getDescriptor(ChangeType.class);
	private static final String CONTAINS_INLINE_BYTECODE_DESC = Type.getDescriptor(ContainsInlineBytecode.class);
	private static final String BYTECODE_NAME = Type.getInternalName(Bytecode.class);
	private static final String INLINE_OPS_NAME = Type.getInternalName(InlineOps.class);
	private static final String METHOD_OP_NAME = Type.getInternalName(InlineOps.MethodOp.class);

	private static final Pattern OBF_PATTERN = Pattern.compile("([^\\Q{\\E]*)\\Q{\\E([^\\Q}\\E]+)\\Q}\\E");

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_FRAMES);

		boolean classChanged = false;

		Map<Pair<String, String>, String> methodDescChanges = Maps.newHashMap();

		for (MethodNode method : node.methods) {
			boolean bytecodeTransformed = false;
			Type methodDesc = Type.getMethodType(method.desc);
			Type[] paramTypes = methodDesc.getArgumentTypes();
			Type returnType = methodDesc.getReturnType();
			boolean methodDescChanged = false;
			boolean containsInlineBytecode = false;
			if (method.visibleAnnotations != null) {
				for (AnnotationNode annotation : method.visibleAnnotations) {
					if (annotation.desc.equals(BYTECODE_TRANSFORMED_ANNOTATION_DESC)) {
						bytecodeTransformed = true;
						classChanged = true;
					} else if (annotation.desc.equals(CHANGE_TYPE_ANNOTATION_DESC)) {
						classChanged = true;
						methodDescChanged = true;
						returnType = Type.getType(obfuscate((String) annotation.values.get(1)));
					} else if (annotation.desc.equals(CONTAINS_INLINE_BYTECODE_DESC)) {
						classChanged = true;
						containsInlineBytecode = true;
					}
				}
			}
			if (method.visibleParameterAnnotations != null) {
				for (int i = 0; i < method.visibleParameterAnnotations.length; i++) {
					if (method.visibleParameterAnnotations[i] != null) {
						for (AnnotationNode annotation : method.visibleParameterAnnotations[i]) {
							if (annotation.desc.equals(CHANGE_TYPE_ANNOTATION_DESC)) {
								classChanged = true;
								methodDescChanged = true;
								paramTypes[i] = Type.getType(obfuscate((String) annotation.values.get(1)));
							}
						}
					}
				}
			}
			if (methodDescChanged) {
				if ((method.access & Opcodes.ACC_PRIVATE) == 0) {
					throw new RuntimeException("Cannot have a @ChangeType annotation on a non-private method");
				}
				String newDesc = Type.getMethodDescriptor(returnType, paramTypes);
				methodDescChanges.put(Pair.of(method.name, method.desc), newDesc);
				method.desc = newDesc;
			}
			if (bytecodeTransformed && containsInlineBytecode) {
				throw new RuntimeException(
						"Cannot have both the @BytecodeMethod and the @ContainsInlineBytecode annotations on the same method");
			}
			if (bytecodeTransformed) {
				bytecodeTransform(method);
			}
			if (containsInlineBytecode) {
				handleInlineBytecode(method);
			}
		}

		if (classChanged) {
			for (Map.Entry<Pair<String, String>, String> methodDescChange : methodDescChanges.entrySet()) {
				for (MethodNode method : node.methods) {
					if (method.instructions != null && method.instructions.size() != 0) {
						AbstractInsnNode insn = method.instructions.getFirst();
						do {
							if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
								MethodInsnNode methodInsn = (MethodInsnNode) insn;
								if (methodInsn.owner.equals(node.name) && Pair.of(methodInsn.name, methodInsn.desc)
										.equals(methodDescChange.getKey())) {
									methodInsn.desc = methodDescChange.getValue();
								}
							}
							insn = insn.getNext();
						} while (insn != null);
					}
				}
			}

			try {
				ClassChecker classChecker = new ClassChecker();
				node.accept(classChecker);
				ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				node.accept(new DetailClassVisitor(writer));
				return writer.toByteArray();
			} catch (ClassVisitFailedException e) {
				VIM.LOGGER.info("--------------------------");
				VIM.LOGGER.info("FOUND SOMETHING WRONG WITH YOUR BYTECODE!");
				if (e.getCurrentMemberName() != null) {
					VIM.LOGGER
							.info("CURRENT MEMBER: " + e.getCurrentMemberName() + " " + e.getCurrentMemberDesc());
				}
				if (e.getLineNumber() != -1) {
					VIM.LOGGER.info("LINE NUMBER: " + e.getLineNumber());
				}
				throw e;
			}
		} else {
			return bytes;
		}
	}

	private static void bytecodeTransform(MethodNode method) {
		int lineNumber = -1;
		InsnList newInsns = new InsnList();
		AbstractInsnNode insn = method.instructions.getFirst();
		Map<Object, LabelNode> labels = Maps.newHashMap();
		while (insn != null) {
			if (insn instanceof LineNumberNode) {
				LineNumberNode lineNode = (LineNumberNode) insn;
				LabelNode label = labels.get(lineNode.start);
				if (label == null) {
					label = new LabelNode();
					labels.put(lineNode.start, label);
				}
				newInsns.add(new LineNumberNode(lineNode.line, label));
				lineNumber = lineNode.line;
			} else if (insn instanceof LabelNode) {
				LabelNode label = labels.get(insn);
				if (label == null) {
					label = new LabelNode();
					labels.put(insn, label);
				}
				newInsns.add(label);
			} else if (insn.getOpcode() == Opcodes.INVOKESTATIC) {
				MethodInsnNode methodInsn = (MethodInsnNode) insn;
				if (methodInsn.owner.equals(BYTECODE_NAME)) {
					try {
						newInsns.add(getBytecodeInsn(methodInsn, labels));
					} catch (Exception e) {
						VIM.LOGGER.error("ERROR HAPPENED AT LINE " + lineNumber);
						throw e;
					}
				}
			}
			insn = insn.getNext();
		}

		method.instructions = newInsns;
	}

	private static AbstractInsnNode getBytecodeInsn(MethodInsnNode bytecodeInsn, Map<Object, LabelNode> labels) {
		String type = bytecodeInsn.name;
		AbstractInsnNode insn = lastRealInsn(bytecodeInsn);
		if (type.equals("insn")) {
			return new InsnNode(getIntFromInsn(insn));
		} else if (type.equals("field")) {
			int opcode;
			String owner;
			String field;
			String desc;
			desc = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			field = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			owner = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			opcode = getIntFromInsn(insn);
			return new FieldInsnNode(opcode, obfuscate(owner), obfuscate(field), obfuscate(desc));
		} else if (type.equals("iinc")) {
			int var;
			int amt;
			amt = getIntFromInsn(insn);
			insn = lastRealInsn(insn);
			var = getIntFromInsn(insn);
			return new IincInsnNode(var, amt);
		} else if (type.equals("intInsn")) {
			int opcode;
			int operand;
			operand = getIntFromInsn(insn);
			insn = lastRealInsn(insn);
			opcode = getIntFromInsn(insn);
			return new IntInsnNode(opcode, operand);
		} else if (type.equals("jump")) {
			int opcode;
			String labelName;
			labelName = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			opcode = getIntFromInsn(insn);
			LabelNode label = labels.get(labelName);
			if (label == null) {
				label = new LabelNode();
				labels.put(labelName, label);
			}
			return new JumpInsnNode(opcode, label);
		} else if (type.equals("label")) {
			String labelName = getStringFromInsn(insn);
			LabelNode label = labels.get(labelName);
			if (label == null) {
				label = new LabelNode();
				labels.put(labelName, label);
			}
			return label;
		} else if (type.equals("ldc")) {
			// TODO: support for obfuscated names?
			return new LdcInsnNode(((LdcInsnNode) insn).cst);
		} else if (type.equals("method")) {
			int opcode;
			String owner;
			String method;
			String desc;
			boolean itf;
			itf = getIntFromInsn(insn) != 0;
			insn = lastRealInsn(insn);
			desc = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			method = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			owner = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			opcode = getIntFromInsn(insn);
			return new MethodInsnNode(opcode, obfuscate(owner), obfuscate(method), obfuscate(desc), itf);
		} else if (type.equals("multianewarray")) {
			String desc;
			int dims;
			dims = getIntFromInsn(insn);
			insn = lastRealInsn(insn);
			desc = getStringFromInsn(insn);
			return new MultiANewArrayInsnNode(obfuscate(desc), dims);
		} else if (type.equals("type")) {
			int opcode;
			String cst;
			cst = getStringFromInsn(insn);
			insn = lastRealInsn(insn);
			opcode = getIntFromInsn(insn);
			return new TypeInsnNode(opcode, obfuscate(cst));
		} else if (type.equals("var")) {
			int opcode;
			int var;
			var = getIntFromInsn(insn);
			insn = lastRealInsn(insn);
			opcode = getIntFromInsn(insn);
			return new VarInsnNode(opcode, var);
		} else {
			throw new RuntimeException("Unknown bytecode type " + type);
		}
	}

	private static void handleInlineBytecode(MethodNode method) {
		AbstractInsnNode insn = method.instructions.getFirst();
		while (insn != null) {
			AbstractInsnNode nextInsn = insn.getNext();
			if (insn.getOpcode() == Opcodes.INVOKESTATIC) {
				MethodInsnNode methodInsn = (MethodInsnNode) insn;
				if (methodInsn.owner.equals(INLINE_OPS_NAME)) {
					AbstractInsnNode argInsn = lastRealInsn(insn);
					String op = methodInsn.name;
					if (op.equals("checkcast")) {
						String cst = getStringFromInsn(argInsn);
						method.instructions.insert(insn, new TypeInsnNode(Opcodes.CHECKCAST, obfuscate(cst)));
					} else if (op.equals("method")) {
						String methodName = getStringFromInsn(argInsn);
						methodName = obfuscate(methodName);
						argInsn = lastRealInsn(argInsn);
						String owner = getInternalNameFromInsn(argInsn);
						owner = obfuscate(owner);
						argInsn = lastRealInsn(argInsn);
						int opcode = getIntFromInsn(argInsn);
						handleMethodOp(method.instructions, insn, opcode, owner, methodName);
					}
					// Repeat this, as the next instruction may have been
					// removed or changed
					nextInsn = insn.getNext();
					AbstractInsnNode insnToRemove = argInsn, nextInsnToRemove;
					do {
						nextInsnToRemove = insnToRemove.getNext();
						method.instructions.remove(insnToRemove);
						insnToRemove = nextInsnToRemove;
					} while (insnToRemove != nextInsn);
				}
			}
			insn = nextInsn;
		}
	}

	private static void handleMethodOp(InsnList instructions, AbstractInsnNode methodInsn, int opcode, String owner,
			String methodName) {
		String returnTypeName = null;
		List<String> parameters = Lists.newArrayList();
		int amountNested = 0;

		AbstractInsnNode currentInsn = methodInsn.getNext(), nextInsn, argInsn;
		while (currentInsn != null) {
			nextInsn = currentInsn.getNext();
			if (currentInsn.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode currentMethodInsn = (MethodInsnNode) currentInsn;
				if (currentMethodInsn.owner.equals(METHOD_OP_NAME)) {
					if (currentInsn.getOpcode() == Opcodes.INVOKESTATIC) {
						if (currentMethodInsn.owner.equals(INLINE_OPS_NAME)
								&& currentMethodInsn.name.equals("method")) {
							amountNested++;
						}
					} else if (currentInsn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						if (currentMethodInsn.owner.equals(METHOD_OP_NAME)) {
							if (amountNested > 0) {
								if (currentMethodInsn.name.startsWith("invoke")) {
									amountNested--;
								}
							} else {
								if (currentMethodInsn.name.equals("returnType")) {
									argInsn = lastRealInsn(currentInsn);
									returnTypeName = getDescriptorFromInsn(argInsn);
									instructions.remove(argInsn);
									instructions.remove(currentInsn);
								} else if (currentMethodInsn.name.equals("param")) {
									argInsn = lastRealInsn(currentInsn);
									parameters.add(getDescriptorFromInsn(argInsn));
									instructions.remove(argInsn);
									instructions.remove(currentInsn);
								} else if (currentMethodInsn.name.equals("arg")) {
									instructions.remove(currentInsn);
								} else if (currentMethodInsn.name.startsWith("invoke")) {
									Type[] paramTypes = new Type[parameters.size()];
									for (int i = 0; i < paramTypes.length; i++) {
										paramTypes[i] = Type.getType(parameters.get(i));
									}
									String invokeType = currentMethodInsn.name.substring(6);
									Type returnType;
									if (invokeType.equals("Boolean")) {
										returnType = Type.BOOLEAN_TYPE;
									} else if (invokeType.equals("Byte")) {
										returnType = Type.BYTE_TYPE;
									} else if (invokeType.equals("Char")) {
										returnType = Type.CHAR_TYPE;
									} else if (invokeType.equals("Double")) {
										returnType = Type.DOUBLE_TYPE;
									} else if (invokeType.equals("Float")) {
										returnType = Type.FLOAT_TYPE;
									} else if (invokeType.equals("Int")) {
										returnType = Type.INT_TYPE;
									} else if (invokeType.equals("Long")) {
										returnType = Type.LONG_TYPE;
									} else if (invokeType.equals("Short")) {
										returnType = Type.SHORT_TYPE;
									} else if (invokeType.equals("Void")) {
										returnType = Type.VOID_TYPE;
									} else {
										returnType = Type.getType(returnTypeName);
									}
									instructions.insert(currentInsn,
											new MethodInsnNode(opcode, owner, methodName,
													Type.getMethodDescriptor(returnType, paramTypes),
													opcode == Opcodes.INVOKEINTERFACE));
									instructions.remove(currentInsn);
									return;
								}
							}
						}
					}
				}
			}
			currentInsn = nextInsn;
		}
		throw new RuntimeException("No invoke instruction reached");
	}

	private static AbstractInsnNode lastRealInsn(AbstractInsnNode insn) {
		do {
			insn = insn.getPrevious();
		} while (insn.getOpcode() == -1);
		return insn;
	}

	private static int getIntFromInsn(AbstractInsnNode insn) {
		if (insn.getType() == AbstractInsnNode.INSN) {
			return insn.getOpcode() - Opcodes.ICONST_0;
		} else if (insn.getType() == AbstractInsnNode.INT_INSN) {
			IntInsnNode intInsn = (IntInsnNode) insn;
			return intInsn.operand;
		} else if (insn.getType() == AbstractInsnNode.LDC_INSN) {
			LdcInsnNode ldcInsn = (LdcInsnNode) insn;
			return (Integer) ldcInsn.cst;
		} else {
			throw new RuntimeException(
					"Cannot get an integer from the instruction " + Printer.OPCODES[insn.getOpcode()]);
		}
	}

	private static String getInternalNameFromInsn(AbstractInsnNode insn) {
		if (insn.getType() == AbstractInsnNode.LDC_INSN) {
			Object cst = ((LdcInsnNode) insn).cst;
			if (cst instanceof String) {
				return obfuscate((String) cst);
			} else if (cst instanceof Type) {
				return ((Type) cst).getInternalName();
			} else {
				throw new RuntimeException("Wrong type of ldc instruction");
			}
		} else {
			throw new RuntimeException(
					"Cannot get an internal name from the instruction " + Printer.OPCODES[insn.getOpcode()]);
		}
	}

	private static String getDescriptorFromInsn(AbstractInsnNode insn) {
		if (insn.getType() == AbstractInsnNode.LDC_INSN) {
			Object cst = ((LdcInsnNode) insn).cst;
			if (cst instanceof String) {
				return obfuscate((String) cst);
			} else if (cst instanceof Type) {
				return ((Type) cst).getDescriptor();
			} else {
				throw new RuntimeException("Wrong type of ldc instruction");
			}
		} else if (insn.getOpcode() == Opcodes.GETSTATIC) {
			FieldInsnNode field = (FieldInsnNode) insn;
			if (field.name.equals("TYPE") && field.desc.equals("Ljava/lang/Class;")) {
				String clazz = field.owner;
				if (clazz.equals("java/lang/Boolean")) {
					return "Z";
				} else if (clazz.equals("java/lang/Byte")) {
					return "B";
				} else if (clazz.equals("java/lang/Character")) {
					return "C";
				} else if (clazz.equals("java/lang/Double")) {
					return "D";
				} else if (clazz.equals("java/lang/Float")) {
					return "F";
				} else if (clazz.equals("java/lang/Integer")) {
					return "I";
				} else if (clazz.equals("java/lang/Long")) {
					return "J";
				} else if (clazz.equals("java/lang/Short")) {
					return "S";
				}
			}
		}
		throw new RuntimeException("Cannot get a descriptor from the instruction " + Printer.OPCODES[insn.getOpcode()]);
	}

	private static String getStringFromInsn(AbstractInsnNode insn) {
		if (insn.getType() == AbstractInsnNode.LDC_INSN) {
			LdcInsnNode ldcInsn = (LdcInsnNode) insn;
			return (String) ldcInsn.cst;
		} else {
			throw new RuntimeException("Cannot get a string from the instruction " + Printer.OPCODES[insn.getOpcode()]);
		}
	}

	private static String obfuscate(String unobfed) {
		StringBuilder newString = new StringBuilder();
		Matcher matcher = OBF_PATTERN.matcher(unobfed);
		int end = 0;
		while (matcher.find()) {
			newString.append(matcher.group(1));
			newString.append(ClassFinder.getObfedName(UsefulNames.valueOf(matcher.group(2))));
			end = matcher.end();
		}
		if (end < unobfed.length()) {
			newString.append(unobfed.substring(end));
		}
		return newString.toString();
	}

}
