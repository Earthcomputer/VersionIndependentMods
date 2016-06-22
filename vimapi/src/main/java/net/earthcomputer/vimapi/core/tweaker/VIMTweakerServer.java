package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.VIM;
import net.earthcomputer.vimapi.serverlauncher.ServerLaunch;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * The tweak class used for servers without FML
 */
public class VIMTweakerServer extends AbstractVIMTweaker {

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
		super.acceptOptions(args, gameDir, assetsDir, version);
		VIM.setSide(EnumSide.SERVER);
		VIM.lock();

		try {
			for (String extraLibrary : ServerLaunch.getExtraLibraries()) {
				URL url = new File(extraLibrary).toURI().toURL();
				Launch.classLoader.addURL(url);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.server.MinecraftServer";
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		super.injectIntoClassLoader(classLoader);
	}

}
