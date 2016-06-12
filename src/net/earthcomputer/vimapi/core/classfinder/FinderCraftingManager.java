package net.earthcomputer.vimapi.core.classfinder;

import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class FinderCraftingManager extends Finder {

	private static final List<String> STRINGS = Arrays.asList("###", "~~ ", "~O ", "  ~", "XXX");

	@Override
	public void accept(String className, ClassConstants constants, ClassNode node) {
		if (constants.getStringRefs().containsAll(STRINGS)) {
			UsefulNames.found("vim:CraftingManager", className);

			String craftingManagerDesc = "L" + className + ";";
			String addRecipeArg = "[Ljava/lang/Object;)";

			for (FieldNode field : node.fields) {
				if (field.desc.equals(craftingManagerDesc)) {
					UsefulNames.found("vim:CraftingManager.instance", field.name);
				}
			}

			boolean foundAddShapedRecipe = false;

			for (MethodNode method : node.methods) {
				if (method.desc.contains(addRecipeArg)) {
					if (foundAddShapedRecipe) {
						UsefulNames.found("vim:CraftingManager.addShapelessRecipe", method.name);
					} else {
						foundAddShapedRecipe = true;
						UsefulNames.found("vim:CraftingManager.addShapedRecipe", method.name);
						UsefulNames.found("vim:ShapedRecipe", Type.getReturnType(method.desc).getInternalName());
					}
				} else if (method.desc.endsWith(")V")) {
					Type[] argTypes = Type.getArgumentTypes(method.desc);
					if (argTypes.length == 1) {
						UsefulNames.found("vim:CraftingManager.addRecipe", method.name);
						UsefulNames.found("vim:IRecipe", argTypes[0].getInternalName());
					}
				}
			}
		}
	}

}
