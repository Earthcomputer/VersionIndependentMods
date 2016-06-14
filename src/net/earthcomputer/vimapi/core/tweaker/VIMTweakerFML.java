package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.VIM;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;

/**
 * The FML loading plugin for use with FML
 */
public class VIMTweakerFML implements IFMLLoadingPlugin {

	public VIMTweakerFML() {
		// FMLRelaunchLog.log(Level.WARN, "The coremod %s does not have a
		// MCVersion annotation, it may cause issues with this version of
		// Minecraft", coreModClass);
		LogManager.getLogger("VIM").info("Ignore what FML just said then... as we know, we run on multiple versions");
		Launch.classLoader.addTransformerExclusion("net.earthcomputer.vimapi.core.tweaker.");
	}

	@Override
	public String[] getASMTransformerClass() {
		return AbstractVIMTweaker.TRANSFORMERS;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return VIMModContainer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		for (String exclusion : AbstractVIMTweaker.CLASS_LOADER_EXCLUSIONS) {
			// class loader exclusions don't work for FML, though transformer
			// exclusions do
			Launch.classLoader.addTransformerExclusion(exclusion);
		}

		for (String exclusion : AbstractVIMTweaker.TRANSFORMER_EXCLUSIONS) {
			Launch.classLoader.addTransformerExclusion(exclusion);
		}

		@SuppressWarnings("unchecked")
		List<String> args = (List<String>) Launch.blackboard.get("ArgumentList");
		args = VIMInterface.acceptArgs(args, (File) data.get("mcLocation"));
		Launch.blackboard.put("ArgumentList", args);
		Side side = FMLLaunchHandler.side();
		VIMInterface.setSide(side == Side.CLIENT ? EnumSide.CLIENT : EnumSide.SERVER);
		VIMInterface.findClasses(Launch.classLoader.getURLs(),
				side == Side.CLIENT ? "net.minecraft.client.main.Main" : "net.minecraft.server.MinecraftServer");
		VIMInterface.lock();
	}

	// Unfortunately this class is needed so as not to load the VIM class
	// before it is added as a class loader exclusion
	private static class VIMInterface {

		public static List<String> acceptArgs(List<String> args, File gameDir) {
			return VIM.acceptArgs(args, gameDir);
		}

		public static void setSide(EnumSide side) {
			VIM.setSide(side);
		}

		public static void findClasses(URL[] urls, String mainClass) {
			VIM.findClasses(urls, mainClass);
		}

		public static void lock() {
			VIM.lock();
		}

	}

}
