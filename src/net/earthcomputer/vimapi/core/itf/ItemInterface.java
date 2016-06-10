package net.earthcomputer.vimapi.core.itf;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.Bytecode;
import net.earthcomputer.vimapi.core.BytecodeMethod;
import net.earthcomputer.vimapi.core.ChangeType;

public class ItemInterface {

	private ItemInterface() {
	}

	@BytecodeMethod
	@ChangeType("L{ITEM};")
	private static Object getItemByName(String name) {
		Bytecode.var(Opcodes.ALOAD, 0);
		Bytecode.method(Opcodes.INVOKESTATIC, "{ITEM}", "{ITEM_GETBYNAME}", "(Ljava/lang/String;)L{ITEM};", false);
		Bytecode.insn(Opcodes.ARETURN);
		return null;
	}

	@BytecodeMethod
	private static String getNameFromItem(@ChangeType("L{ITEM};") Object item) {
		Bytecode.field(Opcodes.GETSTATIC, "{ITEM}", "{ITEM_ITEMREGISTRY}", "L{REGISTRY_NAMESPACED};");
		Bytecode.field(Opcodes.GETFIELD, "{REGISTRY_NAMESPACED}", "{REGISTRY_NAMESPACED_INVERSEOBJECTREGISTRY}",
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
