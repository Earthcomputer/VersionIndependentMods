package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FinderNBTBaseSubclass implements IFinder {

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (node.superName.equals(UsefulNames.get("vim:NBTBase"))) {
			if ((node.access & Opcodes.ACC_ABSTRACT) != 0) {
				UsefulNames.found("vim:NBTPrimitive", className);
			} else {
				String usefulName = null;
				FieldNode dataField = null;
				for (FieldNode field : node.fields) {
					String desc = field.desc;
					if (desc.equals("[B")) {
						usefulName = "vim:NBTByteArray";
						dataField = field;
						break;
					} else if (desc.equals("Ljava/lang/String;")) {
						usefulName = "vim:NBTString";
						dataField = field;
						break;
					} else if (desc.equals("Ljava/util/List;")) {
						usefulName = "vim:NBTList";
						dataField = field;
						// Do not break here, we need to continue searching for
						// the tagType field
					} else if (desc.equals("Ljava/util/Map;")) {
						usefulName = "vim:NBTCompound";
						dataField = field;
						break;
					} else if (desc.equals("[I")) {
						usefulName = "vim:NBTIntArray";
						dataField = field;
						break;
					} else if (desc.equals("B")) {
						// tagType field in NBTTagList
						UsefulNames.found("vim:NBTList.tagType", field.name);
					}
				}

				// usefulName may be null if NBTTagEnd
				if (usefulName != null) {
					UsefulNames.found(usefulName, className);
					UsefulNames.found(usefulName + ".data", dataField.name);
				}
			}
		}
	}

}
