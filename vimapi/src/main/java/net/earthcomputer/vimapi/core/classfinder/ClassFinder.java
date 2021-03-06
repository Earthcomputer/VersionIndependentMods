package net.earthcomputer.vimapi.core.classfinder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.compress.utils.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.earthcomputer.vimapi.VIM;
import net.earthcomputer.vimapi.util.BeforeAfterComparable;
import net.earthcomputer.vimapi.util.RelativePosition;

/**
 * A class in charge of finding vanilla classes and recognizing them. If you
 * want to recognize a vanilla class, use
 * <code>ClassFinder.registerFinder("your-modid:your-finder-name", your-finder)</code>
 */
public class ClassFinder {

	private static final List<BeforeAfterComparable<IFinder>> finderList = Lists.newArrayList();

	private ClassFinder() {
	}

	public static void registerFinder(String name, IFinder finder) {
		registerFinder(name, finder, RelativePosition.create());
	}

	public static void registerFinder(String name, IFinder finder, RelativePosition relativePos) {
		finderList.add(new BeforeAfterComparable<IFinder>(name, finder, relativePos));
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
				searchJarForUsefulClasses(jarFile, classEntries);
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

	private static void searchJarForUsefulClasses(JarFile jar, Set<JarEntry> entries) throws IOException {
		int numClasses = entries.size();
		String[] classNames = new String[numClasses];
		ClassConstants[] constants = new ClassConstants[numClasses];
		ClassNode[] nodes = new ClassNode[numClasses];

		VIM.LOGGER.info("Reading Minecraft JAR (" + entries.size() + " classes)");

		int i = 0;
		for (JarEntry entry : entries) {
			byte[] classBytes = IOUtils.toByteArray(new BufferedInputStream(jar.getInputStream(entry)));
			constants[i] = ClassConstants.readFromBytes(classBytes);
			ClassReader reader = new ClassReader(classBytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			nodes[i] = node;
			classNames[i] = node.name;
			i++;
		}

		IOUtils.closeQuietly(jar);

		Collections.sort(finderList);

		VIM.LOGGER.info("Searching Minecraft JAR for what VIM needs (" + finderList.size() + " finders)");

		for (BeforeAfterComparable<IFinder> comparableFinder : finderList) {
			IFinder finder = comparableFinder.getValue();

			for (i = 0; i < numClasses; i++) {
				finder.accept(classNames[i], constants[i], nodes[i]);
			}
		}

		// Let's free some memory
		finderList.clear();
	}

	static {
		registerFinder("vim:nbt_base", new FinderNBTBase());
		registerFinder("vim:nbt_base_subclass", new FinderNBTBaseSubclass(),
				RelativePosition.create().after("vim:nbt_base"));
		registerFinder("vim:nbt_primitive_subclass", new FinderNBTPrimitiveSubclass(),
				RelativePosition.create().after("vim:nbt_base_subclass"));

		registerFinder("vim:item", new FinderItem());

		registerFinder("vim:item_stack", new FinderItemStack(),
				RelativePosition.create().after("vim:nbt_base_subclass").after("vim:item"));

		registerFinder("vim:registry_namespaced", new FinderRegistryNamespaced(),
				RelativePosition.create().after("vim:item"));

		registerFinder("vim:crafting_manager", new FinderCraftingManager());

		registerFinder("vim:minecraft", new FinderMinecraft());

		registerFinder("vim:dedicated_server", new FinderDedicatedServer(),
				RelativePosition.create().after("vim:minecraft"));
	}
}
