package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.util.List;

import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.VIM;

/**
 * The tweak class used for clients without FML
 */
public class VIMTweakerClient extends AbstractVIMTweaker {

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
		super.acceptOptions(args, gameDir, assetsDir, version);
		this.args.add("--gameDir");
		this.args.add(gameDir.getAbsolutePath());
		this.args.add("--assetsDir");
		this.args.add(assetsDir.getAbsolutePath());
		this.args.add("--version");
		this.args.add(version);
		VIM.setSide(EnumSide.CLIENT);
		VIM.lock();
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

}
