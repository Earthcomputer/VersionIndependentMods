package net.earthcomputer.vimapi.core.itf;

import java.util.Map;

import org.objectweb.asm.Opcodes;

import net.earthcomputer.vimapi.core.ChangeType;
import net.earthcomputer.vimapi.core.ContainsInlineBytecode;
import net.earthcomputer.vimapi.core.InlineOps;

/**
 * This class interfaces with the vanilla Item class
 */
public class ItemInterface {

	private ItemInterface() {
	}

	@ContainsInlineBytecode
	@ChangeType("L{vim:Item};")
	private static Object getItemByName(String name) {
		return InlineOps.method(Opcodes.INVOKESTATIC, "{vim:Item}", "{vim:Item.getByName}").returnType("L{vim:Item};")
				.param(String.class).argObject(name).invokeObject();
	}

	@ContainsInlineBytecode
	private static String getNameFromItem(@ChangeType("L{vim:Item};") Object item) {
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
