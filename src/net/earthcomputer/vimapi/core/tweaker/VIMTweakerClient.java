package net.earthcomputer.vimapi.core.tweaker;

import net.earthcomputer.vimapi.EnumSide;

public class VIMTweakerClient extends AbstractVIMTweaker {

	public VIMTweakerClient() {
		side = EnumSide.CLIENT;
	}

	@Override
	public String getLaunchTarget() {
		return "net.minecraft.client.main.Main";
	}

}
