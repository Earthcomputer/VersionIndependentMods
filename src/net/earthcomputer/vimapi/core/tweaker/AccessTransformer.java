package net.earthcomputer.vimapi.core.tweaker;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * If the class is not an interface, this transformer will add access methods
 * for all fields (even public ones), and all methods which are not &lt;init&gt;
 * or &lt;clinit&gt;. Setter methods will not be added for final fields. The
 * names of these methods have the format access$getfield_[fieldId],
 * access$setfield_[fieldId] or access$method[methodId]. The IDs can be obtained
 * through the getMemberId method of this class. These access methods are then
 * used by {@link BytecodeTransformer} for
 * {@link net.earthcomputer.vimapi.core.InlineOps InlineOps}
 */
public class AccessTransformer implements IClassTransformer {

	private static final Map<Member, String> memberIds = Maps.newHashMap();
	private static int nextMemberId = 0;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_FRAMES);

		if ((node.access & Opcodes.ACC_INTERFACE) != 0) {
			return bytes;
		}

		List<MethodNode> methodsToAdd = Lists.newArrayList();

		for (FieldNode field : node.fields) {
			boolean isStatic = (field.access & Opcodes.ACC_STATIC) != 0;
			String memberId = getMemberId(node.name, field.name, field.desc);
			MethodNode methodToAdd = new MethodNode(
					Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | (isStatic ? Opcodes.ACC_STATIC : 0),
					"access$getfield_" + memberId, "()" + field.desc, null, null);
			if (!isStatic) {
				methodToAdd.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
			}
			methodToAdd.instructions.add(new FieldInsnNode(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, node.name,
					field.name, field.desc));
			methodToAdd.instructions.add(new InsnNode(Type.getType(field.desc).getOpcode(Opcodes.IRETURN)));
			methodsToAdd.add(methodToAdd);
			if ((field.access & Opcodes.ACC_FINAL) == 0) {
				methodToAdd = new MethodNode(
						Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | (isStatic ? Opcodes.ACC_STATIC : 0),
						"access$setfield_" + memberId, "(" + field.desc + ")V", null, null);
				if (!isStatic) {
					methodToAdd.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				}
				methodToAdd.instructions
						.add(new VarInsnNode(Type.getType(field.desc).getOpcode(Opcodes.ILOAD), isStatic ? 0 : 1));
				methodToAdd.instructions.add(new FieldInsnNode(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD,
						node.name, field.name, field.desc));
				methodToAdd.instructions.add(new InsnNode(Opcodes.RETURN));
				methodsToAdd.add(methodToAdd);
			}
		}

		for (MethodNode method : node.methods) {
			if (!method.name.startsWith("<")) {
				boolean isStatic = (method.access & Opcodes.ACC_STATIC) != 0;
				String memberId = getMemberId(node.name, method.name, method.desc);
				MethodNode methodToAdd = new MethodNode(
						Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC
								| (isStatic ? Opcodes.ACC_STATIC : 0),
						"access$method_" + memberId, method.desc, null, null);
				if (!isStatic) {
					methodToAdd.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
				}
				Type[] paramTypes = Type.getArgumentTypes(method.desc);
				int varIndex = isStatic ? 0 : 1;
				for (int i = 0; i < paramTypes.length; i++) {
					methodToAdd.instructions.add(new VarInsnNode(paramTypes[i].getOpcode(Opcodes.ILOAD), varIndex));
					varIndex += paramTypes[i].getSize();
				}
				methodToAdd.instructions.add(new MethodInsnNode(isStatic ? Opcodes.INVOKESTATIC
						: ((method.access & Opcodes.ACC_PRIVATE) != 0 ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL),
						node.name, method.name, method.desc, false));
				Type returnType = Type.getReturnType(method.desc);
				methodToAdd.instructions.add(new InsnNode(returnType.getOpcode(Opcodes.IRETURN)));
				methodsToAdd.add(methodToAdd);
			}
		}

		node.methods.addAll(methodsToAdd);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(writer);
		return writer.toByteArray();
	}

	public static String getMemberId(String className, String memberName, String memberDesc) {
		Member member = new Member(className, memberName, memberDesc);
		if (!memberIds.containsKey(member)) {
			String memberId = Integer.toHexString(nextMemberId++);
			memberIds.put(new Member(className, memberName, memberDesc), memberId);
			return memberId;
		} else {
			return memberIds.get(member);
		}
	}

	private static class Member {
		private String className;
		private String memberName;
		private String memberDesc;

		public Member(String className, String memberName, String memberDesc) {
			this.className = className;
			this.memberName = memberName;
			this.memberDesc = memberDesc;
		}

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			} else if (!(other instanceof Member)) {
				return false;
			} else {
				Member otherMember = (Member) other;
				return className.equals(otherMember.className) && memberName.equals(otherMember.memberName)
						&& memberDesc.equals(otherMember.memberDesc);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(className, memberName, memberDesc);
		}
	}

}
