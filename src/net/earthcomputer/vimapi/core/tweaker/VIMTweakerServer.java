package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.util.List;

import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.VIM;

public class VIMTweakerServer extends AbstractVIMTweaker {

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
		super.acceptOptions(args, gameDir, assetsDir, version);
		VIM.setSide(EnumSide.SERVER);
		VIM.lock();
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.server.MinecraftServer";
	}

}
