package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.VIM;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;

public class VIMTweakerFML implements IFMLLoadingPlugin {

	public VIMTweakerFML() {
		// FMLRelaunchLog.log(Level.WARN, "The coremod %s does not have a
		// MCVersion annotation, it may cause issues with this version of
		// Minecraft", coreModClass);
		VIM.LOGGER.info("Ignore what FML just said then... as we know, we run on multiple versions");
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
		@SuppressWarnings("unchecked")
		List<String> args = (List<String>) Launch.blackboard.get("ArgumentList");
		args = VIM.acceptArgs(args, (File) data.get("mcLocation"));
		Launch.blackboard.put("ArgumentList", args);
		VIM.setSide(FMLLaunchHandler.side() == Side.CLIENT ? EnumSide.CLIENT : EnumSide.SERVER);
		VIM.lock();
	}

}
