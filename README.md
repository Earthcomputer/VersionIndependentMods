# VersionIndependentMods
VersionIndependentMods, or VIM, is an API for mods that do not want to depend on the Minecraft version. VIM is compatible with all Minecraft versions since 1.8, and is also compatible with Forge. Its intention is that modders don't have to worry about the Minecraft version or updating their mods every time Minecraft updates.
## Installation
### For players and modpack creators
- If you're using Forge (which you most likely are if you're adding VIM mods to a pre-existing modpack), download VIM from [this URL](http://dl.bintray.com/earthcomputer/earthcomputer-maven/net/earthcomputer/vimapi/vimapi/a0.0.2/vimapi-a0.0.2.jar) and put it into the `mods` folder of your modpack.
- If you're not using Forge, download the installer from [this URL](http://dl.bintray.com/earthcomputer/earthcomputer-maven/net/earthcomputer/vimapi/installer/a0.0.2/installer-a0.0.2.jar), run it, and follow the instructions.
- In both cases, mods that run with VIM should go in the `VIMMods` folder which is either automatically created by the installer or manually created next to the Forge `mods` folder.

### For modders
- Download the pre-made project from [this URL](http://dl.bintray.com/earthcomputer/earthcomputer-maven/net/earthcomputer/vimapi/prebuilt_project/a0.0.2/prebuilt_project-a0.0.2.zip) and extract it
- Run one of the following commands in that directory (note, on Linux, `gradlew` should be replaced with `./gradlew`, and on Mac, `gradlew` should be replaced with `bash gradlew`) :
  - If using Eclipse, run `gradlew eclipse`
  - If using IDEA, run `gradlew idea`
- Import this project to the workspace in the IDE
- Make their mod
- Compile their mod either using `gradlew build` or from within the IDE
- Publish their mod so everyone can play with it

### For contributors
Contributors can clone this repository in the normal `git clone` way (I use SourceTree instead), and then, depending on whether they're using Eclipse or IDEA, they run `gradlew eclipse` or `gradlew idea`, respectively, in the directory they cloned this repository. Then, they point their IDE to the same directory and then import the existing projects (called `vimapi`, `installer` and `example_mod`) in that directory.
## How it works
VIM uses Mojang's LegacyLauncher, aka the LaunchWrapper, which the vanilla launcher uses to 'hack' old versions of Minecraft in a way that is compatible with newer launcher versions. VIM uses the LaunchWrapepr to 'hack' newer versions of Minecraft in a similar sort of way.

The big problem of updating to new Minecraft versions is that Minecraft is obfuscated, which means that every time Mojang release a version of the game, they run it through a program which removes all the meaningful class, method and field names (such as `net.minecraft.client.Minecraft`), and replaces them with meaningless names (such as `abc`) that are not only difficult for humans to read, but which change between Minecraft versions.

VIM solves this issue by analysing the Minecraft JAR before the game starts and looking for common characteristics in class files. For example, no matter the Minecraft version, there will always be a block referred to as `minecraft:stone`, so if a class contains `"stone"`, `"gold_ore"`, `"gravel"`, `"reeds"` and `"cake"`, you can be pretty sure that class is the one representing blocks, no matter what its meaningless name is.

Once VIM has discovered the names of the classes, methods and fields it'll be using, it lets Minecraft run. Though VIM does tweak vanilla classes slightly, it is nothing compared to how it transforms its own classes. At a lower level, VIM classes may contain markers (or annotations) which basically say "hey, transform me, now you have the correct information". In this way, VIM can directly reference classes, methods and fields with variable names without the performance penalty of doing it the usual way, by reflection.
