package net.earthcomputer.vimapi.core.tweaker;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.earthcomputer.vimapi.VIM;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

/**
 * The FML mod container for VIM
 */
public class VIMModContainer extends DummyModContainer {

	public VIMModContainer() {
		super(new ModMetadata());
		ModMetadata meta = this.getMetadata();
		meta.modId = "vim";
		meta.name = "VersionIndependentMods";
		meta.version = VIM.VERSION;
		meta.authorList = Arrays.asList("Earthcomputer");
		meta.description = "A mod loader and API which makes mods able to be written only once for many Minecraft versions";
		meta.url = "http://github.com/Earthcomputer/VersionIndependentMods";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}

}
