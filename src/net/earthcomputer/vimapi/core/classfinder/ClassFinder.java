package net.earthcomputer.vimapi.core.classfinder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.compress.utils.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.earthcomputer.vimapi.VIM;

public class ClassFinder {

	private static final List<String> NBT_BASE_STRINGS = Arrays.asList("END", "BYTE", "SHORT", "INT", "LONG", "FLOAT",
			"DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]");
	private static final List<String> ITEM_STRINGS = Arrays.asList("iron_shovel", "feather", "bread", "sign",
			"milk_bucket", "clock", "cake", "melon_seeds", "blaze_rod", "spawn_egg");
	private static final List<String> ITEM_STACK_STRINGS = Arrays.asList("x", "@", "id", "Count", "Damage", "tag",
			"ench", "display", "Name", "#%04d/%d%s");
	private static final List<String> CRAFTING_MANAGER_STRINGS = Arrays.asList("###", "~~ ", "~O ", "  ~", "XXX");

	private static final Map<UsefulNames, String> usefulNames = Maps.newHashMap();

	private ClassFinder() {
	}

	public static void searchURLsForClasses(URL[] urls, String mainClass) {
		String entryToFind = mainClass.replace('.', '/') + ".class";
		boolean foundJar = false;
		for (URL url : urls) {
			if (!url.getFile().endsWith(".jar")) {
				continue;
			}

			JarFile jarFile = null;
			try {
				jarFile = new JarFile(new File(url.toURI()));

				Enumeration<JarEntry> entries = jarFile.entries();
				Set<JarEntry> classEntries = Sets.newHashSet();
				boolean foundEntry = false;
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						classEntries.add(entry);
						if (entryToFind.equals(entry.getName())) {
							foundEntry = true;
						}
					}
				}
				if (!foundEntry) {
					continue;
				}
				searchJarForRightClasses(jarFile, classEntries);
				foundJar = true;
				break;
			} catch (IOException e) {
				continue;
			} catch (URISyntaxException e) {
				continue;
			} finally {
				IOUtils.closeQuietly(jarFile);
			}
		}

		if (!foundJar) {
			throw new RuntimeException("Unable to locate the Minecraft JAR");
		}
	}

	private static void searchJarForRightClasses(JarFile jar, Set<JarEntry> entries) throws IOException {
		for (int iteration = 0; iteration <= 2; iteration++) {
			Iterator<JarEntry> entryI = entries.iterator();
			while (entryI.hasNext()) {
				JarEntry entry = entryI.next();
				String internalClassName = entry.getName();
				internalClassName = internalClassName.substring(0, internalClassName.length() - 6);
				byte[] bytes = IOUtils.toByteArray(jar.getInputStream(entry));
				UsefulNames usefulClass;
				if (iteration == 0) {
					usefulClass = examineClassByConstants(internalClassName, bytes);
				} else if (iteration == 1) {
					usefulClass = examineClassByContent(internalClassName, bytes);
				} else {
					usefulClass = examineClassByContentPost(internalClassName, bytes);
				}
				if (usefulClass != null) {
					usefulNames.put(usefulClass, internalClassName);
					entryI.remove();
				}
			}
		}

		Set<UsefulNames> unableToFind = Sets.newHashSet();
		for (UsefulNames usefulName : UsefulNames.values()) {
			if (!usefulNames.containsKey(usefulName)) {
				if (usefulName.getWorkingSide() == null || usefulName.getWorkingSide() == VIM.getSide()) {
					unableToFind.add(usefulName);
				}
			}
		}
		if (!unableToFind.isEmpty()) {
			VIM.LOGGER.error("WARNING: UNABLE TO FIND THE FOLLOWING USEFUL NAMES, THIS COULD CAUSE SERIOUS ISSUES:");
			for (UsefulNames usefulName : unableToFind) {
				VIM.LOGGER.error(usefulName);
			}
		}
	}

	private static UsefulNames examineClassByConstants(String className, byte[] bytes) {
		Set<String> foundStrings = ClassConstants.readFromBytes(bytes).getStringRefs();

		if (foundStrings.containsAll(NBT_BASE_STRINGS)) {
			examineNBTBase(bytes);
			return UsefulNames.NBT_BASE;
		} else if (foundStrings.containsAll(ITEM_STRINGS)) {
			if (examineItem(className, bytes)) {
				return UsefulNames.ITEM;
			}
		} else if (foundStrings.containsAll(ITEM_STACK_STRINGS)) {
			// Return null, as we want to examine this class further with more
			// information
			usefulNames.put(UsefulNames.ITEM_STACK, className);
			return null;
		} else if (foundStrings.containsAll(CRAFTING_MANAGER_STRINGS)) {
			examineCraftingManager(className, bytes);
			return UsefulNames.CRAFTING_MANAGER;
		} else if (foundStrings.contains("textures/gui/title/mojang.png")) {
			examineMinecraft(bytes);
			return UsefulNames.MINECRAFT;
		}

		return null;
	}

	private static void examineNBTBase(byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_CODE);

		for (MethodNode method : node.methods) {
			if ((method.access & Opcodes.ACC_ABSTRACT) != 0 && method.desc.endsWith(")B")) {
				usefulNames.put(UsefulNames.NBT_BASE_GETTYPE, method.name);
			} else if ((method.access & Opcodes.ACC_STATIC) != 0 && method.desc.startsWith("(B)")) {
				usefulNames.put(UsefulNames.NBT_BASE_CREATENEWBYTYPE, method.name);
			}
		}
	}

	private static boolean examineItem(String className, byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_CODE);

		// If not Items class
		if (node.fields.size() > 64) {
			return false;
		}

		for (MethodNode method : node.methods) {
			if ((method.access & Opcodes.ACC_STATIC) != 0
					&& method.desc.equals("(Ljava/lang/String;)L" + className + ";")) {
				usefulNames.put(UsefulNames.ITEM_GETBYNAME, method.name);
			}
		}

		FieldNode itemRegistryField = node.fields.get(0);
		usefulNames.put(UsefulNames.ITEM_ITEMREGISTRY, itemRegistryField.name);
		usefulNames.put(UsefulNames.REGISTRY_NAMESPACED, Type.getType(itemRegistryField.desc).getInternalName());
		return true;
	}

	private static void examineCraftingManager(String className, byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_CODE);

		String craftingManagerDesc = "L" + className + ";";
		String addRecipeArg = "[Ljava/lang/Object;)";

		for (FieldNode field : node.fields) {
			if (field.desc.equals(craftingManagerDesc)) {
				usefulNames.put(UsefulNames.CRAFTING_MANAGER_INSTANCE, field.name);
			}
		}

		boolean foundAddShapedRecipe = false;

		for (MethodNode method : node.methods) {
			if (method.desc.contains(addRecipeArg)) {
				if (foundAddShapedRecipe) {
					usefulNames.put(UsefulNames.CRAFTING_MANAGER_ADDSHAPELESSRECIPE, method.name);
				} else {
					foundAddShapedRecipe = true;
					usefulNames.put(UsefulNames.CRAFTING_MANAGER_ADDSHAPEDRECIPE, method.name);
					usefulNames.put(UsefulNames.SHAPED_RECIPE, Type.getReturnType(method.desc).getInternalName());
				}
			} else if (method.desc.endsWith(")V")) {
				Type[] argTypes = Type.getArgumentTypes(method.desc);
				if (argTypes.length == 1) {
					usefulNames.put(UsefulNames.CRAFTING_MANAGER_ADDRECIPE, method.name);
					usefulNames.put(UsefulNames.IRECIPE, argTypes[0].getInternalName());
				}
			}
		}
	}

	private static void examineMinecraft(byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		boolean foundStartGame = false;
		for (MethodNode method : node.methods) {
			if (!foundStartGame && method.exceptions.contains("org/lwjgl/LWJGLException")) {
				foundStartGame = true;
				usefulNames.put(UsefulNames.MINECRAFT_STARTGAME, method.name);
			}
		}
	}

	private static UsefulNames examineClassByContent(String className, byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_CODE);

		if (node.superName.equals(usefulNames.get(UsefulNames.NBT_BASE))) {
			if ((node.access & Opcodes.ACC_ABSTRACT) != 0) {
				return UsefulNames.NBT_PRIMITIVE;
			} else {
				return examineSubclassOfNBTBase(node);
			}
		}

		if (node.superName.equals("net/minecraft/server/MinecraftServer")) {
			boolean isIntegratedServer = false;
			String mcDesc = "L" + usefulNames.get(UsefulNames.MINECRAFT) + ";";
			for (FieldNode field : node.fields) {
				if (field.desc.equals(mcDesc)) {
					isIntegratedServer = true;
					break;
				}
			}
			if (!isIntegratedServer) {
				examineDedicatedServer(node);
				return UsefulNames.DEDICATED_SERVER;
			}
		}

		return null;
	}

	private static UsefulNames examineSubclassOfNBTBase(ClassNode node) {
		UsefulNames usefulName = null;
		UsefulNames usefulField = null;
		FieldNode dataField = null;
		for (FieldNode field : node.fields) {
			String desc = field.desc;
			if (desc.equals("[B")) {
				usefulName = UsefulNames.NBT_BYTE_ARRAY;
				usefulField = UsefulNames.NBT_BYTE_ARRAY_DATA;
				dataField = field;
				break;
			} else if (desc.equals("Ljava/lang/String;")) {
				usefulName = UsefulNames.NBT_STRING;
				usefulField = UsefulNames.NBT_STRING_DATA;
				dataField = field;
				break;
			} else if (desc.equals("Ljava/util/List;")) {
				usefulName = UsefulNames.NBT_LIST;
				usefulField = UsefulNames.NBT_LIST_DATA;
				dataField = field;
				// Do not break here, we need to continue searching for
				// the tagType field
			} else if (desc.equals("Ljava/util/Map;")) {
				usefulName = UsefulNames.NBT_COMPOUND;
				usefulField = UsefulNames.NBT_COMPOUND_DATA;
				dataField = field;
				break;
			} else if (desc.equals("[I")) {
				usefulName = UsefulNames.NBT_INT_ARRAY;
				usefulField = UsefulNames.NBT_INT_ARRAY_DATA;
				dataField = field;
				break;
			} else if (desc.equals("B")) {
				// tagType field in NBTTagList
				usefulNames.put(UsefulNames.NBT_LIST_TAG_TYPE, field.name);
			}
		}
		if (usefulName == null) {
			// Assume NBTTagEnd
			return null;
		}
		usefulNames.put(usefulField, dataField.name);
		return usefulName;
	}

	private static void examineDedicatedServer(ClassNode node) {
		for (MethodNode method : node.methods) {
			if (!method.name.equals("<init>")) {
				usefulNames.put(UsefulNames.DEDICATED_SERVER_STARTSERVER, method.name);
				break;
			}
		}
	}

	private static UsefulNames examineClassByContentPost(String className, byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, ClassReader.SKIP_CODE);

		if (node.superName.equals(usefulNames.get(UsefulNames.NBT_PRIMITIVE))) {
			return examineSubclassOfNBTPrimitive(node);
		}

		if (node.name.equals(usefulNames.get(UsefulNames.REGISTRY_NAMESPACED))) {
			examineRegistryNamespaced(node);
			return UsefulNames.REGISTRY_NAMESPACED;
		}

		if (node.name.equals(usefulNames.get(UsefulNames.ITEM_STACK))) {
			examineItemStack(node);
			return UsefulNames.ITEM_STACK;
		}

		return null;
	}

	private static UsefulNames examineSubclassOfNBTPrimitive(ClassNode node) {
		UsefulNames usefulName = null;
		UsefulNames usefulField = null;
		FieldNode dataField = null;
		for (FieldNode field : node.fields) {
			String desc = field.desc;
			if (desc.equals("B")) {
				usefulName = UsefulNames.NBT_BYTE;
				usefulField = UsefulNames.NBT_BYTE_DATA;
				dataField = field;
				break;
			} else if (desc.equals("S")) {
				usefulName = UsefulNames.NBT_SHORT;
				usefulField = UsefulNames.NBT_SHORT_DATA;
				dataField = field;
				break;
			} else if (desc.equals("I")) {
				usefulName = UsefulNames.NBT_INT;
				usefulField = UsefulNames.NBT_INT_DATA;
				dataField = field;
				break;
			} else if (desc.equals("J")) {
				usefulName = UsefulNames.NBT_LONG;
				usefulField = UsefulNames.NBT_LONG_DATA;
				dataField = field;
				break;
			} else if (desc.equals("F")) {
				usefulName = UsefulNames.NBT_FLOAT;
				usefulField = UsefulNames.NBT_FLOAT_DATA;
				dataField = field;
				break;
			} else if (desc.equals("D")) {
				usefulName = UsefulNames.NBT_DOUBLE;
				usefulField = UsefulNames.NBT_DOUBLE_DATA;
				dataField = field;
				break;
			}
		}
		if (usefulName == null) {
			throw new RuntimeException("Encountered subclass of NBTPrimitive without a data field");
		}
		usefulNames.put(usefulField, dataField.name);
		return usefulName;
	}

	private static void examineItemStack(ClassNode node) {
		String itemDesc = "L" + usefulNames.get(UsefulNames.ITEM) + ";";
		String nbtCompoundDesc = "L" + usefulNames.get(UsefulNames.NBT_COMPOUND) + ";";
		// Clashes with animationsToGo
		boolean foundStackSize = false;
		// Likely to clash with something in the future
		boolean foundDamage = false;
		// Clashes with Forge's capNBT
		boolean foundTag = false;
		for (FieldNode field : node.fields) {
			if (field.desc.equals(itemDesc)) {
				usefulNames.put(UsefulNames.ITEM_STACK_ITEM, field.name);
			} else if (!foundStackSize && field.desc.equals("I") && (field.access & Opcodes.ACC_PUBLIC) != 0) {
				foundStackSize = true;
				usefulNames.put(UsefulNames.ITEM_STACK_STACKSIZE, field.name);
			} else if (!foundDamage && field.desc.equals("I")
					&& (field.access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
				foundDamage = true;
				usefulNames.put(UsefulNames.ITEM_STACK_DAMAGE, field.name);
			} else if (!foundTag && field.desc.equals(nbtCompoundDesc)) {
				foundTag = true;
				usefulNames.put(UsefulNames.ITEM_STACK_TAG, field.name);
			}
		}
	}

	private static void examineRegistryNamespaced(ClassNode node) {
		for (FieldNode field : node.fields) {
			if (field.desc.equals("Ljava/util/Map;")) {
				usefulNames.put(UsefulNames.REGISTRY_NAMESPACED_INVERSEOBJECTREGISTRY, field.name);
			}
		}
	}

	public static String getObfedName(UsefulNames usefulName) {
		return usefulNames.get(usefulName);
	}
}
