package net.earthcomputer.vimapi.core.tweaker;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.earthcomputer.vimapi.EnumSide;
import net.earthcomputer.vimapi.core.ClassFinder;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public abstract class AbstractVIMTweaker implements ITweaker {

	public static final Logger LOGGER = LogManager.getLogger("VIM");

	protected static EnumSide side;

	private List<String> args;
	private static File gameDir;
	private static File assetsDir;
	private static String version;

	private static File modsDir;

	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String version) {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();

		OptionSpec<File> modsOption = parser.accepts("VIMModsDir", "The directory to load VIM mods from")
				.withRequiredArg().ofType(File.class).defaultsTo(new File(gameDir, "VIMMods"));
		OptionSpec<String> nonOption = parser.nonOptions();

		OptionSet options = parser.parse(args.toArray(new String[args.size()]));
		modsDir = modsOption.value(options);

		this.args = Lists.newArrayList(nonOption.values(options));
		this.args.add("--gameDir");
		this.args.add(gameDir.getAbsolutePath());
		this.args.add("--assetsDir");
		this.args.add(assetsDir.getAbsolutePath());
		this.args.add("--version");
		this.args.add(version);

		AbstractVIMTweaker.gameDir = gameDir;
		AbstractVIMTweaker.assetsDir = assetsDir;
		AbstractVIMTweaker.version = version;
	}

	@Override
	public String[] getLaunchArguments() {
		return args.toArray(new String[args.size()]);
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		ClassFinder.searchURLsForClasses(classLoader.getURLs(), getLaunchTarget());

		classLoader.registerTransformer(BytecodeTransformer.class.getName());
		classLoader.registerTransformer(AllPublicTransformer.class.getName());
		classLoader.registerTransformer(InjectingTransformer.class.getName());
		try {
			classLoader.loadClass("net.earthcomputer.vimapi.core.itf.NBTInterface");
			classLoader.loadClass("net.earthcomputer.vimapi.core.itf.ItemInterface");
			classLoader.loadClass("net.earthcomputer.vimapi.core.itf.ItemStackInterface");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static EnumSide getSide() {
		return side;
	}

	public static File getGameDir() {
		return gameDir;
	}

	public static File getAssetsDir() {
		return assetsDir;
	}

	public static String getVersion() {
		return version;
	}

	public static File getModsDir() {
		return modsDir;
	}

}
