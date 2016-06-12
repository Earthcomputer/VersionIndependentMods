package net.earthcomputer.vimapi.core.classfinder;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class FinderRegistryNamespaced extends Finder {

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		// This has already been found by FinderItem
		if (className.equals(UsefulNames.get("vim:RegistryNamespaced"))) {
			for (FieldNode field : node.fields) {
				if (field.desc.equals("Ljava/util/Map;")) {
					UsefulNames.found("vim:RegistryNamespaced.inverseObjectRegistry", field.name);
				}
			}
		}
	}

}
