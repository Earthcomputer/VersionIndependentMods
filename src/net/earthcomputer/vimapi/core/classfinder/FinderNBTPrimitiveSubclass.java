package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FinderNBTPrimitiveSubclass extends Finder {

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (node.superName.equals(UsefulNames.get("vim:NBTPrimitive"))) {
			String usefulName = null;
			FieldNode dataField = null;
			for (FieldNode field : node.fields) {
				String desc = field.desc;
				if (desc.equals("B")) {
					usefulName = "vim:NBTByte";
					dataField = field;
					break;
				} else if (desc.equals("S")) {
					usefulName = "vim:NBTShort";
					dataField = field;
					break;
				} else if (desc.equals("I")) {
					usefulName = "vim:NBTInt";
					dataField = field;
					break;
				} else if (desc.equals("J")) {
					usefulName = "vim:NBTLong";
					dataField = field;
					break;
				} else if (desc.equals("F")) {
					usefulName = "vim:NBTFloat";
					dataField = field;
					break;
				} else if (desc.equals("D")) {
					usefulName = "vim:NBTDouble";
					dataField = field;
					break;
				}
			}
			if (usefulName == null) {
				throw new RuntimeException("Encountered subclass of NBTPrimitive without a data field");
			}
			UsefulNames.found(usefulName, className);
			UsefulNames.found(usefulName + ".data", dataField.name);
		}
	}

}
