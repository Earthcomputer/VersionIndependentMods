package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.util.List;

import net.earthcomputer.vimapi.VIM;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public abstract class AbstractVIMTweaker implements ITweaker {

	protected List<String> args;

	static final String[] TRANSFORMERS = new String[] { "net.earthcomputer.vimapi.core.tweaker.BytecodeTransformer",
			"net.earthcomputer.vimapi.core.tweaker.AccessTransformer",
			"net.earthcomputer.vimapi.core.tweaker.InjectingTransformer" };
	static final String[] CLASS_LOADER_EXCLUSIONS = new String[] { "net.earthcomputer.vimapi.VIM" };
	static final String[] TRANSFORMER_EXCLUSIONS = new String[] { "net.earthcomputer.vimapi.core.classfinder.",
			"net.earthcomputer.vimapi.EnumSide" };

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
		this.args = VIM.acceptArgs(args, gameDir);
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[args.size()]);
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		for (String exclusion : CLASS_LOADER_EXCLUSIONS) {
			classLoader.addClassLoaderExclusion(exclusion);
		}

		for (String exclusion : TRANSFORMER_EXCLUSIONS) {
			classLoader.addTransformerExclusion(exclusion);
		}

		VIM.findClasses(classLoader.getURLs(), getLaunchTarget());

		for (String transformer : TRANSFORMERS) {
			classLoader.registerTransformer(transformer);
		}
	}

}
