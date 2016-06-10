package net.earthcomputer.vimapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.compress.utils.IOUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import net.earthcomputer.vimapi.core.tweaker.AbstractVIMTweaker;
import net.minecraft.launchwrapper.Launch;

public class Loader {

	private static final String VIMMOD_DESC = Type.getDescriptor(VimMod.class);

	private static final List<ModInfo> mods = Lists.newArrayList();

	public static void beginLoading() {
		constructMods();
		preInitializeMods();
	}

	private static void constructMods() {
		File modsDir = AbstractVIMTweaker.getModsDir();

		if (!modsDir.isDirectory()) {
			AbstractVIMTweaker.LOGGER.info("Identified no mods to load");
		} else {
			AbstractVIMTweaker.LOGGER.info("Searching " + modsDir + " for mods");
			List<String> modClasses = Lists.newArrayList();
			try {
				searchDirForMods(modsDir, modClasses);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
			if (modClasses.isEmpty()) {
				AbstractVIMTweaker.LOGGER.info("Identified no mods to load");
			}
			AbstractVIMTweaker.LOGGER.info("Identified " + modClasses.size() + " mods to load:");
			AbstractVIMTweaker.LOGGER.info(modClasses);

			AbstractVIMTweaker.LOGGER.info("Starting construction...");
			for (String modClass : modClasses) {
				Class<?> clazz;
				try {
					clazz = Class.forName(modClass);
				} catch (ClassNotFoundException e) {
					AbstractVIMTweaker.LOGGER.error("It looks like you have a corrupt mod file");
					throw Throwables.propagate(e);
				}
				VimMod theMod = clazz.getAnnotation(VimMod.class);
				String id = theMod.id();
				String name = theMod.name();
				if (name.isEmpty()) {
					name = id;
				}
				String version = theMod.version();
				String minimumMCVersion = theMod.minimumMCVersion();
				if (minimumMCVersion.isEmpty()) {
					minimumMCVersion = null;
				}
				Object instance;
				try {
					instance = clazz.newInstance();
				} catch (Exception e) {
					AbstractVIMTweaker.LOGGER.error("The class " + modClass + " (belonging to mod " + name
							+ ") does not have a zero-argument constructor");
					throw Throwables.propagate(e);
				}
				mods.add(new ModInfo(id, name, version, minimumMCVersion, instance));
			}
		}
	}

	private static void searchDirForMods(File dir, final List<String> modClasses) throws IOException {
		File[] candidates = dir.listFiles();
		for (File candidate : candidates) {
			if (candidate.isDirectory()) {
				AbstractVIMTweaker.LOGGER.info("Also searching " + candidate + " for mods");
				searchDirForMods(candidate, modClasses);
			} else if (candidate.getName().endsWith(".jar") || candidate.getName().endsWith(".zip")) {
				try {
					ZipFile jarFile = new ZipFile(candidate);
					Enumeration<? extends ZipEntry> entries = jarFile.entries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = entries.nextElement();
						if (!entry.getName().endsWith(".class")) {
							continue;
						}
						InputStream is = jarFile.getInputStream(entry);
						byte[] classBytes = IOUtils.toByteArray(is);
						ClassReader reader = new ClassReader(classBytes);
						reader.accept(new ClassVisitor(Opcodes.ASM5) {
							private String className;

							@Override
							public void visit(int version, int access, String name, String signature, String superName,
									String[] interfaces) {
								className = name;
							}

							@Override
							public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
								if (desc.equals(VIMMOD_DESC)) {
									modClasses.add(className.replace('/', '.'));
								}
								return null;
							}
						}, ClassReader.SKIP_CODE);
					}
					jarFile.close();
					Launch.classLoader.addURL(candidate.toURI().toURL());
				} catch (ZipException e) {
					AbstractVIMTweaker.LOGGER
							.error("The file " + candidate.getName() + " does not have a valid ZIP format, skipping");
				}
			}
		}
	}

	private static void preInitializeMods() {
		AbstractVIMTweaker.LOGGER.info("Starting pre-initialization...");
		callLifecycleEvent(LifecycleEventType.PREINIT);
	}

	private static void callLifecycleEvent(LifecycleEventType type) {
		for (ModInfo mod : mods) {
			Object instance = mod.modInstance();
			Class<?> modClass = instance.getClass();
			for (Method method : modClass.getMethods()) {
				LifecycleHandler handler = method.getAnnotation(LifecycleHandler.class);
				if (handler != null && handler.value() == type) {
					try {
						method.invoke(instance);
					} catch (Exception e) {
						AbstractVIMTweaker.LOGGER.error("Exception calling preinit method");
						throw Throwables.propagate(e);
					}
				}
			}
		}
	}

}
