package net.earthcomputer.vimapi;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class VIM {
	public static final Logger LOGGER = LogManager.getLogger("VIM");
	public static final String VERSION = "1.0";

	private static boolean isLocked = false;
	private static File gameDir;
	private static File modsDir;

	private static EnumSide side;

	public static List<String> acceptArgs(List<String> args, File gameDir) {
		if (isLocked) {
			throw new IllegalStateException("Cannot call VIM.acceptArgs() after it is locked");
		}

		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();

		OptionSpec<File> modsOption = parser.accepts("VIMModsDir", "The directory to load VIM mods from")
				.withRequiredArg().ofType(File.class).defaultsTo(new File(gameDir, "VIMMods"));
		OptionSpec<String> nonOption = parser.nonOptions();

		OptionSet options = parser.parse(args.toArray(new String[args.size()]));
		modsDir = modsOption.value(options);

		VIM.gameDir = gameDir;

		return Lists.newArrayList(nonOption.values(options));
	}

	public static void setSide(EnumSide side) {
		if (isLocked) {
			throw new IllegalStateException("Cannot call VIM.setSide() after it is locked");
		}

		VIM.side = side;
	}

	public static void lock() {
		if (isLocked) {
			throw new IllegalStateException("Cannot call VIM.lock() after it is locked");
		}

		isLocked = true;
	}

	public static File getGameDir() {
		return gameDir;
	}

	public static File getModsDir() {
		return modsDir;
	}

	public static EnumSide getSide() {
		return side;
	}

}
