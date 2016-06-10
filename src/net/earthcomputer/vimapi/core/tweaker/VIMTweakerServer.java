package net.earthcomputer.vimapi.core.tweaker;

import net.earthcomputer.vimapi.EnumSide;

public class VIMTweakerServer extends AbstractVIMTweaker {

	public VIMTweakerServer() {
		side = EnumSide.SERVER;
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.server.MinecraftServer";
	}

}
