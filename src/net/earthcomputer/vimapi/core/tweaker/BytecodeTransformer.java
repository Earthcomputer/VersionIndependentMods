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
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.DetailClassVisitor;
import net.earthcomputer.vimapi.core.DetailClassVisitor.ClassVisitFailedException;
import net.earthcomputer.vimapi.core.InlineOps;
import net.earthcomputer.vimapi.core.classfinder.UsefulNames;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * This transformer translates code containing {@link Bytecode},
 * {@link InlineOps} and {@link ChangeType}.<br/>
 * <br/>
 * The main reason these things exist is so that VIM can directly reference
 * obfuscated vanilla code without the performance penalty of using reflection.
 * Coupled with the obfuscation format (see {@link #obfuscate(String)}), this
 * makes for a very powerful technique.
 */
public class BytecodeTransformer implements IClassTransformer {

	private static final String BYTECODE_TRANSFORMED_ANNOTATION_DESC = Type.getDescriptor(BytecodeMethod.class);
	private static final String CHANGE_TYPE_ANNOTATION_DESC = Type.getDescriptor(ChangeType.class);
	private static final String CONTAINS_INLINE_BYTECODE_DESC = Type.getDescriptor(ContainsInlineBytecode.class);
	private static final String BYTECODE_NAME = Type.getInternalName(Bytecode.class);
	private static final String INLINE_OPS_NAME = Type.getInternalName(InlineOps.class);
	private static final String METHOD_OP_NAME = Type.getInternalName(InlineOps.MethodOp.class);
	private static final String FIELD_OP_NAME = Type.getInternalName(InlineOps.FieldOp.class);

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

		// Changes in method descriptors
		Map<Pair<String, String>, String> methodTypeChanges = Maps.newHashMap();

		for (MethodNode method : node.methods) {
			// Does this method have the @BytecodeMethod annotation?
			boolean isBytecodeMethod = false;
			// Does this method have the @ContainsInlineBytecode annotation?
			boolean containsInlineBytecode = false;

			Type methodDesc = Type.getMethodType(method.desc);
			Type[] paramTypes = methodDesc.getArgumentTypes();
			Type returnType = methodDesc.getReturnType();
			// Whether we've found the @ChangeType annotation either on the
			// method or on its parameters
			boolean hasChangedType = false;

			// Look through the method's annotations
			if (method.visibleAnnotations != null) {
				for (AnnotationNode annotation : method.visibleAnnotations) {
					if (annotation.desc.equals(BYTECODE_TRANSFORMED_ANNOTATION_DESC)) {
						// found @BytecodeTransformed
						isBytecodeMethod = true;
						classChanged = true;
					} else if (annotation.desc.equals(CONTAINS_INLINE_BYTECODE_DESC)) {
						// found @ContainsInlineBytecode
						classChanged = true;
						containsInlineBytecode = true;
					} else if (annotation.desc.equals(CHANGE_TYPE_ANNOTATION_DESC)) {
						// found @ChangeType, change return type of method
						classChanged = true;
						hasChangedType = true;
						returnType = Type.getType(obfuscate((String) annotation.values.get(1)));
					}
				}
			}

			// The through the method's parameters' annotations
			if (method.visibleParameterAnnotations != null) {
				for (int i = 0; i < method.visibleParameterAnnotations.length; i++) {
					if (method.visibleParameterAnnotations[i] != null) {
						for (AnnotationNode annotation : method.visibleParameterAnnotations[i]) {
							if (annotation.desc.equals(CHANGE_TYPE_ANNOTATION_DESC)) {
								// found @ChangeType, change type of parameter
								classChanged = true;
								hasChangedType = true;
								paramTypes[i] = Type.getType(obfuscate((String) annotation.values.get(1)));
							}
						}
					}
				}
			}

			if (hasChangedType) {
				if ((method.access & Opcodes.ACC_PRIVATE) == 0) {
					throw new RuntimeException("Cannot have a @ChangeType annotation on a non-private method");
				}
				String newDesc = Type.getMethodDescriptor(returnType, paramTypes);
				methodTypeChanges.put(Pair.of(method.name, method.desc), newDesc);
				method.desc = newDesc;
			}
			if (isBytecodeMethod && containsInlineBytecode) {
				throw new RuntimeException(
						"Cannot have both the @BytecodeMethod and the @ContainsInlineBytecode annotations on the same method");
			}
			if (isBytecodeMethod) {
				handleBytecodeMethod(method);
			} else if (containsInlineBytecode) {
				handleInlineBytecode(method);
			}
		}

		if (classChanged) {
			// For each direct reference of a method with a @ChangeType
			// annotation, we have to update the reference so that it matches
			// the new method descriptor
			for (Map.Entry<Pair<String, String>, String> methodTypeChange : methodTypeChanges.entrySet()) {
				for (MethodNode method : node.methods) {
					if (method.instructions != null && method.instructions.size() != 0) {
						AbstractInsnNode insn = method.instructions.getFirst();
						do {
							if (insn.getType() == AbstractInsnNode.METHOD_INSN) {
								MethodInsnNode methodInsn = (MethodInsnNode) insn;
								if (methodInsn.owner.equals(node.name) && Pair.of(methodInsn.name, methodInsn.desc)
										.equals(methodTypeChange.getKey())) {
									methodInsn.desc = methodTypeChange.getValue();
								}
							}
							insn = insn.getNext();
						} while (insn != null);
					}
				}
			}

			// Check that the altered class is valid, then convert it to
			// bytecode
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
					VIM.LOGGER.info("CURRENT MEMBER: " + e.getCurrentMemberName() + " " + e.getCurrentMemberDesc());
				}
				if (e.getLineNumber() != -1) {
					VIM.LOGGER.info("LINE NUMBER: " + e.getLineNumber());
				}
				throw e;
			}
		} else { // if (!classChanged)
			return bytes;
		}
	}

	/**
	 * Handle a method with a @BytecodeMethod annotation
	 */
	private static void handleBytecodeMethod(MethodNode method) {
		int lineNumber = -1;
		InsnList newInsns = new InsnList();
		AbstractInsnNode insn = method.instructions.getFirst();
		// The keys of this map may either be a LabelNode or a String
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
		AbstractInsnNode argInsn = lastRealInsn(bytecodeInsn);
		if (type.equals("insn")) {
			return new InsnNode(getIntFromInsn(argInsn));
		} else if (type.equals("field")) {
			int opcode;
			String owner;
			String field;
			String desc;
			desc = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			field = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			owner = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			opcode = getIntFromInsn(argInsn);
			return new FieldInsnNode(opcode, obfuscate(owner), obfuscate(field), obfuscate(desc));
		} else if (type.equals("iinc")) {
			int var;
			int amt;
			amt = getIntFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			var = getIntFromInsn(argInsn);
			return new IincInsnNode(var, amt);
		} else if (type.equals("intInsn")) {
			int opcode;
			int operand;
			operand = getIntFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			opcode = getIntFromInsn(argInsn);
			return new IntInsnNode(opcode, operand);
		} else if (type.equals("jump")) {
			int opcode;
			String labelName;
			labelName = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			opcode = getIntFromInsn(argInsn);
			LabelNode label = labels.get(labelName);
			if (label == null) {
				label = new LabelNode();
				labels.put(labelName, label);
			}
			return new JumpInsnNode(opcode, label);
		} else if (type.equals("label")) {
			String labelName = getStringFromInsn(argInsn);
			LabelNode label = labels.get(labelName);
			if (label == null) {
				label = new LabelNode();
				labels.put(labelName, label);
			}
			return label;
		} else if (type.equals("ldc")) {
			// TODO: support for obfuscated names?
			return new LdcInsnNode(((LdcInsnNode) argInsn).cst);
		} else if (type.equals("method")) {
			int opcode;
			String owner;
			String method;
			String desc;
			boolean itf;
			itf = getIntFromInsn(argInsn) != 0;
			argInsn = lastRealInsn(argInsn);
			desc = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			method = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			owner = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			opcode = getIntFromInsn(argInsn);
			return new MethodInsnNode(opcode, obfuscate(owner), obfuscate(method), obfuscate(desc), itf);
		} else if (type.equals("multianewarray")) {
			String desc;
			int dims;
			dims = getIntFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			desc = getStringFromInsn(argInsn);
			return new MultiANewArrayInsnNode(obfuscate(desc), dims);
		} else if (type.equals("type")) {
			int opcode;
			String cst;
			cst = getStringFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			opcode = getIntFromInsn(argInsn);
			return new TypeInsnNode(opcode, obfuscate(cst));
		} else if (type.equals("var")) {
			int opcode;
			int var;
			var = getIntFromInsn(argInsn);
			argInsn = lastRealInsn(argInsn);
			opcode = getIntFromInsn(argInsn);
			return new VarInsnNode(opcode, var);
		} else {
			throw new RuntimeException("Unknown bytecode type " + type);
		}
	}

	/**
	 * Handle a method with the @ContainsInlineBytecode annotation
	 */
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
						String cst = getInternalNameFromInsn(argInsn);
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
					} else if (op.equals("field")) {
						String fieldName = getStringFromInsn(argInsn);
						fieldName = obfuscate(fieldName);
						argInsn = lastRealInsn(argInsn);
						String owner = getInternalNameFromInsn(argInsn);
						owner = obfuscate(owner);
						argInsn = lastRealInsn(argInsn);
						int opcode = getIntFromInsn(argInsn);
						handleFieldOp(method.instructions, insn, opcode, owner, fieldName);
					}

					// Repeat this, as the next instruction may have been
					// removed or changed
					nextInsn = insn.getNext();

					// Remove the InlineOps static call and all the argument
					// instructions
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
				if (currentInsn.getOpcode() == Opcodes.INVOKESTATIC) {
					if (currentMethodInsn.owner.equals(INLINE_OPS_NAME) && currentMethodInsn.name.equals("method")) {
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
							} else if (currentMethodInsn.name.startsWith("arg")) {
								instructions.remove(currentInsn);
							} else if (currentMethodInsn.name.startsWith("invoke")) {
								Type[] paramTypes = new Type[parameters.size()];
								for (int i = 0; i < paramTypes.length; i++) {
									paramTypes[i] = Type.getType(parameters.get(i));
								}
								String invokeType = currentMethodInsn.name.substring(6);
								Type returnType = getTypeFromSuffix(invokeType, returnTypeName);
								boolean itfMethod = opcode == Opcodes.INVOKEINTERFACE;
								String methodDesc = Type.getMethodDescriptor(returnType, paramTypes);
								String transformedMethodName = itfMethod ? methodName
										: "access$method_"
												+ AccessTransformer.getMemberId(owner, methodName, methodDesc);
								instructions.insert(currentInsn, new MethodInsnNode(opcode, owner,
										transformedMethodName, methodDesc, itfMethod));
								instructions.remove(currentInsn);
								return;
							}
						}
					}
				}
			}
			currentInsn = nextInsn;
		}
		throw new RuntimeException("No invoke instruction reached");
	}

	private static void handleFieldOp(InsnList instructions, AbstractInsnNode methodInsn, int opcode, String owner,
			String fieldName) {
		String typeName = null;
		int amountNested = 0;

		AbstractInsnNode currentInsn = methodInsn.getNext(), nextInsn, argInsn;
		while (currentInsn != null) {
			nextInsn = currentInsn.getNext();

			if (currentInsn.getType() == AbstractInsnNode.METHOD_INSN) {
				MethodInsnNode currentMethodInsn = (MethodInsnNode) currentInsn;
				if (currentInsn.getOpcode() == Opcodes.INVOKESTATIC) {
					if (currentMethodInsn.owner.equals(INLINE_OPS_NAME) && currentMethodInsn.name.equals("field")) {
						amountNested++;
					}
				} else if (currentInsn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
					if (currentMethodInsn.owner.equals(FIELD_OP_NAME)) {
						if (amountNested > 0) {
							if (currentMethodInsn.name.startsWith("get") || currentMethodInsn.name.startsWith("set")) {
								amountNested--;
							}
						} else {
							if (currentMethodInsn.name.equals("type")) {
								argInsn = lastRealInsn(currentInsn);
								typeName = getDescriptorFromInsn(argInsn);
								instructions.remove(argInsn);
								instructions.remove(currentInsn);
							} else if (currentMethodInsn.name.equals("instance")) {
								instructions.remove(currentInsn);
							} else if (currentMethodInsn.name.startsWith("get")) {
								String getType = currentMethodInsn.name.substring(3);
								Type fieldType = getTypeFromSuffix(getType, typeName);
								int methodOpcode = opcode == Opcodes.GETSTATIC ? Opcodes.INVOKESTATIC
										: Opcodes.INVOKEVIRTUAL;
								String fieldDesc = fieldType.getDescriptor();
								String methodName = "access$getfield_"
										+ AccessTransformer.getMemberId(owner, fieldName, fieldDesc);
								instructions.insert(currentInsn,
										new MethodInsnNode(methodOpcode, owner, methodName, "()" + fieldDesc, false));
								instructions.remove(currentInsn);
								return;
							} else if (currentMethodInsn.name.startsWith("set")) {
								String setType = currentMethodInsn.name.substring(3);
								Type fieldType = getTypeFromSuffix(setType, typeName);
								int methodOpcode = opcode == Opcodes.PUTSTATIC ? Opcodes.INVOKESTATIC
										: Opcodes.INVOKEVIRTUAL;
								String fieldDesc = fieldType.getDescriptor();
								String methodName = "access$setfield_"
										+ AccessTransformer.getMemberId(owner, fieldName, fieldDesc);
								instructions.insert(currentInsn, new MethodInsnNode(methodOpcode, owner, methodName,
										"(" + fieldDesc + ")V", false));
								instructions.remove(currentInsn);
								return;
							}
						}
					}
				}
			}
			currentInsn = nextInsn;
		}
		throw new RuntimeException("No get or set instruction reached");
	}

	/**
	 * Gets the previous 'real' instruction (instruction with a positive opcode)
	 * from the given instruction
	 */
	private static AbstractInsnNode lastRealInsn(AbstractInsnNode insn) {
		do {
			insn = insn.getPrevious();
		} while (insn.getOpcode() == -1);
		return insn;
	}

	/**
	 * Returns the integer that the given instruction would load onto the stack
	 */
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

	/**
	 * Returns the internal name from the object that the given instruction
	 * would load onto the stack, obfuscated if necessary
	 */
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

	/**
	 * Returns the internal name from the object that the given instruction
	 * would load onto the stack, obfuscated if necessary
	 */
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

	/**
	 * Returns the string that the given instruction would load onto the stack
	 */
	private static String getStringFromInsn(AbstractInsnNode insn) {
		if (insn.getType() == AbstractInsnNode.LDC_INSN) {
			LdcInsnNode ldcInsn = (LdcInsnNode) insn;
			return (String) ldcInsn.cst;
		} else {
			throw new RuntimeException("Cannot get a string from the instruction " + Printer.OPCODES[insn.getOpcode()]);
		}
	}

	/**
	 * Returns the type from the suffix of commonly used methods, such as
	 * {@link InlineOps.MethodOp#argDouble(double)} or
	 * {@link InlineOps.FieldOp#getLong()}
	 */
	private static Type getTypeFromSuffix(String suffix, String objectTypeName) {
		if (suffix.equals("Boolean")) {
			return Type.BOOLEAN_TYPE;
		} else if (suffix.equals("Byte")) {
			return Type.BYTE_TYPE;
		} else if (suffix.equals("Char")) {
			return Type.CHAR_TYPE;
		} else if (suffix.equals("Double")) {
			return Type.DOUBLE_TYPE;
		} else if (suffix.equals("Float")) {
			return Type.FLOAT_TYPE;
		} else if (suffix.equals("Int")) {
			return Type.INT_TYPE;
		} else if (suffix.equals("Long")) {
			return Type.LONG_TYPE;
		} else if (suffix.equals("Short")) {
			return Type.SHORT_TYPE;
		} else if (suffix.equals("Void")) {
			return Type.VOID_TYPE;
		} else {
			return Type.getType(objectTypeName);
		}
	}

	/**
	 * This method reads an internal name, member name or descriptor in the
	 * obfuscated format and converts it to an unobfuscated format.<br/>
	 * <br/>
	 * The obfuscated format is exactly the same as an internal name, member
	 * name or descriptor except that {} braces are used with names registered
	 * through {@link net.earthcomputer.vimapi.core.classfinder.UsefulNames
	 * UsefulNames} for obfuscated Minecraft names . For example, if you want
	 * the internal name of Item, you use <code>{vim:Item}</code>, and if you
	 * want a method descriptor with <code>ItemStack</code>, <code>String</code>
	 * and <code>int</code> parameters and returning a
	 * <code>RegistryNamespaced</code>, you would use
	 * <code>(L{vim:ItemStack};Ljava/lang/String;I)L{vim:RegistryNamespaced};</code>
	 */
	private static String obfuscate(String unobfed) {
		StringBuilder newString = new StringBuilder();
		Matcher matcher = OBF_PATTERN.matcher(unobfed);
		int end = 0;
		while (matcher.find()) {
			newString.append(matcher.group(1));
			newString.append(UsefulNames.get(matcher.group(2)));
			end = matcher.end();
		}
		if (end < unobfed.length()) {
			newString.append(unobfed.substring(end));
		}
		return newString.toString();
	}

}
