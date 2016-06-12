package net.earthcomputer.vimapi.core.itf;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.Bytecode;
import net.earthcomputer.vimapi.core.BytecodeMethod;
import net.earthcomputer.vimapi.core.ChangeType;

public class ItemInterface {

	private ItemInterface() {
	}

	@BytecodeMethod
	@ChangeType("L{vim:Item};")
	private static Object getItemByName(String name) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.method(Opcodes.INVOKESTATIC, "{vim:Item}", "{vim:Item.getByName}", "(Ljava/lang/String;)L{vim:Item};", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static String getNameFromItem(@ChangeType("L{vim:Item};") Object item) {
		Bytecode.field(Opcodes.GETSTATIC, "{vim:Item}", "{vim:Item.itemRegistry}", "L{vim:RegistryNamespaced};");
		Bytecode.field(Opcodes.GETFIELD, "{vim:RegistryNamespaced}", "{vim:RegistryNamespaced.inverseObjectRegistry}",
				"Ljava/util/Map;");
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.method(Opcodes.INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;",
				true);
		Bytecode.var(Opcodes.ASTORE, 1);
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.jump(Opcodes.IFNONNULL, "nameNotNull");
		Bytecode.insn(Opcodes.ACONST_NULL);
		Bytecode.insn(Opcodes.ARETURN);
		Bytecode.label("nameNotNull");
		Bytecode.var(Opcodes.ALOAD, 1);
		Bytecode.method(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

}
