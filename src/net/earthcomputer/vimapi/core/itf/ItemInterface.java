package net.earthcomputer.vimapi.core.itf;

import java.util.Map;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;

public class ItemInterface {

	private ItemInterface() {
	}

	// @BytecodeMethod
	@ContainsInlineBytecode
	@ChangeType("L{vim:Item};")
	private static Object getItemByName(String name) {
		// Bytecode.var(Opcodes.ALOAD, 0);
		// Bytecode.method(Opcodes.INVOKESTATIC, "{vim:Item}",
		// "{vim:Item.getByName}", "(Ljava/lang/String;)L{vim:Item};", false);
		// Bytecode.insn(Opcodes.ARETURN);
		return InlineOps.method(Opcodes.INVOKESTATIC, "{vim:Item}", "{vim:Item.getByName}").returnType("L{vim:Item};")
				.param(String.class).argObject(name).invokeObject();
	}

	// @BytecodeMethod
	@ContainsInlineBytecode
	private static String getNameFromItem(@ChangeType("L{vim:Item};") Object item) {
		// Bytecode.field(Opcodes.GETSTATIC, "{vim:Item}",
		// "{vim:Item.itemRegistry}", "L{vim:RegistryNamespaced};");
		// Bytecode.field(Opcodes.GETFIELD, "{vim:RegistryNamespaced}",
		// "{vim:RegistryNamespaced.inverseObjectRegistry}",
		// "Ljava/util/Map;");
		// Bytecode.var(Opcodes.ALOAD, 0);
		// Bytecode.method(Opcodes.INVOKEINTERFACE, "java/util/Map", "get",
		// "(Ljava/lang/Object;)Ljava/lang/Object;",
		// true);
		// Bytecode.var(Opcodes.ASTORE, 1);
		// Bytecode.var(Opcodes.ALOAD, 1);
		// Bytecode.jump(Opcodes.IFNONNULL, "nameNotNull");
		// Bytecode.insn(Opcodes.ACONST_NULL);
		// Bytecode.insn(Opcodes.ARETURN);
		// Bytecode.label("nameNotNull");
		// Bytecode.var(Opcodes.ALOAD, 1);
		// Bytecode.method(Opcodes.INVOKEVIRTUAL, "java/lang/Object",
		// "toString", "()Ljava/lang/String;", false);
		// Bytecode.insn(Opcodes.ARETURN);
		Object itemName = ((Map<?, ?>) InlineOps
				.field(Opcodes.GETFIELD, "{vim:RegistryNamespaced}", "{vim:RegistryNamespaced.inverseObjectRegistry}")
				.type(Map.class).instance(InlineOps.field(Opcodes.GETSTATIC, "{vim:Item}", "{vim:Item.itemRegistry}")
						.type("L{vim:RegistryNamespaced};").getObject())
				.getObject()).get(item);
		if (itemName == null) {
			return null;
		} else {
			return itemName.toString();
		}
	}

}
