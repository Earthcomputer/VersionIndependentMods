package net.earthcomputer.vimapi.installer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.earthcomputer.vimapi.VIM;

public class Installer {

	static final String TITLE = "VIM Installer";
	static final String VIM_ARCHIVE_NAME = "vimapi-" + VIM.VERSION + ".jar";

	static final File MC_HOME;
	private static final String RUN_SERVER_SCRIPT;

	static {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			File mcHome = new File(System.getenv("appdata"), ".minecraft");
			if (!mcHome.isDirectory()) {
				mcHome = new File(System.getProperty("user.home"), ".minecraft");
			}
			MC_HOME = mcHome;
			RUN_SERVER_SCRIPT = "run_vim_server.bat";
		} else {
			if (os.contains("mac")) {
				MC_HOME = new File(System.getProperty("user.home"), "Library/Application Support/minecraft");
			} else {
				MC_HOME = new File(System.getProperty("user.home"), ".minecraft");
			}
			RUN_SERVER_SCRIPT = "run_vim_server";
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Failed to set L&F, not to worry...");
		}

		int installingFor = JOptionPane.showOptionDialog(null, "What do you want to install VIM for?", TITLE,
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new Object[] { "Vanilla Client", " Vanilla Server", "Forge Client", "Forge Server" }, "Client");

		switch (installingFor) {
		case 0:
			if (!installForClient()) {
				return;
			}
			break;
		case 1:
			if (!installForServer()) {
				return;
			}
			break;
		case 2:
			if (!installForForgeClient()) {
				return;
			}
			break;
		case 3:
			if (!installForForgeServer()) {
				return;
			}
			break;
		default:
			return;
		}
		JOptionPane.showMessageDialog(null, "Installed successfully", TITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	private static boolean installForClient() {
		return VanillaClientInstall.installClient();
	}

	private static boolean installForServer() {
		File serverDir = getDirectory("Select directory of server");
		if (serverDir == null) {
			return false;
		}

		File serverFile = null;
		for (File file : serverDir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jar")) {
				if (serverFile != null) {
					JOptionPane.showMessageDialog(null,
							"There appears to be more than one JAR file in that directory.\nRe-install the Minecraft server and try again",
							TITLE, JOptionPane.ERROR_MESSAGE);
					return false;
				}
				serverFile = file;
			}
		}
		if (serverFile == null) {
			JOptionPane.showMessageDialog(null,
					"There doesn't seem to be a JAR file in that directory.\nInstall the Minecraft server and try again",
					TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (Pattern.compile("\\s").matcher(serverFile.getName()).find()) {
			JOptionPane.showMessageDialog(null, "The same of the server JAR file may not contain spaces", TITLE,
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		List<String> libraryPaths = new ArrayList<String>();
		libraryPaths.add(installLibrary(serverDir, "LaunchWrapper", "net.minecraft", "launchwrapper", "1.9"));
		libraryPaths.add(installLibrary(serverDir, "ASM", "org.ow2.asm", "asm-all", "5.0.3"));
		libraryPaths.add(installLibrary(serverDir, "JOpt Simple", "net.sf.jopt-simple", "jopt-simple", "4.6"));
		libraryPaths
				.add(installLibrary(serverDir, "Commons Compress", "org.apache.commons", "commons-compress", "1.8.1"));
		if (libraryPaths.contains(null)) {
			return false;
		}

		File launcherFile = new File(serverDir, VIM_ARCHIVE_NAME);
		try {
			BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(launcherFile));
			ZipOutputStream jarOut = new ZipOutputStream(bufferedOut);
			ZipInputStream jarIn = new ZipInputStream(Installer.class.getResourceAsStream("/" + VIM_ARCHIVE_NAME));
			ZipEntry entry = jarIn.getNextEntry();
			while (entry != null) {
				jarOut.putNextEntry(new JarEntry(entry.getName()));
				if (!entry.getName().equals("META-INF/MANIFEST.MF")) {
					copyStream(jarIn, jarOut);
				} else {
					Manifest manifest = generateManifest(libraryPaths, serverFile);
					manifest.write(jarOut);
				}
				jarOut.closeEntry();
				entry = jarIn.getNextEntry();
			}
			jarIn.close();
			bufferedOut.flush();
			jarOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error creating the server launcher JAR", TITLE,
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			Files.copy(Installer.class.getResourceAsStream("/" + RUN_SERVER_SCRIPT),
					new File(serverDir, RUN_SERVER_SCRIPT).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error creating launch script", TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}

		File modsDir = new File(serverDir, "VIMMods");
		if (!modsDir.isDirectory()) {
			modsDir.mkdir();
		}

		return true;
	}

	private static boolean installForForgeClient() {
		File modsDir = new File(MC_HOME, "mods");
		if (!modsDir.isDirectory()) {
			modsDir.mkdirs();
		}
		modsDir = new File(MC_HOME, "VIMMods");
		if (!modsDir.isDirectory()) {
			modsDir.mkdir();
		}
		return createVIMJar(new File(modsDir, VIM_ARCHIVE_NAME));
	}

	private static boolean installForForgeServer() {
		File serverDir = getDirectory("Select directory of server");
		if (serverDir == null) {
			return false;
		}
		File modsDir = new File(serverDir, "mods");
		if (!modsDir.isDirectory()) {
			modsDir.mkdirs();
		}
		modsDir = new File(serverDir, "VIMMods");
		if (!modsDir.isDirectory()) {
			modsDir.mkdir();
		}
		return createVIMJar(new File(modsDir, VIM_ARCHIVE_NAME));
	}

	private static File getDirectory(String title) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(title);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null) {
			return null;
		} else if (!selectedFile.isDirectory()) {
			return null;
		} else {
			return selectedFile;
		}
	}

	private static String installLibrary(File serverDir, String name, String group, String artifact, String version) {
		String groupPath = group.replace('.', '/');
		String remotePath = String.format("%1$s/%2$s/%3$s/%2$s-%3$s.jar", groupPath, artifact, version);
		String localPath = "libraries/".concat(remotePath);
		File destFile = new File(serverDir, localPath);
		if (destFile.isFile()) {
			return localPath;
		}
		URL url;
		try {
			url = new URL("https://libraries.minecraft.net/" + remotePath);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		try {
			File parentFile = destFile.getParentFile();
			if (!parentFile.isDirectory()) {
				parentFile.mkdirs();
			}
			Files.copy(new BufferedInputStream(url.openConnection().getInputStream()), destFile.toPath());
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null,
					"An error occurred downloading the library \"" + name + "\".\nCheck your internet connection",
					TITLE, JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "An error occurred downloading the library \"" + name + "\n", TITLE,
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return localPath;
	}

	// Code from Commons Compress
	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[8024];
		int n = 0;
		while (-1 != (n = in.read(buffer))) {
			out.write(buffer, 0, n);
		}
	}

	private static Manifest generateManifest(List<String> libraries, File serverFile) {
		Manifest manifest = new Manifest();
		Attributes mainAttributes = manifest.getMainAttributes();

		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");

		StringBuilder classPath = new StringBuilder(serverFile.getName());
		for (String library : libraries) {
			classPath.append(' ').append(library);
		}
		mainAttributes.putValue("Class-Path", classPath.toString());

		mainAttributes.putValue("Main-Class", "net.earthcomputer.vimapi.serverlauncher.ServerLaunch");

		return manifest;
	}

	static boolean createVIMJar(File location) {
		try {
			Files.copy(Installer.class.getResourceAsStream("/" + VIM_ARCHIVE_NAME), location.toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error creating VIM JAR", TITLE, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
