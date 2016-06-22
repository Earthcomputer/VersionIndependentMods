package net.earthcomputer.vimapi.installer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.earthcomputer.vimapi.VIM;

public class VanillaClientInstall {

	private static final JsonParser PARSER = new JsonParser();
	private static final int MAX_LAUNCHER_VERSION_FORMAT = 18;
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final DateFormat EN_US_DATE_FORMAT = DateFormat.getDateTimeInstance(2, 2, Locale.US);
	private static final DateFormat ISO_8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public static boolean installClient() {
		File vimInstallationDir = new File(Installer.MC_HOME, "libraries/net/earthcomputer/vimapi/" + VIM.VERSION);
		if (!vimInstallationDir.isDirectory()) {
			vimInstallationDir.mkdirs();
		}
		if (!Installer.createVIMJar(new File(vimInstallationDir, Installer.VIM_ARCHIVE_NAME))) {
			return false;
		}

		try {
			File launcherProfilesFile = new File(Installer.MC_HOME, "launcher_profiles.json");

			JsonObject profileList;

			JsonObject launcherProfilesRoot = PARSER.parse(new BufferedReader(new FileReader(launcherProfilesFile)))
					.getAsJsonObject();

			if (launcherProfilesRoot.getAsJsonObject("launcherVersion").get("format")
					.getAsInt() > MAX_LAUNCHER_VERSION_FORMAT) {
				JOptionPane.showMessageDialog(null, "VIM has not updated to the new launcher yet", Installer.TITLE,
						JOptionPane.ERROR_MESSAGE);
				return false;
			}

			profileList = launcherProfilesRoot.getAsJsonObject("profiles");

			Set<Map.Entry<String, JsonElement>> profileListEntrySet = profileList.entrySet();
			List<Object> profilesToDisplay = new ArrayList<Object>(profileListEntrySet.size());
			for (Map.Entry<String, JsonElement> profile : profileListEntrySet) {
				String version = findVersion(profile.getValue().getAsJsonObject());
				if (version != null) {
					profilesToDisplay.add(new ProfileOption(profile.getKey(), version));
				}
			}

			if (profilesToDisplay.isEmpty()) {
				JOptionPane.showMessageDialog(null, "There are no profiles that can be copied", Installer.TITLE,
						JOptionPane.ERROR_MESSAGE);
				return false;
			}

			Object input = JOptionPane.showInputDialog(null, "Which version do you want to copy?", Installer.TITLE,
					JOptionPane.QUESTION_MESSAGE, null, profilesToDisplay.toArray(new Object[profilesToDisplay.size()]),
					profilesToDisplay.get(0));
			if (input == null) {
				return false;
			}
			ProfileOption chosenProfile = (ProfileOption) input;

			String oldProfileName = chosenProfile.getName();
			String newProfileName = oldProfileName + "_VIM";
			String oldProfileVersion = chosenProfile.getVersion();
			String newProfileVersion = chosenProfile.getVersion() + "_VIM";

			File oldProfileDir = new File(Installer.MC_HOME, "versions/" + oldProfileVersion);
			File newProfileDir = new File(Installer.MC_HOME, "versions/" + newProfileVersion);
			File oldProfileJar = new File(oldProfileDir, oldProfileVersion + ".jar");
			File newProfileJar = new File(newProfileDir, newProfileVersion + ".jar");
			File oldProfileJson = new File(oldProfileDir, oldProfileVersion + ".json");
			File newProfileJson = new File(newProfileDir, newProfileVersion + ".json");
			if (!oldProfileJar.isFile()) {
				throw new Exception(oldProfileJar + " does not exist");
			}
			if (!oldProfileJson.isFile()) {
				throw new Exception(oldProfileJson + " does not exist");
			}

			if (!newProfileDir.isDirectory()) {
				newProfileDir.mkdirs();
			}
			Files.copy(oldProfileJar.toPath(), newProfileJar.toPath(), StandardCopyOption.REPLACE_EXISTING);

			JsonObject profileRoot = PARSER.parse(new BufferedReader(new FileReader(oldProfileJson))).getAsJsonObject();
			JsonArray libraries = profileRoot.getAsJsonArray("libraries");
			libraries.add(createLibrary("net.earthcomputer", "vimapi", VIM.VERSION));
			libraries.add(createLibrary("net.minecraft", "launchwrapper", "1.9"));
			libraries.add(createLibrary("org.ow2.asm", "asm-all", "5.0.3"));
			String launcherArgs = profileRoot.get("minecraftArguments").getAsString();
			launcherArgs += " --tweakClass net.earthcomputer.vimapi.core.tweaker.VIMTweakerClient";
			profileRoot.remove("minecraftArguments");
			profileRoot.addProperty("minecraftArguments", launcherArgs);
			profileRoot.remove("mainClass");
			profileRoot.addProperty("mainClass", "net.minecraft.launchwrapper.Launch");
			profileRoot.remove("id");
			profileRoot.addProperty("id", newProfileVersion);
			if (!newProfileJson.isFile()) {
				newProfileJson.createNewFile();
			}
			BufferedWriter bufferedOut = new BufferedWriter(new FileWriter(newProfileJson));
			GSON.toJson(profileRoot, bufferedOut);
			bufferedOut.flush();
			bufferedOut.close();

			JsonObject oldProfile = profileList.getAsJsonObject(oldProfileName);
			profileList.add(newProfileName, createProfileReference(oldProfile, newProfileName, newProfileVersion));
			bufferedOut = new BufferedWriter(new FileWriter(launcherProfilesFile));
			GSON.toJson(launcherProfilesRoot, bufferedOut);
			bufferedOut.flush();
			bufferedOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unable to read json files!\nTry re-running the launcher",
					Installer.TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private static String findVersion(JsonObject versionObject) {
		try {
			if (versionObject.has("lastVersionId")) {
				return versionObject.get("lastVersionId").getAsString();
			}
			List<String> allowedReleaseTypes;
			if (versionObject.has("allowedReleaseTypes")) {
				JsonArray array = versionObject.getAsJsonArray("allowedReleaseTypes");
				allowedReleaseTypes = new ArrayList<String>(array.size());
				for (JsonElement releaseType : array) {
					allowedReleaseTypes.add(releaseType.getAsString());
				}
			} else {
				allowedReleaseTypes = Arrays.asList("release");
			}
			File versionsDir = new File(Installer.MC_HOME, "versions");
			if (!versionsDir.isDirectory()) {
				return null;
			}
			List<String> versions = new ArrayList<String>();
			final Map<String, Date> releaseTimes = new HashMap<String, Date>();
			final Map<String, Date> updatedTimes = new HashMap<String, Date>();
			for (File versionFile : versionsDir.listFiles()) {
				if (versionFile.isDirectory()) {
					String versionName = versionFile.getName();
					File versionJson = new File(versionFile, versionName + ".json");
					if (versionJson.isFile()) {
						JsonObject versionRoot = PARSER.parse(new BufferedReader(new FileReader(versionFile)))
								.getAsJsonObject();
						if (allowedReleaseTypes.contains(versionRoot.get("type").getAsString())) {
							versions.add(versionName);
							if (versionRoot.has("releaseTime")) {
								releaseTimes.put(versionName, parseDate(versionRoot.get("releaseTime").getAsString()));
							}
							updatedTimes.put(versionName, parseDate(versionRoot.get("time").getAsString()));
						}
					}
				}
			}
			if (versions.isEmpty()) {
				return null;
			}
			Collections.sort(versions, new Comparator<String>() {
				@Override
				public int compare(String first, String second) {
					if (releaseTimes.containsKey(first) && releaseTimes.containsKey(second)) {
						return releaseTimes.get(first).compareTo(releaseTimes.get(second));
					}
					return updatedTimes.get(first).compareTo(updatedTimes.get(second));
				}
			});
			return versions.get(versions.size() - 1);
		} catch (Exception e) {
			return null;
		}
	}

	private static Date parseDate(String str) {
		try {
			return EN_US_DATE_FORMAT.parse(str);
		} catch (ParseException e) {
			try {
				return ISO_8601_DATE_FORMAT.parse(str);
			} catch (ParseException e1) {
				try {
					str = str.replace("Z", "+00:00");
					str = str.substring(0, 22) + str.substring(23);
					return ISO_8601_DATE_FORMAT.parse(str);
				} catch (Exception e2) {
					throw new JsonParseException("Invalid date", e2);
				}
			}
		}
	}

	private static JsonObject createLibrary(String group, String artifact, String version) {
		JsonObject object = new JsonObject();
		object.addProperty("name", String.format("%s:%s:%s", group, artifact, version));
		return object;
	}

	private static JsonObject createProfileReference(JsonObject oldProfile, String name, String version) {
		JsonObject newProfile = PARSER.parse(GSON.toJson(oldProfile)).getAsJsonObject();
		newProfile.remove("name");
		newProfile.addProperty("name", name);
		newProfile.remove("lastVersionId");
		newProfile.addProperty("lastVersionId", version);
		newProfile.remove("launcherVisibilityOnGameClose");
		newProfile.addProperty("launcherVisibilityOnGameClose", "keep the launcher open");
		return newProfile;
	}

	private static class ProfileOption {
		private String name;
		private String version;

		public ProfileOption(String name, String version) {
			this.name = name;
			this.version = version;
		}

		@Override
		public String toString() {
			return String.format("%s (%s)", name, version);
		}

		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}
	}

}
