package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.util.List;

import net.earthcomputer.vimapi.VIM;
import net.earthcomputer.vimapi.core.ClassFinder;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public abstract class AbstractVIMTweaker implements ITweaker {

	protected List<String> args;

	static final String[] TRANSFORMERS = new String[] { BytecodeTransformer.class.getName(),
			AllPublicTransformer.class.getName(), InjectingTransformer.class.getName() };

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
		ClassFinder.searchURLsForClasses(classLoader.getURLs(), getLaunchTarget());

		for (String transformer : TRANSFORMERS) {
			classLoader.registerTransformer(transformer);
		}
	}

}
