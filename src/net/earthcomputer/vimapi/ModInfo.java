package net.earthcomputer.vimapi;

/**
 * A data structure containing information about a mod
 */
public class ModInfo {

	private final String id;
	private final String name;
	private final String version;
	private final String minimumMCVersion;
	private final Object instance;

	public ModInfo(String id, String name, String version, String minimumMCVersion, Object instance) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.minimumMCVersion = minimumMCVersion;
		this.instance = instance;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getMinimumMCVersion() {
		return minimumMCVersion;
	}

	public Object modInstance() {
		return instance;
	}

}
