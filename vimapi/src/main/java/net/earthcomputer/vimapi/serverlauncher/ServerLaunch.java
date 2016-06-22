package net.earthcomputer.vimapi.serverlauncher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;

import net.minecraft.launchwrapper.Launch;

public class ServerLaunch {

	private static final String[] extraArgs = new String[] { "--tweakClass",
			"net.earthcomputer.vimapi.core.tweaker.VIMTweakerServer", "nogui" };

	private static final List<String> extraLibraries = Lists.newArrayList();

	public static void main(String[] args) {
		for (String extraLibrary : getClassPath().split("\\s+")) {
			extraLibraries.add(extraLibrary);
		}

		String[] newArgs = new String[args.length + extraArgs.length];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		System.arraycopy(extraArgs, 0, newArgs, args.length, extraArgs.length);
		Launch.main(newArgs);
	}

	private static File getJarLocation() {
		try {
			return new File(ServerLaunch.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getClassPath() {
		try {
			JarFile thisJar = new JarFile(getJarLocation());
			String classPath = thisJar.getManifest().getMainAttributes().getValue("Class-Path");
			thisJar.close();
			return classPath;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> getExtraLibraries() {
		return extraLibraries;
	}

}
